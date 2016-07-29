package com.trading.networking.packets;

public class Disconnection {
	public int pId;
	
	public Disconnection() {
		pId = -1;
	}
	
	public Disconnection(int id) {
		pId = id;
	}
}
