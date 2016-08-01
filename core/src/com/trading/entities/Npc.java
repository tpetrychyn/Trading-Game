package com.trading.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.trading.game.Instance;

public class Npc extends Character {
	
	public NpcData npcData;
	
	public Npc(Instance instance) {
		this.instance = instance;
		
        setName("");
	}
	
	public Npc(Texture image, float x, float y, Instance instance, int id, float scale, String name) {
		this.id = id;
		this.instance = instance;
		setPosition(x, y);
		
		npcData = new NpcData(100, 100);
		
        setName(name);
	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		stateTime += Gdx.graphics.getDeltaTime();
		lastMoved += Gdx.graphics.getDeltaTime();
		sprite = new Sprite(getCurrentTexture(stateTime));
		
		
		font.setColor(Color.WHITE);
		font.getData().setScale(0.5f);
		font.draw(batch, getName(), getX() + getWidth()/2 - getName().length()*3/2, getY()+getHeight() + 30);
		font.draw(batch, "Health: " + npcData.health, getX() + getWidth()/2 - 20, getY()+getHeight() + 20);
		font.draw(batch, "Stamina: " + npcData.stamina, getX() + getWidth()/2 - 20, getY()+getHeight()+10);
		
		super.draw(batch, alpha);
		
		if (lastMoved < 0.1)
			isMoving = true;
		else
			isMoving = false;
	}
}
