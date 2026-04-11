package Clase_02.Multithreads;

import Clase_01.CalculatorApp;

public class DProcess implements Runnable {
   @Override
   public void run() {
      CalculatorApp.launchApp(null);
   }
}