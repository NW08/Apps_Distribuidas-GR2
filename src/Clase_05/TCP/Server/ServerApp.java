package Clase_05.TCP.Server;

import Clase_05.TCP.Server.model.ClientHandler;
import Clase_05.TCP.Server.services.ArrivalRegistryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
@RequiredArgsConstructor
public class ServerApp {

   private final int port;
   private final ArrivalRegistryService registryService;

   public void start() {
      try (var serverSocket = new ServerSocket(port)) {
         log.info("TCP Server listening on port {}", port);

         while (!serverSocket.isClosed()) {
            Socket clientSocket = serverSocket.accept();
            log.debug("New client connected from {}", clientSocket.getInetAddress().getHostAddress());

            Thread.startVirtualThread(new ClientHandler(clientSocket, registryService));
         }
      } catch (IOException e) {
         log.error("Failed to start or run TCP Server on port {}", port, e);
      }
   }
}