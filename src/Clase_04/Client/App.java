package Clase_04.Client;

import Clase_04.UI.UserController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Punto de arranque de la aplicación cliente JavaFX. Carga la vista principal y muestra la ventana.
 */
public class App extends Application {
   static void main(String[] args) {
      launch(args);
   }

   @Override
   public void start(Stage stage) throws Exception {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("../UI/View/VistaMain.fxml"));
      Parent root = loader.load();

      Object controller = loader.getController();
      if (controller instanceof UserController)
         ((UserController) controller).setStage(stage);

      // Configurar la escena
      Scene scene = new Scene(root, 600, 400);

      // Configurar la ventana principal
      stage.setTitle("Sistema de Tarjetas Ciudadanas");
      stage.setScene(scene);
      stage.show();
   }
}