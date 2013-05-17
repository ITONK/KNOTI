package com.iha.itonk.ralm;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Client extends ClientDTO implements Bully {
	
	private List<ClientDTO> higherThanMe=new ArrayList<ClientDTO>(), lowerThanMe = new ArrayList<ClientDTO>();
	private ClientDTO leader;

    public Client(ClientDTO clientDTO, ArrayList<ClientDTO> clients) {
    	super(clientDTO.host, clientDTO.port);
    	splitList(clientDTO, clients);
    	StartElection(); // TODO: Find leader in better way...
	}

	private void splitList(ClientDTO client_this, ArrayList<ClientDTO> clients) {
		// TODO: Split list
		Iterator<ClientDTO> clientsIterator = clients.iterator();
		
		while(clientsIterator.hasNext())
		{
			ClientDTO nextClient = clientsIterator.next();
			if(nextClient.equals(client_this))
				break;
			else
				higherThanMe.add(nextClient);
		}
		
		while(clientsIterator.hasNext())
		{
			ClientDTO nextClient = clientsIterator.next();
			if(nextClient.equals(client_this))
				break;
			else
				lowerThanMe.add(nextClient);
		}
	}

	public void StartElection() {

		boolean foundHigher = false;

		for(ClientDTO client : higherThanMe) {
			try 
			{
				Registry registry = LocateRegistry.getRegistry(client.host, client.port);
				Bully stub = (Bully) registry.lookup("Bully"); //Find one to elect
				stub.StartElection(); //Are you there?
				foundHigher = true; // If no exception was thrown
			} 
			catch (Exception e) 
			{
				System.err.println("Client exception: " + e.toString());
			}
		}
		
		if(!foundHigher)
		{
			System.out.println("We didn't find any higher");
			// Announce that I'm the leader now!
			for(ClientDTO client : lowerThanMe) {
				try 
				{
					Registry registry = LocateRegistry.getRegistry(client.host, client.port);
					Bully stub = (Bully) registry.lookup("Bully"); //Find one to elect
					stub.Announce(this); //Are you there?
				} 
				catch (Exception e) 
				{
					e.printStackTrace(); // First will fail here
				}
			}
		}
	}

	public String DoTask() {
		return "Go right";
	}
	
	public void RunTask() {
		try {
			if(leader == null)
				return;
			Registry registry = LocateRegistry.getRegistry(leader.host, leader.port);
			Bully leader = (Bully) registry.lookup("Bully");
			String task = leader.DoTask();
			System.out.println("Leader told us to: "+task);
		} catch (RemoteException e) {
			//TODO: Some logging of exception
			StartElection();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			// If we're here, the programmer did something baaaad...
			e.printStackTrace();
		}
		
	}

	public void Announce(ClientDTO newLeader) {
		this.leader = newLeader;
	}
}
