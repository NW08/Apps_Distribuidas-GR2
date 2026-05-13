package Clase_06.RMI.Server.Classes;


import Clase_06.RMI.Shared.Person;
import Clase_06.RMI.Shared.RemoteServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Optional;

public class Server extends UnicastRemoteObject implements RemoteServer {
   private static final Map<Integer, Person> PERSON_DATABASE = Map.of(
         1, new Person(1, "Jose Vargas", "josev@gmail.com", "Administrator", 5000.00),
         2, new Person(2, "Maria Perez", "maria@gmail.com", "Employee", 2000.00),
         3, new Person(3, "Juan Gomez", "juan@gmail.com", "Technician", 3000.00)
   );

   public Server() throws RemoteException {
      super();
   }

   @Override
   public String findPersonById(int id) throws RemoteException {
      return Optional.ofNullable(PERSON_DATABASE.get(id))
            .map(this::formatPersonData)
            .orElse("Person not found with id: " + id);
   }

   private String formatPersonData(Person person) {
      return """
            %s
            Email: %s
            Role: %s
            Salary: %.2f
            """.formatted(person.name(), person.email(), person.role(), person.salary());
   }
}