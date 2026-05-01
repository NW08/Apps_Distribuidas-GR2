package Clase_02.Multithreads;

import Clase_02.Multithreads.FX.JavaFXManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Launcher {
   private static final int THREAD_POOL_SIZE = 4;

   static void main() throws InterruptedException {
      JavaFXManager.startup();

      try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE)) {
         executor.submit(new AProcess());
         executor.submit(new BProcess());
         executor.submit(new CProcess(100));
         executor.submit(new EProcess());
      }

      JavaFXManager.runOnFxThread(() -> new DProcess().run());
      JavaFXManager.runOnFxThread(() -> new FProcess().run());
   }
}