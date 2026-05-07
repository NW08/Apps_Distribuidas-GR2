package Clase_05.Correction.Client;

import Clase_02.Multithreads.FX.JavaFXManager;

public class Launcher {
   void main() {
      try {
         JavaFXManager.startup();
         ClientApp app = new ClientApp();
         app.start();

      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         System.err.println("La inicialización de JavaFX fue interrumpida.");
      }
   }
}
