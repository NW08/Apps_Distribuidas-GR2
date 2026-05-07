package Clase_05.Correction.Server.repository;

import Clase_05.Correction.Server.db.DatabaseConfig;
import Clase_05.Correction.Server.model.Card;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Optional;

@Slf4j
public class CardRepository {
   private static final String SQL_INSERT = "INSERT INTO card (client_id, balance) VALUES (?, ?)";
   private static final String SQL_FIND = "SELECT id, client_id, balance FROM card WHERE client_id = ?";
   private static final String SQL_UPDATE = "UPDATE card SET balance = ? WHERE id = ?";

   public Card save(Card card, Connection conn) {
      try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

         stmt.setLong(1, card.getUserID());
         stmt.setBigDecimal(2, card.getBalance());

         int affectedRows = stmt.executeUpdate();
         if (affectedRows == 0) throw new SQLException("Creating card failed, no rows affected.");

         long generatedId = extractGeneratedKey(stmt);
         Card savedCard = card.toBuilder()
               .id(generatedId)
               .build();

         log.info("Card created successfully for user ID: {} with card ID: {}", savedCard.getUserID(), savedCard.getId());
         return savedCard;
      } catch (SQLException e) {
         log.error("Error saving card for user ID {}: {}", card.getUserID(), e.getMessage(), e);
         throw new RuntimeException("Database error saving card", e);
      }
   }

   public Optional<Card> findByUserId(String userId) {
      try (Connection conn = DatabaseConfig.getConnection();
           PreparedStatement stmt = conn.prepareStatement(SQL_FIND)) {

         stmt.setLong(1, Long.parseLong(userId));

         try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return Optional.of(mapRow(rs));
         }
      } catch (SQLException e) {
         log.error("Error finding card for user ID {}: {}", userId, e.getMessage(), e);
         throw new RuntimeException("Database error finding card", e);
      }
      return Optional.empty();
   }

   public void updateBalance(Card card) {
      try (Connection conn = DatabaseConfig.getConnection();
           PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

         stmt.setBigDecimal(1, card.getBalance());
         stmt.setLong(2, card.getId());

         int affectedRows = stmt.executeUpdate();
         if (affectedRows == 0) throw new SQLException("Updating balance failed — card ID not found: " + card.getId());

         log.debug("Balance updated for card ID: {}. New balance: {}", card.getId(), card.getBalance());
      } catch (SQLException e) {
         log.error("Error updating balance for card ID {}: {}", card.getId(), e.getMessage(), e);
         throw new RuntimeException("Database error updating balance", e);
      }
   }

   private Card mapRow(ResultSet rs) throws SQLException {
      return Card.builder()
            .id(rs.getLong("id"))
            .userID(rs.getLong("client_id"))
            .balance(rs.getBigDecimal("balance"))
            .build();
   }

   private long extractGeneratedKey(PreparedStatement stmt) throws SQLException {
      try (ResultSet keys = stmt.getGeneratedKeys()) {
         if (keys.next()) return keys.getLong(1);
         throw new SQLException("Creating card failed, no ID obtained.");
      }
   }
}