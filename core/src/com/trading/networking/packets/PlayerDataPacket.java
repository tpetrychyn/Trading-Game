package com.trading.networking.packets;

import com.badlogic.gdx.math.Vector2;
import com.trading.entities.PlayerData;

public class PlayerDataPacket {
	public int instance;
	public int id;
	public PlayerData playerData;
	
	public PlayerDataPacket() {
		playerData = new PlayerData();
		id = -1;
	}
	
	public PlayerDataPacket(int id, int instance, Vector2 pos) {
		this.id = id;
		this.instance = instance;
		playerData = new PlayerData(pos, id, 100, 100);
	}
}
