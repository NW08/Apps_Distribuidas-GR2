package Clase_05.Correction.Client.UI.controller;

import Clase_05.Correction.Client.UI.utils.UserSession;
import Clase_05.Correction.Client.UI.utils.ViewNavigator;
import Clase_05.Correction.Client.UI.utils.ViewRoute;
import Clase_05.Correction.Client.model.User;
import Clase_05.Correction.Client.services.UserService;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SearchViewController {
   private final UserService userService;
   private final UserSession userSession;

   @FXML
   private Label lbl_query_result;

   @FXML
   private TextField txt_field_identification;

   @FXML
   void init() {
      lbl_query_result.setText("Esperando consulta...");
      txt_field_identification.clear();
   }

   @FXML
   void registerUser() {
      ViewNavigator.getInstance().navigateTo(ViewRoute.CREATE);
   }

   @FXML
   void searchUser() {
      String cedula = txt_field_identification.getText();

      if (!userService.isValidIdentification(cedula)) {
         lbl_query_result.setText("La cédula debe tener 10 dígitos.");
         return;
      }

      User foundUser = userService.searchUser(cedula);

      if (foundUser != null) {
         lbl_query_result.setText(String.format("Usuario encontrado: %s %s", foundUser.getName(), foundUser.getLastName()));
         userSession.setCurrentUser(foundUser);

         PauseTransition pause = new PauseTransition(Duration.seconds(2));
         pause.setOnFinished(e -> {
            lbl_query_result.setText("Esperando consulta...");
            ViewNavigator.getInstance().navigateTo(ViewRoute.SHOW);
         });
         pause.play();
      } else lbl_query_result.setText("No existe el usuario o hubo un error de red.");

   }
}