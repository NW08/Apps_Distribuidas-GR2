package Clase_02.Multithreads.FX;

import javafx.application.Platform;
import lombok.NoArgsConstructor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@NoArgsConstructor
public final class JavaFXManager {
   private static final AtomicBoolean initialized = new AtomicBoolean(false);
   private static final CountDownLatch latch = new CountDownLatch(1);

   public static void startup() throws InterruptedException {
      if (initialized.compareAndSet(false, true)) {
         Platform.startup(latch::countDown);
         latch.await();
      }
   }

   public static void runOnFxThread(Runnable action) {
      if (Platform.isFxApplicationThread()) action.run();
      else Platform.runLater(action);
   }
}