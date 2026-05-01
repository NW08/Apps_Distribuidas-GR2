package Clase_02.Multithreads;

public class CProcess implements Runnable {
   private static final int LOOP_START = 25;
   private static final int LOOP_END = -2;
   private final int numerator;

   public CProcess(int numerator) {
      this.numerator = numerator;
   }

   @Override
   public void run() {
      StringBuilder result = new StringBuilder("División de ")
            .append(numerator)
            .append(": ");

      for (int i = LOOP_START; i >= LOOP_END; i--) {
         try {
            result.append(numerator / i).append(" ");
         } catch (ArithmeticException e) {
            result.append("[÷0] ");
         }
      }

      ConsoleMonitor.printMessage(result.toString());
   }
}