package com.iha.itonk.ralm;

import java.io.StringReader;
import java.net.InetAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
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
			String IP = InetAddress.getLocalHost().getHostAddress();
			
			Registry registry = LocateRegistry.createRegistry(port);
			
			System.out.println("IP host: "+IP);
			
			
			ClientDTO client_this = new ClientDTO(IP, port);
			
			//Find everyone greater
			ArrayList<ClientDTO> clients = new ArrayList<ClientDTO>();
			//Read from file
			List<String> clientStrs = Files.readAllLines(Paths.get("/home/limro/clients.txt"/*args[1]*/), Charset.defaultCharset());
	
			if(clientStrs.size() < 1)
				System.err.println("HEY");
			
			for(String clientStr : clientStrs) {
				String[] clientInfo = clientStr.split(" ");
				clients.add(new ClientDTO(clientInfo[0], Integer.parseInt(clientInfo[1])));
			}
			
			if(!clients.contains(client_this)) {
				System.err.println("You aren't in the list!!");
				return;
			}
			
			Client client = new Client(client_this, clients);
		    Bully stub = (Bully) UnicastRemoteObject.exportObject(client, 0);
	
		    // Bind the remote object's stub in the registry
		    registry.rebind("Bully", stub);
			
			while(true) {
				client.RunTask();
				Thread.sleep(1000);
			}
			
		} catch (Exception e) {
			
			System.err.println("Server exception: " + e.toString());
		    e.printStackTrace();
		    
		}
    }
}
