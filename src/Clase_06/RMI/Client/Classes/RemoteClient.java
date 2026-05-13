package Clase_06.RMI.Client.Classes;

import Clase_06.RMI.Shared.RemoteServer;
import lombok.extern.slf4j.Slf4j;

import java.rmi.Naming;

@Slf4j
public class RemoteClient {
   private final RemoteServer remoteServer;

   public RemoteClient(String host, int port, String serviceName) throws Exception {
      String rmiUrl = String.format("rmi://%s:%d/%s", host, port, serviceName);
      log.info("Connecting to RMI Registry at: {}", rmiUrl);

      this.remoteServer = (RemoteServer) Naming.lookup(rmiUrl);
      log.info("Successfully connected to remote server.");
   }

   public String findPerson(int id) {
      try {
         log.debug("Requesting person with id: {}", id);
         return remoteServer.findPersonById(id);
      } catch (Exception e) {
         log.error("Network or Remote Error while fetching person with id: {}", id, e);
         return "Error retrieving data.";
      }
   }
}