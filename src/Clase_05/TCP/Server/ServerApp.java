package Clase_05.TCP.Server;

import Clase_05.TCP.Server.config.JacksonConfig;
import Clase_05.TCP.Server.core.ArrivalRegistryService;
import Clase_05.TCP.Server.core.ClientHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
public class ServerApp {
   private final int port;
   private final ArrivalRegistryService registryService;
   private final ObjectMapper mapper = JacksonConfig.createMapper();
   private final AtomicBoolean isRunning = new AtomicBoolean(true);

   private volatile ServerSocket activeServerSocket;

   public void start() {
      try (ServerSocket serverSocket = new ServerSocket(port)) {
         this.activeServerSocket = serverSocket;
         log.info("TCP Server Listening on port: {}", port);

         while (isRunning.get()) {
            receiveAndProcess();
         }
      } catch (SocketException e) {
         if (!isRunning.get()) log.info("Server socket closed gracefully.");
         else log.error("Unexpected socket error", e);

      } catch (IOException e) {
         log.error("Failed to start or run TCP Server", e);
      }
   }

   private void receiveAndProcess() throws IOException {
      Socket clientSocket = activeServerSocket.accept();
      log.debug("New client connected from {}", clientSocket.getInetAddress().getHostAddress());
      Thread.startVirtualThread(new ClientHandler(clientSocket, registryService, mapper));
   }

   public void stop() {
      if (isRunning.compareAndSet(true, false)) {
         log.info("Server Stop Requested! Closing socket to break accept lock...");
         try {
            if (activeServerSocket != null && !activeServerSocket.isClosed())
               activeServerSocket.close();
         } catch (IOException e) {
            log.error("Error closing server socket during shutdown", e);
         }
      }
   }
}