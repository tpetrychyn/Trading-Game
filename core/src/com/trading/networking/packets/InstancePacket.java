package com.trading.networking.packets;

public class InstancePacket {
	public int id;
	public String name;
	public int width;
	public int height;
	
	public InstancePacket() {
		id = -1;
		name = null;
		width = 0;
		height = 0;
	}
	
	public InstancePacket(int id) {
		this.id = id;
	}
	
	public InstancePacket(int id, String name, int width, int height) {
		this.id = id;
		this.name = name;
		this.width = width;
		this.height = height;
	}
}
