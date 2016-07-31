package com.trading.networking.packets;

import com.trading.entities.Direction;

public class WorldObjectPacket {

	public float x;
	public float y;
	public int id; //object id in the world
	public String type;
	public int typeId; //id of which tree,rock, etc to load

	public WorldObjectPacket() {
	    this.x = 0;
	    this.y = 0;
	    this.id = -1;
	    this.type = null;
	}

	public WorldObjectPacket(float x, float y, int id, String type, int typeId) {
	    this.x = x;
	    this.y = y;
	    this.id = id;
	    this.type = type;
	    this.typeId = typeId;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
}
