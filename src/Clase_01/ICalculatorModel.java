package Clase_01;

/**
 * Contract for all calculator operations. Allows the controller to depend on an abstraction, not a concrete class.
 */
public interface ICalculatorModel {
   double add(double a, double b);

   double subtract(double a, double b);

   double multiply(double a, double b);

   double divide(double a, double b);
}