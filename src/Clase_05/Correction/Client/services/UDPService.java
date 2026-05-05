package Clase_05.Correction.Client.services;

import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class UDPService {
   private static final int TIMEOUT_MS = 5000;
   private static final int BUFFER_SIZE = 2048;

   public CompletableFuture<String> sendAndReceiveAsync(String serverAddress, int serverPort, String payload) {
      return CompletableFuture.supplyAsync(() -> {
         try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(TIMEOUT_MS);
            InetAddress serverIP = InetAddress.getByName(serverAddress);

            byte[] requestData = payload.getBytes(StandardCharsets.UTF_8);
            DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, serverIP, serverPort);
            log.debug("Enviando payload al servidor: {}", payload);
            socket.send(requestPacket);

            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(responsePacket);

            String responseData = new String(responsePacket.getData(), 0, responsePacket.getLength(),
                  StandardCharsets.UTF_8).trim();
            log.debug("Respuesta Recibida: {}", responseData);
            return responseData;

         } catch (Exception e) {
            log.error("Error en la comunicación UDP: {}", e.getMessage());
            throw new RuntimeException("Fallo en la comunicación con el servidor", e);
         }
      });
   }
}