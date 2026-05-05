package Clase_05.Correction.Client.UI.controller;

import Clase_05.Correction.Client.UI.utils.UserSession;
import Clase_05.Correction.Client.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ShowViewController {
   private final UserService userService;
   private final UserSession userSession;

   @FXML
   private Label lbl_balance;

   @FXML
   private Label lbl_fullname;

   @FXML
   private Label lbl_identification;

   @FXML
   private Label lbl_mail;

   @FXML
   private Label lbl_phone;

   @FXML
   private Label lbl_preferred;

   @FXML
   public void initialize() {
      userSession.currentUserProperty().addListener((observable, oldUser, newUser) -> {
         if (newUser != null) {
            lbl_identification.setText(newUser.getIdentification());
            lbl_fullname.setText(newUser.getName() + " " + newUser.getLastName());
            lbl_balance.setText("$ " + newUser.getBalance());
         }
      });
   }

   @FXML
   void payTicket(ActionEvent event) {

   }

   @FXML
   void rechargeCard(ActionEvent event) {

   }

}
