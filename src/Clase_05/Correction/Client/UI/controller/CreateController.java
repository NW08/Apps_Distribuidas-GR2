package Clase_05.Correction.Client.UI.controller;

import Clase_02.Multithreads.FX.JavaFXManager;
import Clase_05.Correction.Client.UI.utils.ViewNavigator;
import Clase_05.Correction.Client.UI.utils.ViewRoute;
import Clase_05.Correction.Client.model.User;
import Clase_05.Correction.Client.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class CreateController {
   private final UserService userService;

   @FXML
   private DatePicker date_picker_birth;
   @FXML
   private Label lbl_result_query;
   @FXML
   private TextField txt_field_identification;
   @FXML
   private TextField txt_field_lastname;
   @FXML
   private TextField txt_field_mail;
   @FXML
   private TextField txt_field_name;
   @FXML
   private TextField txt_field_phone;
   @FXML
   private Button btn_create;

   @FXML
   void createUser() {
      User tempUser = User.builder()
            .identification(txt_field_identification.getText().trim())
            .name(txt_field_name.getText().trim())
            .lastName(txt_field_lastname.getText().trim())
            .email(txt_field_mail.getText().trim())
            .phone(txt_field_phone.getText().trim())
            .birthday(date_picker_birth.getValue())
            .build();

      btn_create.setDisable(true);
      lbl_result_query.setText("Registrando...");

      CompletableFuture
            .supplyAsync(() -> userService.processRegistration(tempUser))
            .thenAccept(result -> JavaFXManager.runOnFxThread(() -> {
               btn_create.setDisable(false);
               lbl_result_query.setText(result
                     .map(id -> "Usuario registrado con ID: " + id)
                     .orElse("Error: No se pudo registrar el usuario."));
            }))
            .exceptionally(ex -> {
               log.error("Unexpected error during registration", ex);
               JavaFXManager.runOnFxThread(() -> {
                  btn_create.setDisable(false);
                  lbl_result_query.setText("Error inesperado. Revise los logs.");
               });
               return null;
            });
   }

   @FXML
   void cancelOperation() {
      ViewNavigator.getInstance().navigateTo(ViewRoute.SEARCH);
   }
}