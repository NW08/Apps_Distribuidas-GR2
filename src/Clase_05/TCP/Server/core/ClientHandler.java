package Clase_05.TCP.Server.core;

import Clase_05.TCP.Server.model.ErrorResponse;
import Clase_05.TCP.Server.model.PunchRequest;
import Clase_05.TCP.Server.model.PunchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class ClientHandler implements Runnable {
   private final Socket socket;
   private final ArrivalRegistryService registryService;
   private final ObjectMapper objectMapper;

   @Override
   public void run() {
      try (socket;
           BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
           OutputStream out = socket.getOutputStream()) {

         String payloadLine = in.readLine();
         log.debug("Received raw payload: {}", payloadLine);

         if (payloadLine != null && !payloadLine.isBlank()) {
            PunchRequest request = objectMapper.readValue(payloadLine, PunchRequest.class);
            PunchResponse response = registryService.processPunch(request);
            sendJsonResponse(out, response);

            log.debug("Successfully processed request for: {}", request.employeeId());
         }
      } catch (Exception e) {
         log.error("Error handling client connection from IP: {}", socket.getInetAddress(), e);
         sendErrorFallback(socket);
      }
   }

   private void sendJsonResponse(OutputStream out, Object responseObj) {
      try {
         byte[] jsonBytes = objectMapper.writeValueAsBytes(responseObj);
         out.write(jsonBytes);
         out.write('\n');
         out.flush();
      } catch (Exception e) {
         log.error("Failed to send JSON response", e);
      }
   }

   private void sendErrorFallback(Socket activeSocket) {
      if (activeSocket == null || activeSocket.isClosed()) return;

      try {
         ErrorResponse errorResponse = new ErrorResponse(false, "Internal Server Error or Invalid Payload");
         sendJsonResponse(activeSocket.getOutputStream(), errorResponse);
      } catch (Exception ex) {
         log.error("Critical failure sending fallback error", ex);
      }
   }
}