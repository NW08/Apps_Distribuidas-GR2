package Clase_05.Correction.Client.UI.controller;

import Clase_02.Multithreads.FX.JavaFXManager;
import Clase_05.Correction.Client.UI.utils.UserSession;
import Clase_05.Correction.Client.UI.utils.ViewNavigator;
import Clase_05.Correction.Client.UI.utils.ViewRoute;
import Clase_05.Correction.Client.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

@Slf4j
@RequiredArgsConstructor
public class ShowController {
   private final UserService userService;
   private final UserSession userSession;

   @FXML
   private Button btn_pay;
   @FXML
   private Button btn_recharge;
   @FXML
   private Label lbl_balance;
   @FXML
   private Label lbl_fullname;
   @FXML
   private Label lbl_identification;
   @FXML
   private Label lbl_preferred;

   /**
    * Allows only valid monetary input: digits with up to two decimal places. Returning {@code null} from the operator rejects the change —
    * correct JavaFX API usage.
    */
   private static UnaryOperator<TextFormatter.Change> moneyInputFilter() {
      String moneyRegex = "^[0-9]*(\\.?[0-9]{0,2})?$";
      return change -> change.getControlNewText().matches(moneyRegex) ? change : null;
   }

   @FXML
   void initialize() {
      userSession.currentUserProperty().addListener((_, _, newUser) -> {
         if (newUser == null) return;

         lbl_identification.setText(newUser.getIdentification());
         lbl_fullname.setText("%s %s".formatted(newUser.getName(), newUser.getLastName()));

         lbl_preferred.setText(newUser.isPreferred() ? "PREFERENCIAL" : "NORMAL");

         String balance = newUser.getCard() != null
               ? newUser.getCard().getBalance().toPlainString()
               : "0.00";
         lbl_balance.setText(balance);
      });
   }

   @FXML
   void payTicket() {
      String userId = String.valueOf(userSession.getCurrentUser().getId());

      btn_pay.setDisable(true);
      CompletableFuture
            .supplyAsync(() -> userService.processPayment(userId))
            .thenAccept(newBalance -> JavaFXManager.runOnFxThread(() -> {
               btn_pay.setDisable(false);
               lbl_balance.setText(newBalance
                     .map(BigDecimal::toPlainString)
                     .orElse("Error al procesar pago"));
            }))
            .exceptionally(ex -> {
               log.error("Unexpected error during payment", ex);
               JavaFXManager.runOnFxThread(() -> {
                  btn_pay.setDisable(false);
                  lbl_balance.setText("Error inesperado");
               });
               return null;
            });
   }

   @FXML
   void rechargeCard() {
      Optional<BigDecimal> validatedAmount = showRechargeDialog();
      if (validatedAmount.isEmpty()) return;

      BigDecimal amount = validatedAmount.get();
      String userId = String.valueOf(userSession.getCurrentUser().getId());

      btn_recharge.setDisable(true);
      CompletableFuture
            .supplyAsync(() -> userService.processRecharge(userId, amount))
            .thenAccept(newValue -> JavaFXManager.runOnFxThread(() -> {
               btn_recharge.setDisable(false);
               lbl_balance.setText(newValue
                     .map(BigDecimal::toPlainString)
                     .orElse("Error al recargar"));
            }))
            .exceptionally(ex -> {
               log.error("Unexpected error during recharge", ex);
               JavaFXManager.runOnFxThread(() -> {
                  btn_recharge.setDisable(false);
                  lbl_balance.setText("Error inesperado");
               });
               return null;
            });
   }

   /**
    * Shows the recharge dialog and returns the validated amount or empty if the user canceled or entered an invalid value. Safe to call on
    * the FX thread — showAndWait() is a blocking modal.
    */
   private Optional<BigDecimal> showRechargeDialog() {
      TextInputDialog dialog = new TextInputDialog();
      dialog.setTitle("Recarga de Saldo");
      dialog.setHeaderText("Monto a recargar en la tarjeta");
      dialog.setContentText("Valor ($):");

      DialogPane dialogPane = dialog.getDialogPane();

      Optional.ofNullable(getClass().getResource("/Clase_05/Correction/Client/UI/view/styles.css"))
            .map(URL::toExternalForm)
            .ifPresentOrElse(
                  dialogPane.getStylesheets()::add,
                  () -> log.warn("'styles.css' is missing")
            );

      dialogPane.getStyleClass().add("content-card");

      dialog.getEditor().setTextFormatter(new TextFormatter<>(moneyInputFilter()));

      return dialog.showAndWait()
            .filter(s -> !s.isBlank() && !s.equals("."))
            .map(String::trim)
            .map(BigDecimal::new)
            .filter(amount -> amount.compareTo(BigDecimal.ZERO) > 0);
   }

   @FXML
   void goBack() {
      userSession.clearSession();
      ViewNavigator.getInstance().navigateTo(ViewRoute.SEARCH);
   }
}