package Clase_07.RMI.Client;

import Clase_07.RMI.Client.Classes.RemoteClient;
import lombok.extern.slf4j.Slf4j;

import java.util.InputMismatchException;
import java.util.Scanner;

@Slf4j
public class ClientLauncher {
   private static final String HOST = "localhost";
   private static final int PORT = 1099;
   private static final String SERVICE_NAME = "RemoteServer";

   static void main() {
      try (Scanner sc = new Scanner(System.in)) {
         RemoteClient client = new RemoteClient(HOST, PORT, SERVICE_NAME);

         log.info("--- Starting Remote Queries ---");
         log.info("Enter 0 to exit.");

         int id = -1;

         while (id != 0) {
            System.out.print("Enter any ID between 1 - 39 (or 0 to exit): ");

            try {
               id = sc.nextInt();
               System.out.println();

               if (id == 0) log.info("Exiting application...");
               else if (id >= 1 && id <= 39) {
                  String result = client.findPerson(id);
                  log.info("Result for ID {}:\n\n{}", id, result);
               } else log.warn("ID out of range. Please enter a valid ID.");


            } catch (InputMismatchException e) {
               log.error("Invalid input. Please enter a number.");
               sc.nextLine();
            }
         }

      } catch (Exception e) {
         log.error("Failed to initialize the RMI Client application", e);
      }
   }
}