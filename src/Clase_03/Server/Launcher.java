package Clase_03.Server;

import Clase_03.Server.model.Calculator;

public class Launcher {
   private static final int SERVER_PORT = 7542;

   static void main() {
      Calculator calculator = new Calculator();
      Server server = new Server(calculator);

      Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
      server.start(SERVER_PORT);
   }
}