package Clase_03.Client;

import lombok.extern.slf4j.Slf4j;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class Client {

   private static final int TIMEOUT_MS = 5000;
   private static final int BUFFER_SIZE = 1024;

   public CompletableFuture<String> sendRequestAsync(
         String serverIP,
         int port,
         double numberOne,
         double numberTwo,
         String operation
   ) {
      return CompletableFuture.supplyAsync(() -> {
         try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(TIMEOUT_MS);
            InetAddress serverAddress = InetAddress.getByName(serverIP);

            String requestPayload = String.format("%s,%s,%s", numberOne, numberTwo, operation);
            byte[] requestData = requestPayload.getBytes(StandardCharsets.UTF_8);
            DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, serverAddress, port);

            log.debug("Sending payload to server: {}", requestPayload);
            socket.send(requestPacket);

            byte[] responseBuffer = new byte[BUFFER_SIZE];
            DatagramPacket incomingPacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(incomingPacket);

            return new String(incomingPacket.getData(), 0, incomingPacket.getLength()).trim();

         } catch (SocketTimeoutException e) {
            log.trace("Waiting for packets...");
            return "ERROR: Timed out";

         } catch (UnknownHostException e) {
            log.error("Unknown server IP: {}", serverIP);
            throw new RuntimeException("Invalid server IP", e);

         } catch (Exception e) {
            log.error("Network error communicating with server: {}", e.getMessage());
            throw new RuntimeException("Connection failed or timed out", e);
         }
      });
   }
}