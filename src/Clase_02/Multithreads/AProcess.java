package Clase_02.Multithreads;

public class AProcess implements Runnable, Printable {
   private static final int REPETITIONS = 150;
   private static final char SYMBOL = '#';

   @Override
   public void printOutput() {
      ConsoleMonitor.printSymbols(SYMBOL, REPETITIONS);
   }

   @Override
   public void run() {
      printOutput();
   }
}