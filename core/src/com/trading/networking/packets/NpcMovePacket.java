package com.trading.networking.packets;

import com.badlogic.gdx.math.Vector2;

public class NpcMovePacket {

	public Vector2 pos;
	public int npcId;

	public NpcMovePacket() {
	    this.pos = new Vector2(0, 0);
	    this.npcId = -1;
	}

	public NpcMovePacket(Vector2 v, int npcId) {
	    this.pos = v;
	    this.npcId = npcId;
	}
	
	public Vector2 getPos() {
		return pos;
	}
}
