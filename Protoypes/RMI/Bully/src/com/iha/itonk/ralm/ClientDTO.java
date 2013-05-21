package com.iha.itonk.ralm;

import java.rmi.Remote;

public class ClientDTO implements java.io.Serializable {
	public ClientDTO(String host, int port, String name)
	{
		this.host = host;
		this.port = port;
		this.name = name;
	}
	
	public String name;
	public String host;
	public int port;
	public String task;
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof ClientDTO)){
			return false;
		} else {
			ClientDTO o = (ClientDTO) other;
			return o.host.equals(this.host) && o.port == this.port && o.name.equals(this.name);
		}
	}
}
