package com.trading.networking.packets;

public class ClientRequest {
	public Requests request;
	
	public ClientRequest() {
		
	}
	
	public ClientRequest(Requests getinstance) {
		this.request = getinstance;
	}
}
