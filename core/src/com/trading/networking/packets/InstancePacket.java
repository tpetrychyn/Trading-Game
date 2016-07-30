package com.trading.networking.packets;

public class InstancePacket {
	
	public int id;
	public int clientId;
	public String action;
	
	public InstancePacket() {
		id = -1;
		action = "";
	}
	
	public InstancePacket(int id, String action) {
		this.id = id;
		this.action = action;
	}
	
	public int getId() {
		return id;
	}
}
