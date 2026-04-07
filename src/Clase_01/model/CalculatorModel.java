package Clase_01.model;

public class CalculatorModel implements ICalculatorModel {

   @Override
   public double add(double a, double b) {
      return a + b;
   }

   @Override
   public double subtract(double a, double b) {
      return a - b;
   }

   @Override
   public double multiply(double a, double b) {
      return a * b;
   }

   @Override
   public double divide(double a, double b) {
      if (b == 0) throw new IllegalArgumentException("División entre 0!!");
      return a / b;
   }
}
