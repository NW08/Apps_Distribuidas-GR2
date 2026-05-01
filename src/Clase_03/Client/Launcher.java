package Clase_03.Client;

import Clase_02.Multithreads.FX.JavaFXManager;
import Clase_03.UI.CalculatorApp;

public class Launcher {
   static void main() {
      try {
         JavaFXManager.startup();
         CalculatorApp app = new CalculatorApp();
         app.start();

      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         System.err.println("La inicialización de JavaFX fue interrumpida.");
      }
   }
}