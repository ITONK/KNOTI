package com.iha.itonk.ralm;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("serial")
public class Client extends ClientDTO implements Bully {
	
	private List<ClientDTO> higherThanMe = new ArrayList<ClientDTO>(), lowerThanMe = new ArrayList<ClientDTO>();
	private ClientDTO leader;

    public Client(ClientDTO clientDTO, ArrayList<ClientDTO> clients) {
    	super(clientDTO.host, clientDTO.port, clientDTO.name);
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
		System.out.println("Starting election");

		for(ClientDTO client : higherThanMe) {
			try 
			{
				System.out.println("Someone is higher than me");
				Registry registry = LocateRegistry.getRegistry(client.host, client.port);
				Bully stub = (Bully) registry.lookup(client.name); //Find one to elect
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
			System.out.println("Noone higher than me. I should be leader");
			// Announce that I'm the leader now!
			for(ClientDTO client : lowerThanMe) {
				try 
				{
					Registry registry = LocateRegistry.getRegistry(client.host, client.port);
					Bully stub = (Bully) registry.lookup(client.name); //Find one to elect
					stub.Announce(this); //Are you there?
				}
				catch (Exception e) 
				{
					System.out.println("Stub is bad");
					e.printStackTrace(); // First will fail here
				}
			}
			if(lowerThanMe.size() == 0)
			{
				System.out.println("I am last client and leader");
				leader = this.leader;
			}
		}
	}

	public String DoTask() {
		return "Go right";
	}
	
	public void RunTask() {
		try {
			if(leader == null)
			{
				return;
			}
				
			Registry registry = LocateRegistry.getRegistry(leader.host, leader.port);
			String name = leader.name;
			Bully leader = (Bully) registry.lookup(name);
			String task = leader.DoTask();
			System.out.println("Leader told us to: " + task);
		} 
		catch (RemoteException e) {
			//TODO: Some logging of exception
			System.out.println("Starting election");
			StartElection();
		} 
		catch (NotBoundException e) {
			// TODO Auto-generated catch block
			// If we're here, the programmer did something baaaad...
			System.out.println("Something went wrong during RunTask()");
			e.printStackTrace();
		}
		
	}

	public void Announce(ClientDTO newLeader) {
		this.leader = newLeader;
	}
}
