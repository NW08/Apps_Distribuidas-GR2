package Clase_02.Multithreads;

import Clase_02.Multithreads.FX.CarApp;

public class FProcess implements Runnable {
   @Override
   public void run() {
      CarApp.launchApp(null);
   }
}
