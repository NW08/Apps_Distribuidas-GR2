package Clase_05.Correction.Server.db;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConnectionSQL {
   private static final String DB_URL = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/test");
   private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "root");

   public static Connection getConnection() {
      String dbPassword = System.getenv("DATABASE_PASS");

      if (dbPassword == null || dbPassword.isBlank()) {
         log.error("Database password environment variable (DATABASE_PASS) is missing or empty.");
         throw new IllegalStateException("Missing database credentials. Please set DATABASE_PASS.");
      }

      try {
         log.debug("Attempting to connect to the database at URL: {}", DB_URL);
         Connection connection = DriverManager.getConnection(DB_URL, DB_USER, dbPassword);
         log.debug("Database connection established successfully.");
         return connection;

      } catch (SQLException e) {
         log.error("Failed to connect to the database. Reason: {}", e.getMessage(), e);
         throw new RuntimeException("Database connection error", e);
      }
   }
}