package Clase_05.TCP.Server.model;

import Clase_05.TCP.Server.services.ArrivalRegistryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@Slf4j
@RequiredArgsConstructor
public class ClientHandler implements Runnable {

   private final Socket socket;
   private final ArrivalRegistryService registryService;

   @Override
   public void run() {
      try (socket;
           var inputStream = new DataInputStream(socket.getInputStream());
           var outputStream = new DataOutputStream(socket.getOutputStream())) {

         String clientName = inputStream.readUTF();
         log.debug("Received request from: {}", clientName);

         String response = registryService.register(clientName);

         outputStream.writeUTF(response);
         outputStream.flush();

         log.debug("Successfully responded to: {}", clientName);

      } catch (IOException e) {
         log.error("I/O error while handling client connection", e);
      }
   }
}