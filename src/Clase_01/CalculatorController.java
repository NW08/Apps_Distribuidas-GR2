package Clase_01;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.function.BinaryOperator;

public class CalculatorController {

   private static final String INVALID_INPUT_MESSAGE = "Please enter valid numbers.";
   private final ICalculatorModel model;

   @FXML
   private TextField inputNumber1;
   @FXML
   private TextField inputNumber2;
   @FXML
   private Label labelResult;

   public CalculatorController(ICalculatorModel model) {
      this.model = model;
   }

   @FXML
   void onAddClick() {
      performOperation(model::add);
   }

   @FXML
   void onSubtractClick() {
      performOperation(model::subtract);
   }

   @FXML
   void onMultiplyClick() {
      performOperation(model::multiply);
   }

   @FXML
   void onDivideClick() {
      performOperation(model::divide);
   }

   @FXML
   void onClearClick() {
      inputNumber1.clear();
      inputNumber2.clear();
      labelResult.setText("0.0");
   }

   /**
    * Parses both inputs, applies the given operation, and displays the result. Centralizes all error handling in one place (DRY).
    */
   private void performOperation(BinaryOperator<Double> operation) {
      try {
         double num1 = Double.parseDouble(inputNumber1.getText());
         double num2 = Double.parseDouble(inputNumber2.getText());
         double result = operation.apply(num1, num2);
         labelResult.setText(String.valueOf(result));
      } catch (NumberFormatException e) {
         labelResult.setText(INVALID_INPUT_MESSAGE);
      } catch (IllegalArgumentException e) {
         // Business rule violations from the model (e.g., division by zero)
         labelResult.setText(e.getMessage());
      }
   }
}