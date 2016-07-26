package com.trading.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.trading.game.GameWorld;
import com.trading.game.Util;

public class Npc extends Actor {
	
	Sprite sprite;
	GameWorld world;
	Vector2 velocity = new Vector2();;
	Animation[] walkAnimations;
	Animation walk;
	Task randomWalk;
	Vector2 minBounds;
	Vector2 maxBounds;
	
	float stateTime;
	
	public Npc(Texture image, float x, float y, GameWorld world) {
		sprite = new Sprite(image);
		this.world = world;
		world.setWorldPosition(this, new Vector2(x,y));
		setWidth(sprite.getWidth());
		setHeight(sprite.getHeight());
		Animator a = new Animator(9, 4, "male_walkcycle.png");
        walk = a.addAnimation(1, 7);
	}
	
	public void startRandomWalk(int delay) {
		randomWalk = new Timer().scheduleTask(new Task(){
		    @Override
		    public void run() {
		        randomWalk();
		    }
		}, 0, delay);
	}
	
	public void stopRandomWalk() {
		randomWalk.cancel();
		velocity.x = 0;
		velocity.y = 0;
	}
	
	public void Draw(SpriteBatch batch) {
		stateTime += Gdx.graphics.getDeltaTime();
		sprite = new Sprite(walk.getKeyFrame(stateTime, true));
		
		Vector2 oldPos = new Vector2(getX(), getY());
        setX(getX() + velocity.x * Gdx.graphics.getDeltaTime());
        setY(getY() + velocity.y * Gdx.graphics.getDeltaTime());
        
        Vector2 newPos = new Vector2(getX(), getY());
        if (world.getWorldPosition(newPos).x < 0 || world.getWorldPosition(newPos).y < 0.2
        		|| world.getWorldPosition(newPos).x > 99.8 || world.getWorldPosition(newPos).y > 100
        		|| world.isCellBlocked(world.getWorldPosition(newPos).x, world.getWorldPosition(newPos).y)){
        	setY(oldPos.y);
        	setX(oldPos.x);
        }
        
        if (maxBounds != null && minBounds != null) {
        	if (world.getWorldPosition(newPos).x < minBounds.x || world.getWorldPosition(newPos).y < minBounds.y
        			|| world.getWorldPosition(newPos).x > maxBounds.x || world.getWorldPosition(newPos).y > maxBounds.y) {
        		setX(oldPos.x);
        		setY(oldPos.y);
        	}		
        }
        
		batch.draw(sprite, getX(), getY(), sprite.getWidth(), sprite.getHeight());
	}
	
	
	public void setBounds(int minX, int minY, int maxX, int maxY) {
		minBounds = new Vector2(minX, minY);
		maxBounds = new Vector2(maxX, maxY);
	}
	
	void randomWalk() {
		int randomWalk = Util.randomRange(0, 5);
		System.out.println(randomWalk);
		switch (randomWalk) {
		case 0:
			velocity.x = 20f;
			break;
		case 1:
			velocity.x = -20f;
			velocity.y = 20f;
			break;
		case 2:
			velocity.y = 20f;
			break;
		case 3: 
			velocity.y = -20f;
			break;
		default:
			velocity.x = 0;
			velocity.y = 0;
			break;
		}
	}
	
	public void addAnimation(Texture sheet, int columns, int rows) {
		
	}
}
