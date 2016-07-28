package com.trading.game;

import com.badlogic.gdx.math.Vector2;

public class NpcMovePacket {

	public Vector2 pos;
	public int id;

	public NpcMovePacket() {
	    pos = new Vector2(0, 0);
	    id = -1;
	}

	public NpcMovePacket(Vector2 v, int id) {
	    pos = v;
	    this.id = id;
	}
	
	public Vector2 getPos() {
		return pos;
	}
}
