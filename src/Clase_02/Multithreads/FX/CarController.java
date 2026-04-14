package Clase_02.Multithreads.FX;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CarController {

   private final ICarModel model;
   private final ExecutorService executor = Executors.newSingleThreadExecutor();
   @FXML
   private TextField txtCodigo;
   @FXML
   private TextField txtMarca;
   @FXML
   private TextField txtModelo;
   @FXML
   private TextField txtPrecio;
   @FXML
   private Button btnGuardar;

   public CarController(ICarModel model) {
      this.model = model;
   }

   @FXML
   private void handleSaveAction() {
      try {
         // 1. Recolección de datos
         Car newCar = extractCarFromForm();

         // 2. Deshabilitar botón para evitar doble envío
         btnGuardar.setDisable(true);
         btnGuardar.setText("GUARDANDO...");

         // 3. Delegar la operación pesada (I/O, Red, BD) a otro hilo
         executor.submit(() -> saveCarAsync(newCar));

      } catch (NumberFormatException e) {
         showAlert(Alert.AlertType.ERROR, "Error de formato", "El price debe ser un valor numérico válido.");
      } catch (IllegalArgumentException e) {
         showAlert(Alert.AlertType.WARNING, "Campos incompletos", e.getMessage());
      }
   }

   private Car extractCarFromForm() {
      String codigo = txtCodigo.getText().trim();
      String marca = txtMarca.getText().trim();
      String modelo = txtModelo.getText().trim();

      if (codigo.isEmpty() || marca.isEmpty() || modelo.isEmpty()) {
         throw new IllegalArgumentException("Por favor, llene todos los campos de texto.");
      }

      double precio = Double.parseDouble(txtPrecio.getText().trim());

      return new Car(codigo, marca, modelo, precio);
   }

   private void saveCarAsync(Car car) {
      try {
         model.saveCar(car);

         Platform.runLater(() -> {
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Vehículo guardado correctamente.");
            resetForm();
         });
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      } finally {
         Platform.runLater(() -> {
            btnGuardar.setDisable(false);
            btnGuardar.setText("GUARDAR");
         });
      }
   }

   private void resetForm() {
      txtCodigo.clear();
      txtMarca.clear();
      txtModelo.clear();
      txtPrecio.clear();
      txtCodigo.requestFocus();
   }

   private void showAlert(Alert.AlertType type, String title, String content) {
      Alert alert = new Alert(type);
      alert.setTitle(title);
      alert.setHeaderText(null);
      alert.setContentText(content);
      alert.showAndWait();
   }


   public void shutdown() {
      executor.shutdown();
   }


}
