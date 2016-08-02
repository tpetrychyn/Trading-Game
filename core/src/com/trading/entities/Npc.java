package com.trading.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.trading.game.Instance;

public class Npc extends Character {
	
	public CharacterStats npcData;
	
	public Npc(Instance instance) {
		this.instance = instance;
		
        setName("");
	}
	
	public Npc(Texture image, float x, float y, Instance instance, int id, float scale, String name) {
		this.id = id;
		this.instance = instance;
		setPosition(x, y);
		
		npcData = new CharacterStats(100, 100);
		
        setName(name);
        font.setColor(Color.WHITE);
	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		super.draw(batch, alpha);
	}
}
