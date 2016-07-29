package com.trading.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.trading.game.Util;
import com.trading.networking.GameServer;
import com.trading.networking.GameWorld;

public class NpcController extends Npc {

	public NpcController(GameWorld world) {
		super(world);
		// TODO Auto-generated constructor stub
	}
	
	public NpcController(Texture image, float x, float y, GameWorld world, int id, float scale) {
		super(image, x, y, world, id, scale);
	}
	float lastUpdate = 0;
	public void draw(Batch batch, float alpha) {
		
		lastUpdate += Gdx.graphics.getDeltaTime();
		
		Vector2 oldPos = new Vector2(getX(), getY());
        setX(getX() + velocity.x * Gdx.graphics.getDeltaTime());
        setY(getY() + velocity.y * Gdx.graphics.getDeltaTime());
        
        Vector2 newPos = new Vector2(getX(), getY());
        if (world.getWorldPosition(newPos).x < 0 || world.getWorldPosition(newPos).y < 0.2
        		|| world.getWorldPosition(newPos).x > 99.8 || world.getWorldPosition(newPos).y > 100
        		|| world.isCellBlocked(world.getWorldPosition(newPos).x, world.getWorldPosition(newPos).y)
        		|| world.actorCollision(this)){
        	setY(oldPos.y);
        	setX(oldPos.x);
        	velocity.x = 0f;
    		velocity.y = 0f;
        } 
        
        if (maxBounds != null && minBounds != null) {
        	if (world.getWorldPosition(newPos).x < minBounds.x || world.getWorldPosition(newPos).y < minBounds.y
        			|| world.getWorldPosition(newPos).x > maxBounds.x || world.getWorldPosition(newPos).y > maxBounds.y) {
        		setX(oldPos.x);
        		setY(oldPos.y);
        		velocity.x = 0f;
        		velocity.y = 0f;
        	}		
        }
        
        stateTime += Gdx.graphics.getDeltaTime();
		
        //if npc is moving use an animated sprite, if not use the static sprites
        if (Math.abs(velocity.x) > 0 || Math.abs(velocity.y) > 0)
        	sprite = new Sprite(walkAnimations[direction.getValue()].getKeyFrame(stateTime, true));
        else
        	sprite = new Sprite(walkAnimations[direction.getValue() + 4].getKeyFrame(stateTime, true));
        
		if (getX() != oldPos.x || getY() != oldPos.y && lastUpdate > 1) {
			GameServer.updateActor(id);
			lastUpdate = 0;
		}	
		
		super.draw(batch, alpha);
	}
	
	public void setBounds(int minX, int minY, int maxX, int maxY) {
		minBounds = new Vector2(minX, minY);
		maxBounds = new Vector2(maxX, maxY);
	}
	
	void randomWalk() {
		int randomWalk = Util.randomRange(0, 8);
		switch (randomWalk) {
		case 0:
			velocity.x = 20f;
			velocity.y = 0f;
			direction = Direction.EAST;
			break;
		case 1:
			velocity.x = -20f;
			velocity.y = 0f;
			direction = Direction.WEST;
			break;
		case 2:
			velocity.y = 20f;
			velocity.x = 0f;
			direction = Direction.NORTH;
			break;
		case 3: 
			velocity.y = -20f;
			velocity.x = 0f;
			direction = Direction.SOUTH;
			break;
		default:
			velocity.x = 0;
			velocity.y = 0;
			break;
		}
	}
	
	public void startRandomWalk(int delay) {
		randomWalk = new Timer().scheduleTask(new Task(){
		    @Override
		    public void run() {
		        randomWalk();
		    }
		}, 0, Util.randomRange(1, delay));
	}
	
	public void stopRandomWalk() {
		randomWalk.cancel();
		velocity.x = 0;
		velocity.y = 0;
	}

}
