package Clase_02.Multithreads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Launcher {
   private static final int THREAD_POOL_SIZE = 5;
   private static final long TIMEOUT_SECONDS = 30;

   static void main() throws InterruptedException {

      try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE)) {

         executor.submit(new AProcess());
         executor.submit(new BProcess());
         executor.submit(new CProcess(100));
         executor.submit(new DProcess());
         executor.submit(new EProcess());


         boolean finished = executor.awaitTermination(TIMEOUT_SECONDS, TimeUnit.SECONDS);
         if (!finished) executor.shutdownNow();

      }
   }
}