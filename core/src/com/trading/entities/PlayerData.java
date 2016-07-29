package com.trading.entities;

import com.badlogic.gdx.math.Vector2;

public class PlayerData {
	
	public Vector2 pos;
	public int pId;
	public int health;
	public int stamina;
	
	public PlayerData() {
		pos = new Vector2(0,0);
		pId = -1;
		health = 100;
		stamina = 100;
	}
	
	public PlayerData(Vector2 pos, int pId, int health, int stamina) {
		this.pos = pos;
		this.pId = pId;
		this.health = health;
		this.stamina = stamina;
	}
}
