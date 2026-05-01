package Clase_03.UI;

import Clase_02.Multithreads.FX.JavaFXManager;
import Clase_03.Client.Client;
import Clase_03.UI.controller.CalculatorController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CalculatorApp {

   public void start() {
      JavaFXManager.runOnFxThread(() -> {
         try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CalculatorView.fxml"));

            Client udpClient = new Client();

            loader.setControllerFactory(controllerClass -> {
               if (controllerClass == CalculatorController.class)
                  return new CalculatorController(udpClient);
               throw new IllegalStateException("Unexpected Controller Class: " + controllerClass);
            });

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Calculadora UDP - Cliente");
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.setOnCloseRequest(_ -> System.exit(0));
            stage.show();
         } catch (Exception e) {
            log.error("Error launching JavaFX application", e);
         }
      });
   }
}