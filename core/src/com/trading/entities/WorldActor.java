package com.trading.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Timer.Task;
import com.trading.game.Instance;

public class WorldActor extends Actor {

	Sprite sprite;
	Instance instance;
	Vector2 velocity = new Vector2();
	Animation[] walkAnimations;
	Animation walk;
	Task randomWalk;
	Vector2 minBounds;
	Vector2 maxBounds;
	public Direction direction = Direction.SOUTH;
	public int id;
	
	public float realX;
	public float realY;
	public float realWidth;
	public float realHeight;
	
	public boolean isMoving = false;
	
	public WorldActor() {
	}
	
	public WorldActor(Texture image, float x, float y, Instance instance, int id, float scale) {
		this.id = id;

		sprite = new Sprite(image);
		this.instance = instance;
		instance.setWorldPosition(this, new Vector2(x,y));
		walkAnimations = new Animation[8];
		Animator a = new Animator(9, 4, "male_walk.png");
        walkAnimations[0] = a.addAnimation(1, 7);
        walkAnimations[1] = a.addAnimation(10, 7);
        walkAnimations[2] = a.addAnimation(19, 7);
        walkAnimations[3] = a.addAnimation(28, 7);

        walkAnimations[4] = a.addAnimation(0, 1);
        walkAnimations[5] = a.addAnimation(9, 1);
        walkAnimations[6] = a.addAnimation(18, 1);
        walkAnimations[7] = a.addAnimation(27, 1);
        
        setScale(scale);
	}
	
	public void setScale(float scale) {
		setWidth(sprite.getWidth() * scale);
		setHeight(sprite.getHeight() * scale);
	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		//spawn actors off screen to save memory?
		if (getX() < 0 && getY() < 0)
			return;
			
		batch.draw(sprite, getX(), getY(), getWidth(), getHeight());
		
	}
}
