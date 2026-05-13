package Clase_05.TCP.Client;

import Clase_02.Multithreads.FX.JavaFXManager;
import Clase_05.TCP.Client.model.JacksonConfig;
import Clase_05.TCP.Client.services.TCPService;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
public class Launcher {
   private static final String SERVER_IP = "192.168.56.1";
   private static final int SERVER_PORT = 5412;

   public static void main() {
      try {
         JavaFXManager.startup();
         ObjectMapper mapper = new JacksonConfig().mapper;
         TCPService service = new TCPService(SERVER_IP, SERVER_PORT, mapper);
         ClientApp app = new ClientApp(service);
         app.start();
      } catch (Exception e) {
         log.error("Critical Error on JVM startup", e);
         System.exit(1);
      }
   }
}