package Clase_05.Correction.Server;

import Clase_05.Correction.Server.model.User;
import Clase_05.Correction.Server.services.CardService;
import Clase_05.Correction.Server.services.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
public class Server {

   private static final int BUFFER_SIZE = 1024;
   private static final int TIMEOUT_MS = 10000;

   private final CardService cardService;
   private final RegistrationService registrationService;

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

         socket.receive(clientPacket); // bloquea solo mientras espera paquetes

         // Captura inmutable de los datos del paquete antes de cederlos al hilo async
         String receivedData = new String(clientPacket.getData(), 0, clientPacket.getLength(),
               StandardCharsets.UTF_8).trim();
         InetAddress clientAddress = clientPacket.getAddress();
         int clientPort = clientPacket.getPort();

         log.debug("Received payload from {}: {}", clientAddress, receivedData);

         // DB work corre en virtual thread — el loop del servidor no se bloquea
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
         String[] dataParts = payload.split("\\|");
         String operation = dataParts[0].trim().toUpperCase();

         // Switch expression moderno. Retorna directamente el String de respuesta.
         return switch (operation) {

            case "SEARCH" -> {
               // Esperado: SEARCH|userId
               Long userId = Long.parseLong(dataParts[1].trim());
               BigDecimal balance = cardService.getBalance(userId);
               yield "SUCCESS|" + balance.toString();
            }

            case "CREATE" -> {
               // Esperado: CREATE|identification|first_name|last_name|email|phone|YYYY-MM-DD
               validateCreatePayload(dataParts);
               User newUser = User.builder()
                     .identification(dataParts[1].trim())
                     .name(dataParts[2].trim())
                     .lastName(dataParts[3].trim())
                     .email(dataParts[4].trim())
                     .phone(dataParts[5].trim())
                     .birthDate(LocalDate.parse(dataParts[6].trim())) // Requiere formato YYYY-MM-DD
                     .build();

               User registeredUser = registrationService.registerNewUser(newUser);
               yield "SUCCESS|" + registeredUser.getId(); // Devolvemos el ID generado al cliente
            }

            case "RECHARGE" -> {
               // Esperado: RECHARGE|userId|amount
               Long userId = Long.parseLong(dataParts[1].trim());
               BigDecimal amount = new BigDecimal(dataParts[2].trim());
               BigDecimal newBalance = cardService.rechargeCard(userId, amount);
               yield "SUCCESS|" + newBalance.toString();
            }

            case "PAY" -> {
               // Esperado: PAY|userId
               Long userId = Long.parseLong(dataParts[1].trim());
               BigDecimal newBalance = cardService.processPayment(userId);
               yield "SUCCESS|" + newBalance.toString();

            }

            default -> "ERROR|Unknown Operator: " + operation;
         };

      } catch (IndexOutOfBoundsException e) {
         log.error("Malformed payload received: {}", payload);
         return "ERROR|Malformed payload. Missing parameters.";
      } catch (NumberFormatException e) {
         log.error("Invalid numbers in payload: {}", payload);
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
      log.info("Server stop requested.");
   }

   private void validateCreatePayload(String[] parts) {
      if (!parts[4].trim().contains("@"))
         throw new IllegalArgumentException("Invalid email format.");
      if (parts[5].trim().length() < 7)
         throw new IllegalArgumentException("Invalid phone number.");
   }
}