package Clase_02.Multithreads.FX;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CarApp extends Application {
   public static void launchApp(String[] args) {
      launch(args);
   }

   @Override
   public void start(Stage stage) throws Exception {
      ICarModel model = new CarModel();
      var loader = new FXMLLoader(getClass().getResource("CarView.fxml"));

      loader.setControllerFactory(controllerClass -> {
         if (controllerClass == CarController.class)
            return new CarController(model);
         throw new IllegalStateException(
               "Unexpected Controller Class: " + controllerClass
         );
      });

      Parent root = loader.load();
      CarController controller = loader.getController();

      var scene = new Scene(root);
      stage.setTitle("App EsfotCar");
      stage.setScene(scene);
      stage.setResizable(false);

      stage.setOnCloseRequest(_ -> controller.shutdown());

      stage.show();
   }
}
