package Clase_01;


/**
 * Implements the four basic arithmetic operations.
 */
public class CalculatorModel implements ICalculatorModel {

   /**
    * Returns the sum of two numbers.
    */
   @Override
   public double add(double a, double b) {
      return a + b;
   }

   /**
    * Returns the difference of two numbers.
    */
   @Override
   public double subtract(double a, double b) {
      return a - b;
   }

   /**
    * Returns the product of two numbers.
    */
   @Override
   public double multiply(double a, double b) {
      return a * b;
   }

   /**
    * Returns the quotient of two numbers.
    *
    * @throws IllegalArgumentException if the divisor is zero.
    */
   @Override
   public double divide(double a, double b) {
      if (b == 0) {
         throw new IllegalArgumentException("Cannot divide by zero.");
      }
      return a / b;
   }
}