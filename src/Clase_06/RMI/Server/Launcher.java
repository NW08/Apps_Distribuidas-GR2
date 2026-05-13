package Clase_06.RMI.Server;

import Clase_06.RMI.Server.Classes.Server;
import Clase_06.RMI.Shared.RemoteServer;
import lombok.extern.slf4j.Slf4j;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

@Slf4j
public class Launcher {
   static void main() {
      try {
         LocateRegistry.createRegistry(1099);
         RemoteServer server = new Server();
         String rmiUrl = "rmi://localhost:1099/RemoteServer";
         Naming.rebind(rmiUrl, server);
         log.info("RMI Remote Server is up and running on {}", rmiUrl);
      } catch (Exception e) {
         log.error("Failed to start the RMI Server", e);
         System.exit(1);
      }
   }
}