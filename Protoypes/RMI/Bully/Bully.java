package ITONK.Bully;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Bully extends Remote {
	void StartElection(Client host) throws RemoteException;
	void ElectionBully(Client host) throws RemoteException;
	void Response(Client host) throws RemoteException;
	void Announce(Client host) throws RemoteException;
}
