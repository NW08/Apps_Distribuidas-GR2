package Clase_05.Correction.Client;

import Clase_02.Multithreads.FX.JavaFXManager;
import Clase_05.Correction.Client.UI.controller.CreateController;
import Clase_05.Correction.Client.UI.controller.SearchController;
import Clase_05.Correction.Client.UI.controller.ShowController;
import Clase_05.Correction.Client.UI.utils.UserSession;
import Clase_05.Correction.Client.UI.utils.ViewNavigator;
import Clase_05.Correction.Client.UI.utils.ViewRoute;
import Clase_05.Correction.Client.services.UDPService;
import Clase_05.Correction.Client.services.UserService;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientApp {
   private static final String SERVER_IP = "192.168.56.1";
   private static final int SERVER_PORT = 7541;
   private static final UDPService service = new UDPService();

   private static Callback<Class<?>, Object> getClassObjectCallback() {
      UserService userService = new UserService(SERVER_IP, SERVER_PORT, service);
      UserSession userSession = new UserSession();

      return controllerClass -> {
         if (controllerClass == SearchController.class) return new SearchController(userService, userSession);
         if (controllerClass == ShowController.class) return new ShowController(userService, userSession);
         if (controllerClass == CreateController.class) return new CreateController(userService);

         try {
            return controllerClass.getDeclaredConstructor().newInstance();
         } catch (Exception e) {
            throw new IllegalStateException("No se pudo crear el controlador: " + controllerClass, e);
         }
      };
   }

   public void start() {
      JavaFXManager.runOnFxThread(() -> {
         try {
            Stage stage = new Stage();
            stage.setTitle("Sistema Metropolitano de Transporte");
            stage.setResizable(false);
            stage.setOnCloseRequest(_ -> System.exit(0));

            stage.setScene(new Scene(new Pane(), 600, 600));

            final Callback<Class<?>, Object> controllerFactory = getClassObjectCallback();

            ViewNavigator navigator = ViewNavigator.getInstance();
            navigator.init(stage, controllerFactory);
            navigator.preloadViews(ViewRoute.CREATE, ViewRoute.SHOW);
            navigator.navigateTo(ViewRoute.SEARCH);

         } catch (Exception e) {
            log.error("Error crítico iniciando la aplicación JavaFX", e);
         }
      });
   }
}