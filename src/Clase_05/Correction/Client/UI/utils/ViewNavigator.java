package Clase_05.Correction.Client.UI.utils;

import Clase_02.Multithreads.FX.JavaFXManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ViewNavigator {

   private final Map<ViewRoute, Parent> viewCache = new ConcurrentHashMap<>();
   private Stage primaryStage;

   private Callback<Class<?>, Object> controllerFactory;

   public static ViewNavigator getInstance() {
      return Holder.INSTANCE;
   }

   public void init(Stage stage, Callback<Class<?>, Object> controllerFactory) {
      this.primaryStage = stage;
      this.controllerFactory = controllerFactory;
   }


   /**
    * Schedules eager loading of the given routes on the FX thread.
    * <p>
    * There is no point in dispatching to a background thread here: {@link javafx.fxml.FXMLLoader} instantiates JavaFX nodes and MUST run on
    * the FX thread. A virtual thread would only serve as a passthrough to {@code runOnFxThread} and terminate immediately — pure overhead.
    */
   public void preloadViews(ViewRoute... routes) {
      for (ViewRoute route : routes)
         JavaFXManager.runOnFxThread(() -> loadAndCache(route));
   }

   public void navigateTo(ViewRoute route) {
      if (primaryStage == null || controllerFactory == null)
         throw new IllegalStateException("ViewNavigator has not been initialized. Call init() first.");

      JavaFXManager.runOnFxThread(() -> {
         Parent cached = viewCache.get(route);
         if (cached != null) {
            renderView(cached);
            log.info("Navigated to {} from cache", route.name());
            return;
         }

         try {
            Parent view = loadViewFromDisk(route);
            viewCache.put(route, view);
            renderView(view);
            log.info("Navigated to {} (loaded from disk)", route.name());
         } catch (Exception e) {
            log.error("Failed to navigate to {}", route.name(), e);
         }
      });
   }

   private void loadAndCache(ViewRoute route) {
      if (viewCache.containsKey(route)) return;
      try {
         Parent view = loadViewFromDisk(route);
         viewCache.put(route, view);
         log.debug("Preloaded view into cache: {}", route.name());
      } catch (Exception e) {
         log.error("Failed to preload view: {}", route.name(), e);
      }
   }


   private Parent loadViewFromDisk(ViewRoute route) throws IOException {
      URL fxmlUrl = getClass().getResource(route.getFxmlPath());
      if (fxmlUrl == null)
         throw new IllegalArgumentException("FXML file not found: " + route.getFxmlPath());

      FXMLLoader loader = new FXMLLoader(fxmlUrl);
      loader.setControllerFactory(controllerFactory);
      return loader.load();
   }

   private void renderView(Parent view) {
      Scene currentScene = primaryStage.getScene();
      if (currentScene == null) primaryStage.setScene(new Scene(view));
      else currentScene.setRoot(view);
      primaryStage.show();
   }

   private static final class Holder {
      private static final ViewNavigator INSTANCE = new ViewNavigator();
   }
}