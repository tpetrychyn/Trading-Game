package com.trading.game;

import com.badlogic.gdx.math.Vector2;

public class PlayerMovePacket {

	public Vector2 pos;
	public int clientID;

	public PlayerMovePacket() {
	    pos = new Vector2(0, 0);
	    clientID = -1;
	}

	public PlayerMovePacket(Vector2 v, int id) {
	    pos = v;
	    clientID = id;
	}
	
	public Vector2 getPos() {
		return pos;
	}
}
