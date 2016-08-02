package com.trading.entities;

public class NpcData {
	public int health;
	public int stamina;
	
	public NpcData() {
		health = 100;
		stamina = 100;
	}
	
	public NpcData(int health, int stamina) {
		this.health = health;
		this.stamina = stamina;
	}
}
