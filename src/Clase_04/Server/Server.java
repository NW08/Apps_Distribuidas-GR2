package Clase_04.Server;

import Clase_04.Server.Model.Tarjeta;
import Clase_04.Server.Model.Usuario;
import lombok.extern.java.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Clase principal del servidor UDP que gestiona usuarios y tarjetas ciudadanas. Maneja operaciones de registro, búsqueda, recarga y pago de
 * pasaje.
 */
@Log
public class Server {
   private final int puerto;

   public Server(int puerto) {
      this.puerto = puerto;
   }

   // ================= MÉTODOS AUXILIARES =================

   /**
    * Busca un usuario por cédula en la lista estática. Retorna el usuario si existe, o null si no se encuentra.
    */
   private static Usuario obtenerUsuario(String cedula) {
      for (Usuario u : Usuario.getUsuariosCreados()) {
         if (u.getCedula().equals(cedula)) {
            return u;
         }
      }
      return null;
   }

   /**
    * Verifica si el usuario tiene tarjeta, si no, le crea una y se la asigna automáticamente.
    */
   private static void asegurarTarjeta(Usuario usuario) {
      if (usuario.getTarjeta() == null) {
         Tarjeta nuevaTarjeta = new Tarjeta();
         nuevaTarjeta.asignarTarjeta(usuario);
         usuario.setTarjeta(nuevaTarjeta);
      }
   }

   // ================= MÉTODOS DE OPERACIÓN =================
   private static String registrarUsuario(String[] partes) {
      if (partes.length != 6) {
         return "ERROR: Formato inválido";
      }

      String cedula = partes[1];
      if (obtenerUsuario(cedula) != null) {
         return "ERROR: Usuario ya existe";
      }

      String correo = partes[2];
      String telefono = partes[3];
      String nombre = partes[4];
      boolean preferencial = Boolean.parseBoolean(partes[5]);

      // Crear nuevo usuario (se agrega automáticamente a la lista en el constructor)
      Usuario nuevo = new Usuario(cedula, correo, telefono, nombre, preferencial);
      return "OK|Usuario registrado: " + nuevo.getNombre();
   }

   private static String buscarUsuario(String[] partes) {
      if (partes.length != 2) {
         return "ERROR: Formato inválido";
      }

      Usuario encontrado = obtenerUsuario(partes[1]);
      if (encontrado == null) {
         return "ERROR: Usuario no encontrado";
      }

      // Obtener saldo de la tarjeta (si existe)
      Tarjeta tarjeta = encontrado.getTarjeta();
      double saldo = (tarjeta != null) ? tarjeta.getSaldo() : 0.0;

      // Retornar formato asegurando punto decimal con Locale.US
      return String.format(Locale.US,
            "OK|Cédula: %s|Nombre: %s|Correo: %s|Teléfono: %s|Preferencial: %b|Saldo: %.2f",
            encontrado.getCedula(),
            encontrado.getNombre(),
            encontrado.getCorreo(),
            encontrado.getTelefono(),
            encontrado.isPreferencial(),
            saldo
      );
   }

   private static String recargarTarjeta(String[] partes) {
      if (partes.length != 3) {
         return "ERROR: Formato inválido";
      }

      try {
         // Validar que el monto sea un número válido
         double monto = Double.parseDouble(partes[2].replace(',', '.'));
         if (monto <= 0) {
            return "ERROR: Monto debe ser positivo";
         }

         Usuario usuario = obtenerUsuario(partes[1]);
         if (usuario == null) {
            return "ERROR: Usuario no encontrado";
         }

         asegurarTarjeta(usuario);
         usuario.getTarjeta().cargarSaldo(monto);

         return String.format(Locale.US, "OK|Saldo recargado. Nuevo saldo: %.2f", usuario.getTarjeta().getSaldo());
      } catch (NumberFormatException e) {
         return "ERROR: Monto inválido";
      }
   }

   private static String pagarPasaje(String[] partes) {
      if (partes.length != 2) {
         return "ERROR: Formato inválido";
      }

      Usuario usuario = obtenerUsuario(partes[1]);
      if (usuario == null) {
         return "ERROR: Usuario no encontrado";
      }

      asegurarTarjeta(usuario);

      if (usuario.getTarjeta().pagarPasaje()) {
         return String.format(Locale.US, "OK|Pago exitoso. Saldo restante: %.2f", usuario.getTarjeta().getSaldo());
      } else {
         return "ERROR: Saldo insuficiente";
      }
   }

   // ================= NÚCLEO DEL SERVIDOR =================
   public void operar() {
      log.info("Servidor UDP iniciado en el puerto " + puerto);

      try (DatagramSocket socket = new DatagramSocket(puerto)) {
         while (true) {
            byte[] bufferEnter = new byte[1024];

            DatagramPacket packetEnter = new DatagramPacket(bufferEnter, bufferEnter.length);
            socket.receive(packetEnter);

            // Convertir de bytes a String (Aseguramos UTF-8)
            String recibido = new String(
                  packetEnter.getData(),
                  0,
                  packetEnter.getLength(),
                  StandardCharsets.UTF_8
            );

            log.info("[CLIENTE -> SERVER] " + recibido);

            String respuesta = procesarMensaje(recibido);

            log.info("[SERVER -> CLIENTE] " + respuesta);

            // Convertir String a bytes (Aseguramos UTF-8)
            byte[] bufferSalida = respuesta.getBytes(StandardCharsets.UTF_8);

            DatagramPacket salida = new DatagramPacket(
                  bufferSalida,
                  bufferSalida.length,
                  packetEnter.getAddress(),
                  packetEnter.getPort()
            );

            socket.send(salida);
         }
      } catch (Exception e) {
         log.severe("Error grave en el servidor: " + e.getMessage());
      }
   }

   private String procesarMensaje(String mensaje) {
      String[] partes = mensaje.split("\\|");
      String comando = partes[0].toUpperCase();

      return switch (comando) {
         case "REGISTRAR" -> registrarUsuario(partes);
         case "BUSCAR" -> buscarUsuario(partes);
         case "RECARGAR" -> recargarTarjeta(partes);
         case "PAGAR" -> pagarPasaje(partes);
         default -> "ERROR: Comando no reconocido";
      };
   }
}