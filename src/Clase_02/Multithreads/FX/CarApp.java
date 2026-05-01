package Clase_02.Multithreads.FX;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CarApp {

   public static void openWindow() {
      JavaFXManager.runOnFxThread(() -> {
         try {
            ICarModel model = new CarModel();
            var loader = new FXMLLoader(CarApp.class.getResource("CarView.fxml"));

            loader.setControllerFactory(cls -> {
               if (cls == CarController.class) return new CarController(model);
               throw new IllegalStateException("Unexpected controller: " + cls);
            });

            Parent root = loader.load();
            CarController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("App EsfotCar");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setOnCloseRequest(_ -> controller.shutdown());
            stage.show();

         } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error al abrir CarApp", e);
         }
      });
   }
}