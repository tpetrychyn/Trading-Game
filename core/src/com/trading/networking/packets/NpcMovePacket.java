package com.trading.networking.packets;


public class NpcMovePacket {

	public float x;
	public float y;
	public int npcId;
	public String name;

	public NpcMovePacket() {
	    this.x = 0;
	    this.y = 0;
	    this.npcId = -1;
	    this.name = "";
	}

	public NpcMovePacket(float x, float y, int npcId, String name) {
	    this.x = x;
	    this.y = y;
	    this.npcId = npcId;
	    this.name = name;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
}
