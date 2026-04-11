package Clase_02.Multithreads;

public class EProcess implements Runnable {
   @Override
   public void run() {
      String text = "Hello World!\nThread name: " + Thread.currentThread().getName();
      ConsoleMonitor.printMessage(text);
   }
}