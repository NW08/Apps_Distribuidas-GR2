package Clase_05.TCP.Client;

import Clase_05.TCP.Client.services.ArrivalClientService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Launcher {
   void main() {
      String serverIp = "192.168.1.2";
      int serverPort = 5412;
      String employeeName = "Jossu Ortiz";

      var clientService = new ArrivalClientService(serverIp, serverPort);

      try {
         log.info("Attempting to register arrival for: {}", employeeName);
         String result = clientService.registerArrival(employeeName);
         log.info("Success! Server says: {}", result);
      } catch (RuntimeException e) {
         log.error("Process aborted due to network error: {}", e.getMessage());
      }
   }
}