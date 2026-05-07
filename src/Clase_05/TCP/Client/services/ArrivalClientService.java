package Clase_05.TCP.Client.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@Slf4j
@RequiredArgsConstructor
public class ArrivalClientService implements IArrivalClientService {
   private final String serverIp;
   private final int serverPort;

   @Override
   public String registerArrival(String name) {
      try (Socket socket = new Socket(serverIp, serverPort);
           var outputStream = new DataOutputStream(socket.getOutputStream());
           var inputStream = new DataInputStream(socket.getInputStream())) {

         log.debug("Connected to server at {}:{}", serverIp, serverPort);

         outputStream.writeUTF(name);
         outputStream.flush();

         String response = inputStream.readUTF();
         log.debug("Received response from server: {}", response);

         return response;

      } catch (IOException e) {
         log.error("Failed to communicate with the registry server at {}:{}", serverIp, serverPort, e);
         throw new RuntimeException("Communication failure with registry server", e);
      }
   }
}