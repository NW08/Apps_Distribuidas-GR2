package Clase_06.RMI.Shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteServer extends Remote {
   String findPersonById(int id) throws RemoteException;
}