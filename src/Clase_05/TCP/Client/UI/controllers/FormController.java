package Clase_05.TCP.Client.UI.controllers;

import Clase_02.Multithreads.FX.JavaFXManager;
import Clase_05.TCP.Client.model.PunchRequest;
import Clase_05.TCP.Client.services.TCPService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
public class FormController {
   private final TCPService service;

   @FXML
   private TextField employeeIdInput;
   @FXML
   private Button entryBtn;
   @FXML
   private Button exitBtn;
   @FXML
   private Button lunchInBtn;
   @FXML
   private Button lunchOutBtn;
   @FXML
   private Label statusMessageLabel;

   @FXML
   public void initialize() {
      entryBtn.setOnAction(_ -> processPunch("ENTRY"));
      lunchOutBtn.setOnAction(_ -> processPunch("LUNCH_OUT"));
      lunchInBtn.setOnAction(_ -> processPunch("LUNCH_IN"));
      exitBtn.setOnAction(_ -> processPunch("EXIT"));
   }

   private void processPunch(String type) {
      String employeeId = employeeIdInput.getText().trim();

      if (employeeId.isEmpty()) {
         updateStatus("Employee ID cannot be empty", false);
         return;
      }

      disableButtons(true);
      updateStatus("Sending request...", true);

      String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      PunchRequest request = new PunchRequest(employeeId, type, timestamp);

      service.sendPunchAsync(request)
            .whenComplete((response, ex) -> JavaFXManager.runOnFxThread(() -> {
               if (ex != null) {
                  log.error("Failed to process punch operation", ex);
                  updateStatus("Internal Server Error or Communication Failure", false);
               } else updateStatus(response.message(), response.success());
               disableButtons(false);
            }));
   }

   private void updateStatus(String message, boolean isSuccess) {
      statusMessageLabel.setText(message);
      statusMessageLabel.getStyleClass().removeAll("status-success", "status-error");
      statusMessageLabel.getStyleClass().add(isSuccess ? "status-success" : "status-error");
   }


   private void disableButtons(boolean disable) {
      entryBtn.setDisable(disable);
      lunchOutBtn.setDisable(disable);
      lunchInBtn.setDisable(disable);
      exitBtn.setDisable(disable);
   }
}