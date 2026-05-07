package Clase_05.Correction.Server.services;

import Clase_05.Correction.Server.model.Card;
import Clase_05.Correction.Server.model.User;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
public enum Command {
   SEARCH((parts, cardService, _, userService) -> {
      // Expected: SEARCH|<identity_card>
      User user = userService.getUser(parts[1].trim());
      Card card = cardService.findCardByUserId(String.valueOf(user.getId()));
      return "SUCCESS|ID:%d|NAME:%s %s|BALANCE:$%s"
            .formatted(user.getId(), user.getName(), user.getLastName(), card.getBalance());
   }),

   CREATE((parts, _, registrationService, _) -> {
      // Expected: CREATE|identity_card|first_name|last_name|email|phone|YYYY-MM-DD
      if (!parts[4].trim().contains("@"))
         throw new IllegalArgumentException("Invalid email format.");
      if (parts[5].trim().length() != 10)
         throw new IllegalArgumentException("Invalid phone number.");

      User newUser = User.builder()
            .identification(parts[1].trim())
            .name(parts[2].trim())
            .lastName(parts[3].trim())
            .email(parts[4].trim())
            .phone(parts[5].trim())
            .birthday(LocalDate.parse(parts[6].trim()))
            .build();
      User registered = registrationService.registerNewUser(newUser);
      return "SUCCESS|USER_ID:%d".formatted(registered.getId());
   }),

   RECHARGE((parts, cardService, _, _) -> {
      // Expected: RECHARGE|<db_user_id>|<amount>
      String userDbId = parts[1].trim();
      BigDecimal amount = new BigDecimal(parts[2].trim());
      BigDecimal newBalance = cardService.rechargeCard(userDbId, amount);
      return "SUCCESS|BALANCE:$%s".formatted(newBalance);
   }),

   PAY((parts, cardService, _, userService) -> {
      // Expected: PAY|<db_user_id>
      String userDbId = parts[1].trim();
      User user = userService.getUserByDBID(Long.parseLong(userDbId));
      BigDecimal newBalance = cardService.processPayment(userDbId, user.isPreferred());
      return "SUCCESS|BALANCE:$%s".formatted(newBalance);
   });

   private final CommandHandler handler;

   public static Optional<Command> from(String name) {
      try {
         return Optional.of(valueOf(name.toUpperCase()));
      } catch (IllegalArgumentException e) {
         return Optional.empty();
      }
   }

   public String execute(String[] parts, CardService cardService,
                         RegistrationService registrationService, UserService userService) {
      return handler.handle(parts, cardService, registrationService, userService);
   }
}