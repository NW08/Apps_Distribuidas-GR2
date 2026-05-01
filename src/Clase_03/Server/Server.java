package Clase_03.Server;

import Clase_03.Server.model.Calculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

@Slf4j
@RequiredArgsConstructor
public class Server {

   private static final int BUFFER_SIZE = 1024;
   private static final int TIMEOUT_MS = 10000;

   private final Calculator calculator;
   private boolean isRunning = true;

   public void start(int port) {
      try (DatagramSocket socket = new DatagramSocket(port)) {
         socket.setSoTimeout(TIMEOUT_MS);
         log.info("UDP Server Listening: {}", port);

         while (isRunning) {
            receiveAndProcessPacket(socket);
         }
      } catch (IOException e) {
         log.error("Critical server error. Shutting down.", e);
      }
   }

   private void receiveAndProcessPacket(DatagramSocket socket) {
      try {
         byte[] buffer = new byte[BUFFER_SIZE];
         DatagramPacket clientPacket = new DatagramPacket(buffer, buffer.length);

         socket.receive(clientPacket);

         String receivedData = new String(clientPacket.getData(), 0, clientPacket.getLength()).trim();
         log.debug("Received payload from {}: {}", clientPacket.getAddress(), receivedData);

         String responseMessage = processRequest(receivedData);
         sendResponse(socket, clientPacket, responseMessage);
      } catch (SocketTimeoutException e) {
         log.trace("Waiting for packets...");
      } catch (IOException e) {
         log.warn("Network error while handling client packet: {}", e.getMessage());
      }
   }

   private String processRequest(String payload) {
      try {
         String[] dataParts = payload.split(",");
         if (dataParts.length != 3) return "ERROR: Invalid Format";

         double numberOne = Double.parseDouble(dataParts[0].trim());
         double numberTwo = Double.parseDouble(dataParts[1].trim());
         String operation = dataParts[2].trim();

         double result = switch (operation) {
            case "+" -> calculator.add(numberOne, numberTwo);
            case "-" -> calculator.subtract(numberOne, numberTwo);
            case "*" -> calculator.multiply(numberOne, numberTwo);
            case "/" -> calculator.divide(numberOne, numberTwo);
            default -> throw new UnsupportedOperationException("Unknown Operator: " + operation);
         };

         return String.valueOf(result);

      } catch (NumberFormatException e) {
         return "ERROR: Invalid Numbers Provided";
      } catch (IllegalArgumentException | UnsupportedOperationException e) {
         return "ERROR: " + e.getMessage();
      } catch (Exception e) {
         log.error("Unexpected error processing payload: {}", payload, e);
         return "ERROR: Internal Server Error";
      }
   }

   private void sendResponse(DatagramSocket socket, DatagramPacket clientPacket, String responseMessage) throws IOException {
      byte[] responseData = responseMessage.getBytes();
      DatagramPacket responsePacket = new DatagramPacket(
            responseData,
            responseData.length,
            clientPacket.getAddress(),
            clientPacket.getPort()
      );
      socket.send(responsePacket);
   }

   public void stop() {
      this.isRunning = false;
      log.info("Server Stop Requested!");
   }
}