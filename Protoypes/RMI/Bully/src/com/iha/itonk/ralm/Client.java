package com.iha.itonk.ralm;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Client implements Bully {
	
	private List<ClientDTO> higherThanMe = new ArrayList<ClientDTO>(), lowerThanMe = new ArrayList<ClientDTO>();
	private ClientDTO leader;
	private ClientDTO clientInfo;

    public Client(ClientDTO clientDTO, ArrayList<ClientDTO> clients) {
    	clientInfo = clientDTO;
    	splitList(clientDTO, clients);
    	System.out.println("Running with host "+System.getProperty("java.rmi.server.hostname"));
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
				Bully stub = (Bully) registry.lookup("Bully"); //Find one to elect
				stub.StartElection(); //Are you there?
				foundHigher = true; // If no exception was thrown
			} 
			catch (ConnectException ce)
			{
				System.out.println("Could not connect to " + client.name + ". " +
						"Assuming it's not running");
			}
			catch(Exception e)
			{
				System.err.println("ERROR while connecting to " + client.name + "!");
				e.printStackTrace();
			}
		}
		
		if(!foundHigher)
		{
			System.out.println("Noone higher than me. I should be leader");
			leader = clientInfo;
			// Announce that I'm the leader now!
			for(ClientDTO client : lowerThanMe) {
				try 
				{
					Registry registry = LocateRegistry.getRegistry(client.host, client.port);
					Bully stub = (Bully) registry.lookup("Bully"); //Find one to elect
					stub.Announce(clientInfo); 
				}
				catch (ConnectException e) 
				{
					System.out.println("Can't connect to " + client.name + ". It just misses the announcement.");
				}
				catch(Exception e)
				{
					System.err.println("ERROR while connecting/announcing to "+client.name);
					e.printStackTrace();					
				}
			}
			if(lowerThanMe.size() == 0)
			{
				System.out.println("I am last client and leader");
			}
		}
	}

	public String DoTask() {
		return clientInfo.task;
	}
	
	public void RunTask() {
		try {
			if(leader == null || leader == clientInfo)
			{
				return;
			}
				
			Registry registry = LocateRegistry.getRegistry(leader.host, leader.port);
			Bully leaderBully = (Bully) registry.lookup("Bully");
			String task = leaderBully.DoTask();
			System.out.println("Leader (" + leader.name + ") told us to: " + task);
		} 
		catch (RemoteException e) {
			System.out.println("Can't find leader! Will start an election");
			StartElection();
		} 
		catch (Exception e) {
			System.err.println("ERROR while running RunTask()");
			e.printStackTrace();
		}
		
	}

	public void Announce(ClientDTO newLeader) {
		leader = newLeader;
	}
}
