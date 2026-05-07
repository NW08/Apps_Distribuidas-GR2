package Clase_05.Correction.Server;

import Clase_05.Correction.Server.db.DatabaseConfig;
import Clase_05.Correction.Server.repository.CardRepository;
import Clase_05.Correction.Server.repository.UserRepository;
import Clase_05.Correction.Server.services.CardService;
import Clase_05.Correction.Server.services.RegistrationService;
import Clase_05.Correction.Server.services.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Launcher {
   private static final int SERVER_PORT = 7541;

   void main() {
      CardRepository cardRepository = new CardRepository();
      UserRepository userRepository = new UserRepository();

      UserService userService = new UserService(userRepository);
      CardService cardService = new CardService(cardRepository);
      RegistrationService registrationService = new RegistrationService(userRepository, cardRepository);

      Server server = new Server(cardService, registrationService, userService);

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
         log.info("Shutdown signal received. Stopping server gracefully...");
         server.stop();
         DatabaseConfig.shutdown();
      }, "shutdown-hook"));

      log.info("Starting UDP server on port {}", SERVER_PORT);
      server.start(SERVER_PORT);
   }
}