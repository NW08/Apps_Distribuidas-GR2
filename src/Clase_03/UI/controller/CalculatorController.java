package Clase_03.UI.controller;

import Clase_03.Client.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CalculatorController {

   private static final String SERVER_IP = "192.168.1.2";
   private static final int SERVER_PORT = 7542;

   private final Client networkClient;

   @FXML
   private TextField inputNumber1;
   @FXML
   private TextField inputNumber2;
   @FXML
   private Label labelResult;

   @FXML
   void onAddClick() {
      performRemoteOperation("+");
   }

   @FXML
   void onSubtractClick() {
      performRemoteOperation("-");
   }

   @FXML
   void onMultiplyClick() {
      performRemoteOperation("*");
   }

   @FXML
   void onDivideClick() {
      performRemoteOperation("/");
   }

   @FXML
   void onClearClick() {
      inputNumber1.clear();
      inputNumber2.clear();
      labelResult.setText("");
   }

   private void performRemoteOperation(String operation) {
      try {
         double number_one = Double.parseDouble(inputNumber1.getText().trim());
         double number_two = Double.parseDouble(inputNumber2.getText().trim());

         labelResult.setText("Calculando...");

         networkClient.sendRequestAsync(SERVER_IP, SERVER_PORT, number_one, number_two, operation)
               .thenAccept(result -> Platform.runLater(() -> {
                  if (result.startsWith("ERROR")) {
                     labelResult.setText("Server Error");
                     log.warn("Server replied with error: {}", result);
                  } else labelResult.setText(result);
               }))
               .exceptionally(_ -> {
                  Platform.runLater(() -> labelResult.setText("Timeout / Network Error"));
                  return null;
               });

      } catch (NumberFormatException e) {
         labelResult.setText("Número Inválido");
         log.warn("Invalid input from user: {}, {}", inputNumber1.getText(), inputNumber2.getText());
      }
   }
}