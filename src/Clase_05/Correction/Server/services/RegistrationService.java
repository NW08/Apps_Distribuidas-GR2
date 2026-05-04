package Clase_05.Correction.Server.services;

import Clase_05.Correction.Server.db.DatabaseConfig;
import Clase_05.Correction.Server.model.Card;
import Clase_05.Correction.Server.model.User;
import Clase_05.Correction.Server.repository.CardRepository;
import Clase_05.Correction.Server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class RegistrationService {
   private final UserRepository userRepository;
   private final CardRepository cardRepository;

   public User registerNewUser(User newUser) {
      log.info("Registering new user: {} {}", newUser.getName(), newUser.getLastName());

      try (Connection conn = DatabaseConfig.getConnection()) {
         conn.setAutoCommit(false);

         try {
            User savedUser = userRepository.save(newUser, conn);
            log.debug("User persisted with ID: {}", savedUser.getId());

            Card newCard = Card.builder()
                  .userID(savedUser.getId())
                  .build();
            cardRepository.save(newCard, conn);
            log.debug("Card provisioned for user ID: {}", savedUser.getId());

            conn.commit();
            log.info("Registration completed for user ID: {}", savedUser.getId());
            return savedUser;

         } catch (Exception e) {
            conn.rollback();
            log.error("Registration failed, transaction rolled back: {}", e.getMessage(), e);
            throw new RuntimeException("User registration failed", e);
         }

      } catch (SQLException e) {
         log.error("Failed to obtain or manage DB connection: {}", e.getMessage(), e);
         throw new RuntimeException("Database connection error during registration", e);
      }
   }
}