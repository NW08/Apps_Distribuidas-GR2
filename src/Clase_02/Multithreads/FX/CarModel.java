package Clase_02.Multithreads.FX;

public class CarModel implements ICarModel {
   @Override
   public void saveCar(Car car) throws InterruptedException {
      Thread.sleep(1500);
      System.out.println("Vehículo guardado en la base de datos: " + car);
   }
}