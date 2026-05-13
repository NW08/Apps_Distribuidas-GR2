package Clase_05.TCP.Client.services;

import Clase_05.TCP.Client.model.PunchRequest;
import Clase_05.TCP.Client.model.PunchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
public class TCPService implements AutoCloseable {
   private static final int TIMEOUT_MS = 5000;

   private final String serverIp;
   private final int serverPort;
   private final ObjectMapper mapper;
   private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

   public CompletableFuture<PunchResponse> sendPunchAsync(PunchRequest request) {
      return CompletableFuture.supplyAsync(() -> {
         try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(serverIp, serverPort), TIMEOUT_MS);

            socket.setSoTimeout(TIMEOUT_MS);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                  StandardCharsets.UTF_8));
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),
                       StandardCharsets.UTF_8))) {

               String jsonPayload = mapper.writeValueAsString(request);
               out.write(jsonPayload + "\n");
               out.flush();

               String responseLine = in.readLine();
               if (responseLine == null) throw new IOException("Server dropped connection unexpectedly without sending data.");

               return mapper.readValue(responseLine, PunchResponse.class);
            }

         } catch (Exception e) {
            log.error("TCP Communication failure", e);
            throw new CompletionException("TCP Communication failure", e);
         }
      }, virtualThreadExecutor);
   }

   @Override
   public void close() {
      virtualThreadExecutor.shutdown();
      log.info("TCPService Closed Gracefully!");
   }
}