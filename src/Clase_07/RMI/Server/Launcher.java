package Clase_07.RMI.Server;

import Clase_07.RMI.Server.Classes.Repository;
import Clase_07.RMI.Server.Classes.Server;
import lombok.extern.slf4j.Slf4j;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RemoteServer;
import java.util.Optional;

@Slf4j
public class Launcher {
   public static void main() {
      try {
         String dbHost = getEnvOrThrow("DB_HOST");
         int dbPort = Integer.parseInt(getEnvOrDefault());
         String dbName = getEnvOrThrow("DB_NAME");
         String dbUser = getEnvOrThrow("DB_USER");
         String dbPassword = getEnvOrThrow("DB_PASS");

         log.info("Initializing database connection for {}:{}", dbHost, dbPort);

         Repository repository = new Repository(dbHost, dbPort, dbName, dbUser, dbPassword);

         LocateRegistry.createRegistry(1099);
         RemoteServer server = new Server(repository);

         String rmiUrl = "rmi://localhost:1099/RemoteServer";
         Naming.rebind(rmiUrl, server);

         log.info("RMI Remote Server is up and running on {}", rmiUrl);

      } catch (IllegalArgumentException e) {
         log.error("Configuration error: {}", e.getMessage());
         System.exit(1);
      } catch (Exception e) {
         log.error("Failed to start the RMI Server", e);
         System.exit(1);
      }
   }

   private static String getEnvOrThrow(String variableName) {
      return Optional.ofNullable(System.getenv(variableName))
            .orElseThrow(() -> new IllegalArgumentException("Missing required environment variable: " + variableName));
   }

   private static String getEnvOrDefault() {
      return Optional.ofNullable(System.getenv("DB_PORT"))
            .orElse("5432");
   }
}