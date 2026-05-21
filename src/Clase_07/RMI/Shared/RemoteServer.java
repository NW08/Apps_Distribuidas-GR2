package Clase_07.RMI.Shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteServer extends Remote {
   String findUserID(int id) throws RemoteException;
}