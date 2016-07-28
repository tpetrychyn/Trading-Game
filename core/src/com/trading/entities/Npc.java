package com.trading.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
	Vector2 velocity = new Vector2();
	Animation[] walkAnimations;
	Animation walk;
	Task randomWalk;
	Vector2 minBounds;
	Vector2 maxBounds;
	Direction direction = Direction.SOUTH;
	private float scale;
	public int id;
	public String name;
	BitmapFont font;
	
	float stateTime;
	
	public Npc(Texture image, float x, float y, GameWorld world, int id, float scale) {
		this.id = id;
		this.scale = scale;
		font = new BitmapFont();
		font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

		sprite = new Sprite(image);
		this.world = world;
		world.setWorldPosition(this, new Vector2(x,y));
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
	
	public void Draw(SpriteBatch batch) {
		stateTime += Gdx.graphics.getDeltaTime();
		
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
        
        //if npc is moving use an animated sprite, if not use the static sprites
        if (Math.abs(velocity.x) > 0 || Math.abs(velocity.y) > 0)
        	sprite = new Sprite(walkAnimations[direction.getValue()].getKeyFrame(stateTime, true));
        else
        	sprite = new Sprite(walkAnimations[direction.getValue() + 4].getKeyFrame(stateTime, true));
        
        setWidth(sprite.getWidth()*scale);
		setHeight(sprite.getHeight()*scale);
		batch.draw(sprite, getX(), getY(), Size().x, Size().y);
		font.setColor(Color.WHITE);
		font.getData().setScale(0.5f);
		font.draw(batch, name, getX() + Size().x/2 - name.length()*7/2, getY()+Size().y + 10);
	}
	
	public void setBounds(int minX, int minY, int maxX, int maxY) {
		minBounds = new Vector2(minX, minY);
		maxBounds = new Vector2(maxX, maxY);
	}
	
	public Vector2 Size() {
		return new Vector2(getWidth(), getHeight());
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
	
	public void addAnimation(Texture sheet, int columns, int rows) {
	}
}
