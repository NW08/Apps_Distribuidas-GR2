package Clase_05.Correction.Client.UI.controller;

import Clase_05.Correction.Client.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateViewController {
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
   void createUser() {

   }

}
