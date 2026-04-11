package Clase_02.Multithreads;

public class ConsoleMonitor {
   public static synchronized void printSymbols(char symbol, int repetitions) {
      for (int i = 0; i < repetitions; i++) System.out.print(symbol);
      System.out.println();
   }

   public static synchronized void printMessage(String message) {
      System.out.println(message);
   }
}