package Clase_02.Multithreads;

import Clase_01.CalculatorApp;
import Clase_02.Multithreads.FX.JavaFXManager;

public class DProcess implements Runnable {
   @Override
   public void run() {
      JavaFXManager.runOnFxThread(CalculatorApp::openWindow);
   }
}