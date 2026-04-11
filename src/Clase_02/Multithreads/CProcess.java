package Clase_02.Multithreads;

public class CProcess implements Runnable {
   private final int numerator;

   public CProcess(int numerator) {
      this.numerator = numerator;
   }

   @Override
   public void run() {
      StringBuilder result = new StringBuilder();

      for (int i = 25; i >= -2; i--) {
         try {
            result.append(numerator / i).append(" ");
         } catch (ArithmeticException e) {
            result.append("[∞] ");
         }
      }

      ConsoleMonitor.printMessage(result.toString());
   }
}