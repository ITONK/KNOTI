package com.iha.itonk.ralm;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
	
public class Main {
	
    public static void main(String args[]) {
    	if(args.length < 1) {
    		System.err.println("You need at least one argument - the port!!");
    		return;
    	}
	
		try {
			int port = Integer.parseInt(args[0]);
			String IP = System.getProperty("java.rmi.server.hostname"); //InetAddress.getLocalHost().getHostAddress();
			String name = args[2];
			String task = (args.length > 3 ? args[3] : "Stay still");
			
			Registry registry = LocateRegistry.createRegistry(port);
			
			System.out.println("IP host: "+IP + ", port: " + port + " , name: " + name);
			
			ClientDTO client_this = new ClientDTO(IP, port, name);
			client_this.task = task;
			
			ArrayList<ClientDTO> clients = new ArrayList<ClientDTO>();
			//Read from file
			List<String> clientStrs = Files.readAllLines(Paths.get("clients.txt"), Charset.defaultCharset());
	
			if(clientStrs.size() < 1)
				System.err.println("Add more clients! One is not enough");
			
			for(String clientStr : clientStrs) {
				String[] clientInfo = clientStr.split(" ");
				clients.add(new ClientDTO(clientInfo[0], Integer.parseInt(clientInfo[1]), clientInfo[2]));
			}
			
			if(!clients.contains(client_this)) {
				System.err.println("You aren't in the list!!");
				return;
			}

			Client client = new Client(client_this, clients);
			Bully stub = (Bully) UnicastRemoteObject.exportObject(client, 0);
			
			registry.rebind("Bully", stub); //Bind the remote object's stub in the registry

			client.StartElection(); // TODO: Find leader in better way...
			
			while(true) {
				client.RunTask();
				Thread.sleep(1000);
			}
		} 			
		catch(java.io.NotSerializableException e)
		{
			System.err.println("Main: something went wrong!" + "\r" + "Quiting this thread");
			//return;
		}
		catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
		    e.printStackTrace();
		}
    }
}
