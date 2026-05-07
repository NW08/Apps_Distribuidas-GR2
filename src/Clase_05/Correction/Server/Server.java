package Clase_05.Correction.Server;

import Clase_05.Correction.Server.services.CardService;
import Clase_05.Correction.Server.services.Command;
import Clase_05.Correction.Server.services.RegistrationService;
import Clase_05.Correction.Server.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeParseException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
public class Server {
   private static final int BUFFER_SIZE = 1024;
   private static final int TIMEOUT_MS = 10000;

   private final CardService cardService;
   private final RegistrationService registrationService;
   private final UserService userService;

   private final AtomicBoolean isRunning = new AtomicBoolean(true);
   private final ExecutorService dbExecutor = Executors.newVirtualThreadPerTaskExecutor();

   public void start(int port) {
      try (DatagramSocket socket = new DatagramSocket(port)) {
         socket.setSoTimeout(TIMEOUT_MS);
         log.info("UDP Server listening on port: {}", port);

         while (isRunning.get()) {
            receiveAndProcessPacket(socket);
         }
      } catch (IOException e) {
         log.error("Critical server error. Shutting down.", e);
      } finally {
         dbExecutor.shutdown();
      }
   }

   private void receiveAndProcessPacket(DatagramSocket socket) {
      try {
         byte[] buffer = new byte[BUFFER_SIZE];
         DatagramPacket clientPacket = new DatagramPacket(buffer, buffer.length);

         socket.receive(clientPacket);

         String receivedData = new String(clientPacket.getData(), 0, clientPacket.getLength(),
               StandardCharsets.UTF_8).trim();
         InetAddress clientAddress = clientPacket.getAddress();
         int clientPort = clientPacket.getPort();

         log.debug("Received payload from {}: {}", clientAddress, receivedData);

         CompletableFuture
               .supplyAsync(() -> processRequest(receivedData), dbExecutor)
               .thenAccept(response -> {
                  try {
                     sendResponse(socket, clientAddress, clientPort, response);
                  } catch (IOException e) {
                     log.warn("Failed to send response to {}: {}", clientAddress, e.getMessage());
                  }
               })
               .exceptionally(ex -> {
                  log.error("Unhandled async error for payload [{}]: {}", receivedData, ex.getMessage(), ex);
                  return null;
               });

      } catch (SocketTimeoutException e) {
         log.trace("Waiting for packets...");
      } catch (IOException e) {
         log.warn("Network error while receiving packet: {}", e.getMessage());
      }
   }

   private String processRequest(String payload) {
      try {
         String[] parts = payload.split("\\|");
         String operation = parts[0].trim();

         return Command.from(operation)
               .map(cmd -> cmd.execute(parts, cardService, registrationService, userService))
               .orElse("ERROR|Unknown command: " + operation);

      } catch (IndexOutOfBoundsException e) {
         log.error("Malformed payload received: {}", payload);
         return "ERROR|Malformed payload. Missing parameters.";
      } catch (NumberFormatException e) {
         log.error("Invalid number in payload: {}", payload);
         return "ERROR|Invalid number format provided.";
      } catch (DateTimeParseException e) {
         log.error("Invalid date format in payload: {}", payload);
         return "ERROR|Invalid date. Use YYYY-MM-DD.";
      } catch (IllegalArgumentException e) {
         log.warn("Business logic error: {}", e.getMessage());
         return "ERROR|" + e.getMessage();
      } catch (Exception e) {
         log.error("Unexpected error processing payload: {}", payload, e);
         return "ERROR|Internal Server Error";
      }
   }

   private void sendResponse(DatagramSocket socket, InetAddress address, int port, String message) throws IOException {
      byte[] data = message.getBytes(StandardCharsets.UTF_8);
      socket.send(new DatagramPacket(data, data.length, address, port));
      log.debug("Sent response to {}: {}", address, message);
   }

   public void stop() {
      isRunning.compareAndSet(true, false);
      dbExecutor.shutdown();
      try {
         if (!dbExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
            log.warn("Some tasks did not complete within the shutdown window.");
            dbExecutor.shutdownNow();
         }
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         dbExecutor.shutdownNow();
      }
      log.info("Server Stopped.");
   }


}