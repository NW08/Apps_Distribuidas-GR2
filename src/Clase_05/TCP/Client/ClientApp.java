package Clase_05.TCP.Client;

import Clase_02.Multithreads.FX.JavaFXManager;
import Clase_05.TCP.Client.UI.controllers.FormController;
import Clase_05.TCP.Client.services.TCPService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ClientApp {
   private final TCPService service;

   public void start() {
      JavaFXManager.runOnFxThread(() -> {
         try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UI/views/FormView.fxml"));
            loader.setControllerFactory(controllerClass -> {
               if (controllerClass == FormController.class) {
                  return new FormController(service);
               }
               throw new IllegalStateException("Unexpected Controller Class: " + controllerClass);
            });

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Registro de Horarios");
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.setOnCloseRequest(_ -> shutdown());
            stage.show();
         } catch (Exception e) {
            log.error("Critical error starting JavaFX App", e);
            shutdown();
         }
      });
   }

   private void shutdown() {
      log.info("Initiating graceful shutdown...");
      service.close();
      System.exit(0);
   }
}