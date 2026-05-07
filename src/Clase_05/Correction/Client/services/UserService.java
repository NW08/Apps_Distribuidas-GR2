package Clase_05.Correction.Client.services;

import Clase_05.Correction.Client.model.Card;
import Clase_05.Correction.Client.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class UserService {
   private static final String SEPARATOR = "\\|";
   private static final String STATUS_SUCCESS = "SUCCESS";

   private static final String CMD_CREATE = "CREATE";
   private static final String CMD_SEARCH = "SEARCH";
   private static final String CMD_RECHARGE = "RECHARGE";
   private static final String CMD_PAY = "PAY";

   private static final String PREFIX_ID = "ID:";
   private static final String PREFIX_NAME = "NAME:";
   private static final String PREFIX_BALANCE = "BALANCE:$";
   private static final String PREFIX_USER_ID = "USER_ID:";

   private final String serverIP;
   private final int serverPort;
   private final UDPService service;

   /**
    * Sends a CREATE command to the server.
    * <p>Server expects: {@code CREATE|identification|name|lastName|email|phone|YYYY-MM-DD}
    * <p>Server responds: {@code SUCCESS|USER_ID:<id>}
    *
    * @return the new user's database ID, or empty on failure.
    */
   public Optional<Long> processRegistration(User user) {
      try {
         String payload = String.join("|",
               CMD_CREATE,
               user.getIdentification(),
               user.getName(),
               user.getLastName(),
               user.getEmail(),
               user.getPhone(),
               user.getBirthday().toString());

         String response = service.sendAndReceiveAsync(serverIP, serverPort, payload).join();
         String[] parts = response.split(SEPARATOR, 2);

         if (isSuccess(parts, 2)) {
            long userId = Long.parseLong(parts[1].trim().substring(PREFIX_USER_ID.length()));
            log.info("User registered successfully with ID: {}", userId);
            return Optional.of(userId);
         }

         log.warn("Server returned error on registration: {}", response);
         return Optional.empty();

      } catch (Exception e) {
         log.error("Exception while registering user: {}", user.getIdentification(), e);
         return Optional.empty();
      }
   }

   /**
    * Sends a RECHARGE command to the server.
    * <p>Server expects: {@code RECHARGE|<db_user_id>|<amount>}
    * <p>Server responds: {@code SUCCESS|BALANCE:$<newBalance>}
    *
    * @return the updated card balance, or empty on failure.
    */
   public Optional<BigDecimal> processRecharge(String userId, BigDecimal amount) {
      try {
         String payload = String.join("|", CMD_RECHARGE, userId, amount.toPlainString());
         String response = service.sendAndReceiveAsync(serverIP, serverPort, payload).join();
         return parseBalanceResponse(response);

      } catch (Exception e) {
         log.error("Exception while recharging card for user ID: {} with amount: {}", userId, amount, e);
         return Optional.empty();
      }
   }

   /**
    * Sends a PAY command to the server.
    * <p>Server expects: {@code PAY|<db_user_id>}
    * <p>Server responds: {@code SUCCESS|BALANCE:$<newBalance>}
    *
    * @return the updated card balance, or empty on failure.
    */
   public Optional<BigDecimal> processPayment(String userId) {
      try {
         String payload = String.join("|", CMD_PAY, userId);
         String response = service.sendAndReceiveAsync(serverIP, serverPort, payload).join();
         return parseBalanceResponse(response);

      } catch (Exception e) {
         log.error("Exception while processing payment for user ID: {}", userId, e);
         return Optional.empty();
      }
   }

   /**
    * Sends a SEARCH command to the server.
    * <p>Server expects: {@code SEARCH|<identification>}
    * <p>Server responds: {@code SUCCESS|ID:<id>|NAME:<firstName> <lastName>|BALANCE:$<balance>}
    *
    * @return a partially populated User (id, name, lastName, card balance), or empty on failure.
    */
   public Optional<User> searchUser(String identification) {
      try {
         String payload = String.join("|", CMD_SEARCH, identification);
         String response = service.sendAndReceiveAsync(serverIP, serverPort, payload).join();
         String[] parts = response.split(SEPARATOR);

         // Expected 4 tokens: SUCCESS | ID:x | NAME:x | BALANCE:$x
         if (isSuccess(parts, 4)) return Optional.of(parseSearchResponse(parts));

         log.warn("Server returned error or incomplete data for identification {}: {}", identification, response);
         return Optional.empty();

      } catch (Exception e) {
         log.error("Exception while searching user with identification: {}", identification, e);
         return Optional.empty();
      }
   }

   /**
    * Validates that an Ecuadorian cédula has exactly 10 digits.
    */
   public boolean isValidIdentification(String identification) {
      return identification != null && identification.matches("\\d{10}");
   }

   /**
    * Shared parser for any server response of the shape {@code SUCCESS|BALANCE:$<amount>}. Used by both RECHARGE and PAY.
    */
   private Optional<BigDecimal> parseBalanceResponse(String response) {
      String[] parts = response.split(SEPARATOR, 2);

      if (isSuccess(parts, 2)) return parseBalance(parts[1].trim());

      log.warn("Server returned error or incomplete balance response: {}", response);
      return Optional.empty();
   }

   /**
    * Parses the four-token SEARCH response into a User with a nested Card. parts[0] = "SUCCESS" parts[1] = "ID:<id>" parts[2] =
    * "NAME:<firstName> <lastName>" parts[3] = "BALANCE:$<amount>"
    */
   private User parseSearchResponse(String[] parts) {
      long id = Long.parseLong(parts[1].trim().substring(PREFIX_ID.length()));

      String fullName = parts[2].trim().substring(PREFIX_NAME.length());
      String[] names = fullName.split(" ", 2);
      String firstName = names[0];
      String lastName = names.length > 1 ? names[1] : "";

      BigDecimal balance = parseBalance(parts[3].trim()).orElse(BigDecimal.ZERO);

      Card card = Card.builder()
            .userID(id)
            .balance(balance)
            .build();

      return User.builder()
            .id(id)
            .name(firstName)
            .lastName(lastName)
            .card(card)
            .build();
   }

   /**
    * Extracts a BigDecimal from a token formatted as {@code BALANCE:$<amount>}.
    */
   private Optional<BigDecimal> parseBalance(String token) {
      if (!token.startsWith(PREFIX_BALANCE)) {
         log.warn("Unexpected balance token format: {}", token);
         return Optional.empty();
      }
      try {
         return Optional.of(new BigDecimal(token.substring(PREFIX_BALANCE.length())));
      } catch (NumberFormatException e) {
         log.error("Failed to parse balance value from token: {}", token, e);
         return Optional.empty();
      }
   }

   /**
    * Returns true only when the first token is SUCCESS and the array has at least the required number of parts.
    */
   private boolean isSuccess(String[] parts, int requiredLength) {
      return parts.length >= requiredLength && STATUS_SUCCESS.equals(parts[0]);
   }
}