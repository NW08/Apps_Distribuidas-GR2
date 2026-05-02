package Clase_04.Client.Controller;

import Clase_04.Client.Model.User;
import lombok.Getter;
import lombok.extern.java.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;


@Log
public class ClienteUDP {
   private final String SERVER_IP;
   private final int SERVER_PORT;

   @Getter
   private User currentUser;

   public ClienteUDP(String serverAddress, int serverPort) {
      this.SERVER_IP = serverAddress;
      this.SERVER_PORT = serverPort;
   }

   private static User getUser(String[] inputParts) {
      User user = new User();
      user.setCedula(inputParts[1].replace("Cédula: ", "").trim());
      user.setName(inputParts[2].replace("Nombre: ", "").trim());
      user.setMail(inputParts[3].replace("Correo: ", "").trim());
      user.setPhone(inputParts[4].replace("Teléfono: ", "").trim());
      user.setPreferred(Boolean.parseBoolean(inputParts[5].replace("Preferencial: ", "").trim()));

      String saldoStr = inputParts[6].replace("Saldo: ", "").replace(',', '.').trim();
      user.setBalance(Double.parseDouble(saldoStr));

      return user;
   }

   public boolean registrarUsuario(User user) {
      String mensaje = String.format(
            "REGISTRAR|%s|%s|%s|%s|%b",
            user.getCedula(),
            user.getMail(),
            user.getPhone(),
            user.getName(),
            user.isPreferred()
      );

      String respuesta = enviarMensaje(mensaje);

      if (respuesta.startsWith("OK")) {
         this.currentUser = user;
         return true;
      }
      return false;
   }

   public boolean buscarUsuario(String cedula) {
      String mensaje = "BUSCAR|" + cedula;
      String respuesta = enviarMensaje(mensaje);

      if (respuesta.startsWith("OK")) {
         String[] partes = respuesta.split("\\|");
         if (partes.length >= 7) {
            this.currentUser = getUser(partes);
            return true;
         }
      }
      return false;
   }

   private double parsearSaldoRespuesta(String respuesta, String cedula) {
      if (respuesta.startsWith("OK")) {
         String[] partes = respuesta.split(":");
         if (partes.length >= 2) {
            double nuevoSaldo = Double.parseDouble(partes[1].replace(',', '.').trim());
            if (currentUser != null && currentUser.getCedula().equals(cedula)) {
               currentUser.setBalance(nuevoSaldo);
            }
            return nuevoSaldo;
         }
      }
      return -1;
   }

   public double recargarSaldo(String cedula, double monto) {
      String mensaje = String.format(Locale.US, "RECARGAR|%s|%.2f", cedula, monto);
      return parsearSaldoRespuesta(enviarMensaje(mensaje), cedula);
   }

   public double pagarPasaje(String cedula) {
      return parsearSaldoRespuesta(enviarMensaje("PAGAR|" + cedula), cedula);
   }

   /**
    * Envía un mensaje UDP al servidor y retorna la respuesta.
    */
   private String enviarMensaje(String mensaje) {
      try (DatagramSocket socket = new DatagramSocket()) {
         socket.setSoTimeout(10000);

         log.info("[ENVIANDO] " + mensaje);

         byte[] datos = mensaje.getBytes(StandardCharsets.UTF_8);

         DatagramPacket packet = new DatagramPacket(
               datos,
               datos.length,
               InetAddress.getByName(SERVER_IP),
               SERVER_PORT
         );

         socket.send(packet);

         byte[] buffer = new byte[1024];
         DatagramPacket respuestaPacket = new DatagramPacket(buffer, buffer.length);
         socket.receive(respuestaPacket);

         String respuesta = new String(
               respuestaPacket.getData(),
               0,
               respuestaPacket.getLength(),
               StandardCharsets.UTF_8
         );

         log.info("[RECIBIDO] " + respuesta);
         return respuesta;

      } catch (SocketTimeoutException e) {
         log.warning("Tiempo de espera agotado. El servidor no responde.");
         return "ERROR: Timeout";
      } catch (Exception e) {
         log.severe("Error de comunicación: " + e.getMessage());
         return "ERROR: " + e.getMessage();
      }
   }
}