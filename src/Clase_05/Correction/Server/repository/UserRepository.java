package Clase_05.Correction.Server.repository;

import Clase_05.Correction.Server.db.DatabaseConfig;
import Clase_05.Correction.Server.model.User;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Optional;

@Slf4j
public class UserRepository {

   private static final String SQL_INSERT =
         "INSERT INTO client (identity_card, first_name, last_name, email, phone, birthday) VALUES (?, ?, ?, ?, ?, ?)";

   private static final String SQL_FIND_BY_ID = "SELECT id, identity_card, first_name, last_name, email, phone, " +
         "birthday FROM client WHERE id = ?";

   public Optional<User> findById(Long id) {
      try (Connection conn = DatabaseConfig.getConnection();
           PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
         stmt.setLong(1, id);
         try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return Optional.of(mapRow(rs));
         }
      } catch (SQLException e) {
         log.error("Error finding user by ID {}: {}", id, e.getMessage(), e);
         throw new RuntimeException("Database error finding user", e);
      }
      return Optional.empty();
   }

   private User mapRow(ResultSet rs) throws SQLException {
      return User.builder()
            .id(rs.getLong("id"))
            .identification(rs.getString("identity_card"))
            .name(rs.getString("first_name"))
            .lastName(rs.getString("last_name"))
            .email(rs.getString("email"))
            .phone(rs.getString("phone"))
            .birthDate(rs.getDate("birthday").toLocalDate())
            .build();
   }

   public User save(User user, Connection conn) {
      try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

         stmt.setString(1, user.getIdentification());
         stmt.setString(2, user.getName());
         stmt.setString(3, user.getLastName());
         stmt.setString(4, user.getEmail());
         stmt.setString(5, user.getPhone());
         stmt.setDate(6, Date.valueOf(user.getBirthDate()));

         int affectedRows = stmt.executeUpdate();
         if (affectedRows == 0) {
            throw new SQLException("Creating user failed, no rows affected.");
         }

         long generatedId = extractGeneratedKey(stmt);

         User savedUser = user.toBuilder()
               .id(generatedId)
               .build();

         log.info("User created successfully with ID: {}", savedUser.getId());
         return savedUser;

      } catch (SQLException e) {
         log.error("Error saving user to database: {}", e.getMessage(), e);
         throw new RuntimeException("Database error saving user", e);
      }
   }

   private long extractGeneratedKey(PreparedStatement stmt) throws SQLException {
      try (ResultSet keys = stmt.getGeneratedKeys()) {
         if (keys.next()) {
            return keys.getLong(1);
         }
         throw new SQLException("Creating user failed, no ID obtained.");
      }
   }
}