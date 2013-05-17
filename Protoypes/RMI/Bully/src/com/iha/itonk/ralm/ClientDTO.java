package com.iha.itonk.ralm;

public class ClientDTO {
	public ClientDTO(String host, int port)
	{
		this.host = host;
		this.port = port;
	}
	
	public String host;
	public int port;
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof ClientDTO)){
			return false;
		} else {
			ClientDTO o = (ClientDTO) other;
			return o.host.equals(this.host) && o.port == this.port;
		}
	}
}
