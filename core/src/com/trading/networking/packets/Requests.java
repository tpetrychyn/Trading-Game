package com.trading.networking.packets;

public enum Requests {
	newConnection(0), getNpcs(1);
	
	private final int value;
	private Requests(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
