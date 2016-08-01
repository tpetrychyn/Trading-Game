package com.trading.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.trading.entities.WorldObjects.TreePrefab;
import com.trading.game.Instance;

public class Tree extends WorldActor {
	
	public Instance instance;
	public String type;

	public Tree(float x, float y, Instance instance, TreePrefab prefab) {
		Texture t = new Texture("Objects/"+prefab.file);
		sprite = new Sprite(t);
		this.instance = instance;
		setPosition(x, y);
		
		setScale(prefab.scale);
		setOrigin(getWidth()/2, getHeight()/2);
		
		realX = getX() +  (prefab.offsetX * prefab.scale);
		realY = getY() + (prefab.offsetY * prefab.scale);
		realWidth = (prefab.width * prefab.scale);
		realHeight = (prefab.height * prefab.scale);
		if (prefab.color.equals("red"))
			sprite.setColor(Color.RED);
		if (prefab.color.equals("blue"))
			sprite.setColor(Color.BLUE);
		
		this.type = prefab.type;
	}
	
	public void draw(Batch batch, float alpha) {
		super.draw(batch, alpha);
	}
}
