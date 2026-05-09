package Clase_05.TCP.Server;

import Clase_05.TCP.Server.core.ArrivalRegistryService;
import Clase_05.TCP.Server.repository.PunchRepository;

public class Launcher {
   private static final int PORT = 5412;
   PunchRepository repository = new PunchRepository();
   ArrivalRegistryService registryService = new ArrivalRegistryService(repository);
   ServerApp server = new ServerApp(PORT, registryService);

   void main() {
      Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
      server.start();
   }
}