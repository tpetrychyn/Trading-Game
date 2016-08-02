package com.trading.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Timer.Task;
import com.trading.game.Instance;

public class WorldActor extends Actor {

	Sprite sprite;
	Instance instance;
	public Color color = Color.WHITE;
	public float colorTime = 0;
	
	public int id;
	public float realX;
	public float realY;
	public float realWidth;
	public float realHeight;
	public float offsetX;
	public float offsetY;
	
	public WorldActor() {
	}
	
	public WorldActor(Texture image, float x, float y, Instance instance, int id, float scale) {
		this.id = id;

		sprite = new Sprite(image);
		this.instance = instance;
		instance.setWorldPosition(this, new Vector2(x,y));
        
        setScale(scale);
	}
	
	public void setScale(float scale) {
		setWidth(sprite.getWidth() * scale);
		setHeight(sprite.getHeight() * scale);
		sprite.scale(scale);
		sprite.setScale(scale, scale);
	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		//spawn actors off screen to save memory?
		if (getX() < 0 && getY() < 0) {
			return;
		}
		
		if (colorTime > 0) {
			sprite.setColor(sprite.getColor().mul(color));
			//sprite.setColor(color);
			colorTime -= Gdx.graphics.getDeltaTime();
		}
		
		sprite.setScale(0.5f);
		sprite.setPosition(getX()-offsetX, getY()-offsetY);
		sprite.draw(batch);
		//batch.draw(sprite, getX(), getY(), getWidth(), getHeight());
	}
}
