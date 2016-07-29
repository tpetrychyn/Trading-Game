package com.trading.networking.packets;

import com.trading.entities.PlayerData;

public class PlayerDataPacket {
	public int pId;
	public PlayerData playerData;
	
	public PlayerDataPacket() {
		playerData = new PlayerData();
		pId = -1;
	}
}
