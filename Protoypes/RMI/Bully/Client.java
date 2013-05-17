package ITONK.Bully;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client implements Bully {

    private Client() {
		candidates = new List<Client>();
	}

	public String IdName;
	private List<Client> candidates;

	public void StartElection() {
		//Find everyone greater
		List<Client> list = new List<Client>();
		//Read from file
		StreamReader reader = new StreamReader("clients.txt", rw_options); //About right?

		for(reader.line : client) {
			list.add(client);
		}

		for(list : client) {
			try 
			{
				Registry registry = LocateRegistry.getRegistry(client);
				Bully stub = (Bully) registry.lookup(client.IdName); //Find one to elect
				String response = stub.ElectionBully(this); //Are you there?
			} 
			catch (Exception e) 
			{
				System.err.println("Client exception: " + e.toString());
				e.printStackTrace();
			}
		}

		Integer waitingTime = 4000;
		Thread.Wait(waitingTime);

		if(candidates.count() == 0) {
			Announce(this);
		}
	}

	public void ElectionBully(Client host) {
		try 
		{
			Registry registry = LocateRegistry.getRegistry(host);
			Bully stub = (Bully) registry.lookup(client.IdName); //Find the one electing
			String response = stub.Response(this.IdName); //I am greater
		} 
		catch (Exception e) 
		{
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}

		StartElection();
	}

	public void Respose(Client host) {
		candidates.add(host);
	}

	public void DoTask() {
		
	}

    public static void main(String[] args) {

	String host = (args.length < 1) ? null : args[0];
		//Something must be running down here...
    }
}
