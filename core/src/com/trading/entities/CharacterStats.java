package com.trading.entities;

public class CharacterStats {
	public int health;
	public int stamina;
	
	public CharacterStats() {
		health = 100;
		stamina = 100;
	}
	
	public CharacterStats(int health, int stamina) {
		this.health = health;
		this.stamina = stamina;
	}
}
