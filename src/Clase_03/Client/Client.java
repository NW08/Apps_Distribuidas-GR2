package Clase_03.Client;

import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class Client {

   private static final int TIMEOUT_MS = 5000;
   private static final int BUFFER_SIZE = 1024;

   public CompletableFuture<String> sendRequestAsync(
         String serverIP,
         int port,
         double number_one,
         double number_two,
         String operation
   ) {
      return CompletableFuture.supplyAsync(() -> {
         try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(TIMEOUT_MS);
            InetAddress serverAddress = InetAddress.getByName(serverIP);

            String requestPayload = String.format("%s,%s,%s", number_one, number_two, operation);
            byte[] requestData = requestPayload.getBytes();
            DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, serverAddress, port);

            log.debug("Sending payload to server: {}", requestPayload);
            socket.send(requestPacket);

            byte[] responseBuffer = new byte[BUFFER_SIZE];
            DatagramPacket incomingPacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(incomingPacket);

            return new String(incomingPacket.getData(), 0, incomingPacket.getLength()).trim();

         } catch (Exception e) {
            log.error("Network error communicating with server: {}", e.getMessage());
            throw new RuntimeException("Connection failed or timed out", e);
         }
      });
   }
}