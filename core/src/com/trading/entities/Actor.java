package com.trading.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Actor extends Sprite {
	Texture image;
	Sprite sprite;
	
	public Actor(Texture img) {
		image = img;
		sprite = new Sprite(image);
	}
	
	public void setSprite(Sprite s) {
		sprite.set(s);
	}
	
	public Sprite getSprite() {
		return sprite;
	}
}
