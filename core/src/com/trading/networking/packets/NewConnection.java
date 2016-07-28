package com.trading.networking.packets;

import com.badlogic.gdx.math.Vector2;

public class NewConnection {
	public Vector2 pos;
	public int clientId;
	
	public NewConnection() {
		clientId = -1;
		pos = new Vector2(0,0);
	}

	public NewConnection(int id, Vector2 pos) {
		this.pos = pos;
		this.clientId = id;
	}
}
