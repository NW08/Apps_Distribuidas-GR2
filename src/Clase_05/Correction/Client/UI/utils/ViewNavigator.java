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

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ViewNavigator {
   private static ViewNavigator instance;
   private final Map<ViewRoute, Parent> viewCache = new ConcurrentHashMap<>();
   private final ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
   private Stage primaryStage;
   private Callback<Class<?>, Object> controllerFactory;

   public static synchronized ViewNavigator getInstance() {
      if (instance == null) instance = new ViewNavigator();
      return instance;
   }

   public void init(Stage stage, Callback<Class<?>, Object> controllerFactory) {
      this.primaryStage = stage;
      this.controllerFactory = controllerFactory;
   }

   public void preloadViews(ViewRoute... routes) {
      for (ViewRoute route : routes)
         virtualExecutor.submit(() -> loadAndCacheView(route));
   }

   public void navigateTo(ViewRoute route) {
      if (primaryStage == null || controllerFactory == null)
         throw new IllegalStateException("ViewNavigator no ha sido inicializado. Llama a init() primero.");


      JavaFXManager.runOnFxThread(() -> {
         if (viewCache.containsKey(route)) {
            renderView(viewCache.get(route));
            log.info("Navegación desde caché a: {}", route.name());
            return;
         }

         try {
            Parent view = loadViewFromDisk(route);
            viewCache.put(route, view);
            renderView(view);
            log.info("Navegación con carga en caliente a: {}", route.name());
         } catch (Exception e) {
            log.error("Error al navegar a la vista: {}", route.name(), e);
         }
      });
   }

   private void loadAndCacheView(ViewRoute route) {
      JavaFXManager.runOnFxThread(() -> {
         try {
            if (!viewCache.containsKey(route)) {
               Parent view = loadViewFromDisk(route);
               viewCache.put(route, view);
               log.debug("Vista precargada en caché con éxito: {}", route.name());
            }
         } catch (Exception e) {
            log.error("Fallo al precargar la vista: {}", route.name(), e);
         }
      });
   }

   private Parent loadViewFromDisk(ViewRoute route) throws Exception {
      URL fxmlUrl = getClass().getResource(route.getFxmlPath());
      if (fxmlUrl == null) throw new IllegalArgumentException("No se encontró el archivo FXML: " + route.getFxmlPath());

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
}