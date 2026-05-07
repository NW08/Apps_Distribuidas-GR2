package Clase_05.Correction.Client.UI.controller;

import Clase_02.Multithreads.FX.JavaFXManager;
import Clase_05.Correction.Client.UI.utils.UserSession;
import Clase_05.Correction.Client.UI.utils.ViewNavigator;
import Clase_05.Correction.Client.UI.utils.ViewRoute;
import Clase_05.Correction.Client.model.User;
import Clase_05.Correction.Client.services.UserService;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class SearchController {
   private final UserService userService;
   private final UserSession userSession;

   @FXML
   private Button btn_newUser;
   @FXML
   private Label lbl_query_result;
   @FXML
   private TextField txt_field_identification;
   @FXML
   private Button btn_search;

   @FXML
   void initialize() {
      userSession.clearSession();
      lbl_query_result.setText("Esperando consulta...");
      txt_field_identification.clear();
   }

   @FXML
   void registerUser() {
      ViewNavigator.getInstance().navigateTo(ViewRoute.CREATE);
   }

   @FXML
   void searchUser() {
      String cedula = txt_field_identification.getText().trim();

      if (!userService.isValidIdentification(cedula)) {
         lbl_query_result.setText("La cédula debe tener 10 dígitos.");
         return;
      }

      lbl_query_result.setText("Buscando en el servidor...");
      setControlsDisabled(true);

      CompletableFuture
            .supplyAsync(() -> userService.searchUser(cedula))
            .thenAccept(result -> JavaFXManager.runOnFxThread(() -> {
               setControlsDisabled(false);

               if (result.isPresent()) {
                  User foundUser = result.get();

                  User enrichedUser = foundUser.toBuilder()
                        .identification(cedula)
                        .build();

                  lbl_query_result.setText(
                        "Usuario encontrado: %s %s".formatted(enrichedUser.getName(), enrichedUser.getLastName()));

                  userSession.setCurrentUser(enrichedUser);
                  getPauseTransition().play();

               } else {
                  lbl_query_result.setText("No existe el usuario o hubo un error de red.");
               }
            }))
            .exceptionally(ex -> {
               log.error("Unexpected error during user search", ex);
               JavaFXManager.runOnFxThread(() -> {
                  setControlsDisabled(false);
                  lbl_query_result.setText("Error inesperado. Revise los logs.");
               });
               return null;
            });
   }

   private PauseTransition getPauseTransition() {
      PauseTransition pause = new PauseTransition(Duration.seconds(2));
      pause.setOnFinished(_ -> {
         lbl_query_result.setText("Esperando consulta...");
         txt_field_identification.clear();

         ViewNavigator.getInstance().navigateTo(ViewRoute.SHOW);
      });
      return pause;
   }

   private void setControlsDisabled(boolean disabled) {
      txt_field_identification.setDisable(disabled);
      btn_search.setDisable(disabled);
      btn_newUser.setDisable(disabled);
   }
}