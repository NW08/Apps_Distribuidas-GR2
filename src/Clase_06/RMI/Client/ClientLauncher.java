package Clase_06.RMI.Client;

import Clase_06.RMI.Client.Classes.RemoteClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientLauncher {
   static void main() {
      try {
         String host = "localhost";
         int port = 1099;
         String serviceName = "RemoteServer";

         RemoteClient client = new RemoteClient(host, port, serviceName);

         log.info("--- Starting remote queries ---");

         String result1 = client.findPerson(2);
         log.info("Result for ID 2:\n{}", result1);

         String result2 = client.findPerson(99);
         log.info("Result for ID 99:\n{}", result2);

      } catch (Exception e) {
         log.error("Failed to initialize the RMI Client application", e);
      }
   }
}