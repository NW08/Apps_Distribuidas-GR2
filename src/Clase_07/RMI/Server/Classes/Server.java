package Clase_07.RMI.Server.Classes;

import Clase_07.RMI.Shared.Person;
import Clase_07.RMI.Shared.RemoteServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements RemoteServer {
   private final Repository repository;

   public Server(Repository repository) throws RemoteException {
      super();
      this.repository = repository;
   }

   @Override
   public String findUserID(int id) throws RemoteException {
      return repository.findById(id)
            .map(this::formatPersonData)
            .orElse("Person not found with id: " + id);
   }

   private String formatPersonData(Person person) {
      return """
            %s
            Email: %s
            Role: %s
            Salary: %.2f
            """.formatted(person.nombre(), person.correo(), person.cargo(), person.sueldo());
   }
}