package com.trading.networking.packets;

import com.badlogic.gdx.math.Vector2;
import com.trading.entities.PlayerData;

public class InstancePacket {
	
	public int id;
	public int clientId;
	public String action;
	public PlayerData playerData;
	
	public InstancePacket() {
		playerData = new PlayerData();
		id = -1;
		action = "";
	}
	
	public InstancePacket(int id, String action) {
		playerData = new PlayerData();
		this.id = id;
		this.action = action;
	}
	
	public InstancePacket(int id, String action, Vector2 pos) {
		this.id = id;
		this.action = action;
		playerData = new PlayerData(pos, id, 100, 100);
	}
	
	public int getId() {
		return id;
	}
}
