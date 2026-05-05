package Clase_05.Correction.Client.services;

import Clase_05.Correction.Client.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Slf4j
@RequiredArgsConstructor
public class UserService {
   private final String SERVER_IP;
   private final int SERVER_PORT;

   private final UDPService server = new UDPService();

   public boolean isValidIdentification(String cedula) {
      return cedula != null && cedula.matches("\\d{10}");
   }

   public User searchUser(String cedula) {
      try {
         String payload = String.format("SEARCH|%s", cedula.trim());
         String response = server.sendAndReceiveAsync(SERVER_IP, SERVER_PORT, payload).join();

         if (response.startsWith("SUCCESS|")) {
            String userData = response.substring(8);
            return parseUserFromString(userData);
         } else {
            log.warn("El servidor respondió con error o no encontró al usuario: {}", response);
            return null;
         }

      } catch (Exception e) {
         log.error("Excepción intentando buscar el usuario con cédula: {}", cedula, e);
         return null;
      }
   }

   private User parseUserFromString(String userData) {
      String cleanData = userData.replace("User(", "").replace(")", "").trim();

      String[] data = cleanData.split(",");

      try {
         String id = data[0].split("=")[1].trim();
         String identityCard = data[1].split("=")[1].trim();
         String name = data[2].split("=")[1].trim();
         String lastName = data[3].split("=")[1].trim();
         String email = data[4].split("=")[1].trim();
         String phone = data[5].split("=")[1].trim();
         String birthdayStr = data[6].split("=")[1].trim();

         if (birthdayStr.equals("null")) throw new IllegalArgumentException("La fecha de nacimiento no puede ser nula");

         return User.builder()
               .id(Long.parseLong(id))
               .identification(identityCard)
               .name(name)
               .lastName(lastName)
               .email(email.equals("null") ? "" : email)
               .phone(phone.equals("null") ? "" : phone)
               .birthday(LocalDate.parse(birthdayStr))
               .build();

      } catch (ArrayIndexOutOfBoundsException | DateTimeParseException e) {
         log.error("Error al parsear los datos del usuario. Formato inesperado: {}", userData, e);
         return null;
      }
   }
}