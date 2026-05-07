package Clase_05.Correction.Client.services;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class UDPService {

   private static final int TIMEOUT_MS = 5000;
   private static final int BUFFER_SIZE = 4096;

   private static final ExecutorService EXECUTOR =
         Executors.newVirtualThreadPerTaskExecutor();

   public CompletableFuture<String> sendAndReceiveAsync(String serverAddress, int serverPort, String payload) {
      return CompletableFuture.supplyAsync(() -> {
         try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(TIMEOUT_MS);

            InetAddress serverIP = InetAddress.getByName(serverAddress);
            byte[] requestData = payload.getBytes(StandardCharsets.UTF_8);

            DatagramPacket requestPacket =
                  new DatagramPacket(requestData, requestData.length, serverIP, serverPort);
            log.debug("Sending payload to {}:{} → {}", serverAddress, serverPort, payload);
            socket.send(requestPacket);

            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(responsePacket);

            String response = new String(
                  responsePacket.getData(), 0, responsePacket.getLength(), StandardCharsets.UTF_8).trim();
            log.debug("Response received from {}:{} → {}", serverAddress, serverPort, response);
            return response;

         } catch (SocketTimeoutException e) {
            // Known, expected failure — warn level, no stack trace needed
            log.warn("UDP request to {}:{} timed out after {} ms", serverAddress, serverPort, TIMEOUT_MS);
            throw new RuntimeException("Server did not respond within timeout", e);

         } catch (IOException e) {
            log.error("UDP I/O error communicating with {}:{}", serverAddress, serverPort, e);
            throw new RuntimeException("UDP communication failed", e);
         }
      }, EXECUTOR);
   }
}