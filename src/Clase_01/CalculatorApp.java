package Clase_01;

import Clase_01.controller.CalculatorController;
import Clase_01.model.CalculatorModel;
import Clase_01.model.ICalculatorModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class CalculatorApp extends Application {
   public static void launchApp(String[] args) {
      launch(args);
   }

   @Override
   public void start(Stage stage) throws Exception {
      ICalculatorModel model = new CalculatorModel();
      var loader = new FXMLLoader(getClass().getResource("view/CalculatorView.fxml"));

      loader.setControllerFactory(controllerClass -> {
         if (controllerClass == CalculatorController.class)
            return new CalculatorController(model);
         throw new IllegalStateException(
               "Unexpected Controller Class: " + controllerClass
         );
      });

      Parent root = loader.load();
      var scene = new Scene(root);
      stage.setTitle("Calculadora Básica");
      stage.setScene(scene);
      stage.setResizable(false);
      stage.show();
   }
}
