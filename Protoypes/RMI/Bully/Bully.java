package ITONK.Bully;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Bully extends Remote {
	void DoTask() throws RemoteException;
	void StartElection() throws RemoteException;
	void Announce(Client host) throws RemoteException;
}
