package Clase_05.Correction.Server.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatabaseConfig {
   private static final HikariDataSource DATA_SOURCE = buildDataSource();

   private static HikariDataSource buildDataSource() {
      String password = System.getenv("DATABASE_PASS");
      if (password == null || password.isBlank()) throw new IllegalStateException("Missing env variable: DATABASE_PASS");

      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/test?tcpKeepAlive=true"));
      config.setUsername(System.getenv().getOrDefault("DB_USER", "root"));
      config.setPassword(password);

      config.setMaximumPoolSize(10);
      config.setMinimumIdle(2);
      config.setConnectionTimeout(5000);
      config.setIdleTimeout(150000);
      config.setMaxLifetime(600000);
      config.setPoolName("udp-server-pool");

      config.setConnectionTestQuery("SELECT 1");

      log.info("HikariCP pool initialized — maxPoolSize: {}", config.getMaximumPoolSize());
      return new HikariDataSource(config);
   }

   public static Connection getConnection() throws SQLException {
      return DATA_SOURCE.getConnection();
   }

   public static void shutdown() {
      if (!DATA_SOURCE.isClosed()) {
         DATA_SOURCE.close();
         log.info("Connection pool closed.");
      }
   }
}