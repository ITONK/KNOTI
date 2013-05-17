package com.iha.itonk.ralm;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Bully extends Remote {
	String DoTask() throws RemoteException;
	void StartElection() throws RemoteException;
	void Announce(ClientDTO newLeader) throws RemoteException;
}
