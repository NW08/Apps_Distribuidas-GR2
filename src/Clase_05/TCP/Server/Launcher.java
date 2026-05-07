package Clase_05.TCP.Server;

import Clase_05.TCP.Server.services.ArrivalRegistryService;

public class Launcher {
   void main() {
      int port = 5412;
      var registryService = new ArrivalRegistryService();
      var server = new ServerApp(port, registryService);

      server.start();
   }
}