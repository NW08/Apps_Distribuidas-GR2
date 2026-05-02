package Clase_04.UI;

import Clase_04.Client.Controller.ClienteUDP;
import Clase_04.Client.Model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.Locale;

/**
 * Controlador principal que maneja las 3 vistas de la aplicación. Gestiona la comunicación con el servidor y la navegación entre ventanas.
 */
@Log
public class UserController {

   // Constantes estáticas para el servidor (mejor práctica)
   private static final String SERVER_IP = "localhost";
   private static final int SERVER_PORT = 7000;
   @Getter
   private static User userActual;

   // ================= VISTA MAIN =================
   @FXML
   private TextField lbCedulaEnter;
   @FXML
   private Label lblWarning;

   // ================= VISTA REGISTRO =================
   @FXML
   private TextField lbNombreEnter;
   @FXML
   private TextField lbCedulaEnterRegistro;
   @FXML
   private TextField lbCorreoEnter;
   @FXML
   private TextField lbPreferencialEnter;

   // ================= VISTA PAGOS/RECARGAS =================
   @FXML
   private TextField lbSaldo;
   @FXML
   private TextArea txtAInfoUsuario;

   /**
    * -- SETTER -- Establece el Stage principal para navegación. Se llama desde AppLauncher al cargar la primera vista.
    */
   @Setter
   private Stage stage;

   @FXML
   void initialize() {
      if (txtAInfoUsuario != null) cargarDatosUsuario();
   }

   // ================= MÉTODOS DE NAVEGACIÓN =================
   private void cambiarVista(String fxmlPath) {
      try {
         FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
         Parent root = loader.load();

         // Obtener el controlador de la nueva vista y pasarle el stage
         Object controller = loader.getController();
         if (controller instanceof UserController) ((UserController) controller).setStage(stage);

         Scene scene = new Scene(root, 600, 400);
         stage.setScene(scene);
         stage.show();
      } catch (IOException e) {
         log.severe("Error al cargar la vista " + fxmlPath + ": " + e.getMessage());
         mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la vista.");
      }
   }

   @FXML
   private void goToRegistro() {
      cambiarVista("View/VistaRegistroUser.fxml");
   }

   // ================= VISTA MAIN - EVENTOS =================

   @FXML
   private void searchUser() {
      String cedula = lbCedulaEnter.getText().trim();

      if (cedula.isEmpty()) {
         lblWarning.setText("Ingrese una cédula");
         return;
      }

      ClienteUDP cliente = new ClienteUDP(SERVER_IP, SERVER_PORT);
      boolean encontrado = cliente.buscarUsuario(cedula);

      if (encontrado) {
         userActual = cliente.getCurrentUser();
         cambiarVista("View/VistaPagosRecargas.fxml");
      } else {
         lblWarning.setText("Usuario no encontrado");
      }
   }

   // ================= VISTA REGISTRO - EVENTOS =================
   private Boolean parsearPreferencial(String valor) {
      if (valor.isEmpty()) return false;
      return switch (valor.toLowerCase()) {
         case "si", "1", "true" -> true;
         case "no", "0", "false" -> false;
         default -> null;
      };
   }

   @FXML
   private void crearUsuario() {
      String nombre = lbNombreEnter.getText().trim();
      String cedula = lbCedulaEnterRegistro.getText().trim();
      String correo = lbCorreoEnter.getText().trim();
      String preferencialStr = lbPreferencialEnter.getText().trim();

      if (nombre.isEmpty() || cedula.isEmpty() || correo.isEmpty()) {
         mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Todos los campos son obligatorios");
         return;
      }

      Boolean preferencial = parsearPreferencial(preferencialStr);
      if (preferencial == null) {
         mostrarAlerta(Alert.AlertType.ERROR, "Error", "Valor de preferencial inválido. Use: SI/NO o 1/0");
         return;
      }

      User nuevoUser = new User(cedula, correo, "", nombre, preferencial);
      log.info("Intentando registrar: {}");

      boolean registrado = new ClienteUDP(SERVER_IP, SERVER_PORT).registrarUsuario(nuevoUser);
      log.info("Resultado registro: {}");

      if (!registrado) {
         mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo registrar el usuario");
         return;
      }

      userActual = nuevoUser;
      limpiarCamposRegistro();
      mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario registrado correctamente");
      cambiarVista("View/VistaPagosRecargas.fxml");
   }

   private void limpiarCamposRegistro() {
      lbNombreEnter.clear();
      lbCedulaEnterRegistro.clear();
      lbCorreoEnter.clear();
      lbPreferencialEnter.clear();
   }

   // ================= VISTA PAGOS/RECARGAS - EVENTOS =================
   @FXML
   public void cargarDatosUsuario() {
      if (userActual != null) {
         String info = String.format(Locale.US,
               "Cédula: %s\nNombre: %s\nCorreo: %s\nTeléfono: %s\nPreferencial: %b\nSaldo: $%.2f",
               userActual.getCedula(),
               userActual.getName(),
               userActual.getMail(),
               userActual.getPhone(),
               userActual.isPreferred(),
               userActual.getBalance()
         );
         txtAInfoUsuario.setText(info);
         lbSaldo.setText(String.format(Locale.US, "%.2f", userActual.getBalance()));
      }
   }

   @FXML
   private void recargar() {
      if (userActual == null) {
         mostrarAlerta(Alert.AlertType.ERROR, "Error", "No hay usuario activo");
         return;
      }

      TextInputDialog dialog = new TextInputDialog();
      dialog.setTitle("Recargar Saldo");
      dialog.setHeaderText("Ingrese el monto a recargar:");
      dialog.setContentText("Monto:");

      String result = dialog.showAndWait().orElse(null);

      if (result != null && !result.trim().isEmpty()) {
         try {
            String montoStr = result.trim().replace(',', '.');
            double monto = Double.parseDouble(montoStr);

            if (monto <= 0) {
               mostrarAlerta(Alert.AlertType.WARNING, "Error", "El monto debe ser positivo");
               return;
            }

            ClienteUDP cliente = new ClienteUDP(SERVER_IP, SERVER_PORT);
            double nuevoSaldo = cliente.recargarSaldo(userActual.getCedula(), monto);

            if (nuevoSaldo >= 0) {
               cargarDatosUsuario();
               mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Saldo recargado: $"
                     + String.format(Locale.US, "%.2f", monto));
            } else {
               mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo recargar el saldo");
            }

         } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Monto numérico inválido");
         }
      }
   }

   @FXML
   private void pagar() {
      if (userActual == null) {
         mostrarAlerta(Alert.AlertType.ERROR, "Error", "No hay usuario activo");
         return;
      }

      ClienteUDP cliente = new ClienteUDP(SERVER_IP, SERVER_PORT);
      double nuevoSaldo = cliente.pagarPasaje(userActual.getCedula());

      if (nuevoSaldo >= 0) {
         cargarDatosUsuario();
         mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Pasaje pagado correctamente");
      } else {
         mostrarAlerta(Alert.AlertType.WARNING, "Saldo Insuficiente", "No tienes saldo suficiente para " +
               "pagar el pasaje");
      }
   }

   // ================= MÉTODOS AUXILIARES =================

   /**
    * Muestra una ventana de alerta. Ahora permite elegir el tipo de ícono (Error, Info, etc.).
    */
   private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
      Alert alert = new Alert(tipo);
      alert.setTitle(titulo);
      alert.setHeaderText(null);
      alert.setContentText(mensaje);
      alert.showAndWait();
   }
}