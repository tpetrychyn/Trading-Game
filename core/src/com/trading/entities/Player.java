package com.trading.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.trading.game.Instance;

public class Player extends WorldActor  {
	
    Instance instance;
    
    float playerSpeed = 200f;
    public boolean isMoving = false;
    
	public int myId = 0;
    
    float stateTime = 0;
    BitmapFont font;
    public PlayerData playerData;
    public float lastMoved = 0;
    Animation swordAnimations[];
    
    public int offsetX;
    public int offsetY;
    
    public float getSpeed() {
    	return playerSpeed;
    }
    
    public void setSpeed(float speed) {
    	playerSpeed = speed;
    }
    
    public Vector2 getPosition() {
    	Vector2 pos = new Vector2(getX(), getY());
		return pos;
    }
    
    public Vector2 getWorldPosition() {
    	return instance.getWorldPosition(getPosition());
    }
    
    public void setWorldPosition(Vector2 pos) {
    	setX((pos.x + ((pos.y-pos.x)/2)) * 64);
		setY((pos.y - pos.x) * 16);
    }
    
    public void setDirection(Direction dir) {
    	direction = dir;
    }
    
    public Direction getDirection() {
    	return direction;
    }
    
    public void setPosition(Vector2 transform) {
    	setX(transform.x);
    	setY(transform.y);
    }
    
    Sprite equipped;
	
	public Player(Instance instance) {
		this.instance = instance;
		
		Texture t = new Texture(Gdx.files.internal("male_idle.png"), true);
		sprite = new Sprite(t);
		
		// Center the sprite in the top/middle of the screen
        sprite.setPosition(Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);
        
        equipped = new Sprite(new Texture(Gdx.files.internal("sword.png")));
        equipped.setPosition(Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);
        equipped.setOrigin(equipped.getWidth()/2, equipped.getHeight()/2);
        
        font = new BitmapFont();
        
        setScale(1f);
        
        offsetX = 9;
        offsetY = 14;
        realWidth = 14;
        realHeight = 22;
        
        setOrigin(getWidth()/2,getHeight()/2);
        
		walkAnimations = new Animation[8];
		Animator a = new Animator(9, 4, "male_walk2.png");
        walkAnimations[0] = a.addAnimation(1, 7);
        walkAnimations[1] = a.addAnimation(10, 7);
        walkAnimations[2] = a.addAnimation(19, 7);
        walkAnimations[3] = a.addAnimation(28, 7);

        walkAnimations[4] = a.addAnimation(0, 1);
        walkAnimations[5] = a.addAnimation(9, 1);
        walkAnimations[6] = a.addAnimation(18, 1);
        walkAnimations[7] = a.addAnimation(27, 1);
        
        
        swordAnimations = new Animation[8];
		a = new Animator(9, 4, "sword_walk.png");
		swordAnimations[0] = a.addAnimation(1, 7);
		swordAnimations[1] = a.addAnimation(10, 7);
		swordAnimations[2] = a.addAnimation(19, 7);
		swordAnimations[3] = a.addAnimation(28, 7);

		swordAnimations[4] = a.addAnimation(0, 1);
		swordAnimations[5] = a.addAnimation(9, 1);
		swordAnimations[6] = a.addAnimation(18, 1);
		swordAnimations[7] = a.addAnimation(27, 1);
        
        setName("Taylor");
        playerData = new PlayerData();
	}
	
	public TextureRegion getCurrentTexture(float st) {
    	if (!isMoving)
    		return walkAnimations[direction.getValue() + 4].getKeyFrame(st);
    	
    	return walkAnimations[direction.getValue()].getKeyFrame(st, true);
    }
	
	public TextureRegion getCurrentSword(float st) {
    	if (!isMoving)
    		return swordAnimations[direction.getValue() + 4].getKeyFrame(st);
    	
    	return swordAnimations[direction.getValue()].getKeyFrame(st, true);
    }
	
	@Override
	public void draw(Batch batch, float alpha) {
		stateTime += Gdx.graphics.getDeltaTime();
		lastMoved += Gdx.graphics.getDeltaTime();
		sprite = new Sprite(getCurrentTexture(stateTime));
		font.setColor(Color.BLUE);
		font.getData().setScale(0.5f);
		font.draw(batch, playerData.pId + "", getX() + getWidth()/2 - getName().length()*7/2, getY()+getHeight() + 10);
		font.draw(batch, "Health: " + playerData.health, getX() + getWidth()/2, getY()+getHeight() + 30);
		font.draw(batch, "Stamina: " + playerData.stamina, getX() + getWidth()/2, getY()+getHeight() + 40);
		
		if (direction == Direction.NORTH)
			drawEquipped(batch, stateTime);
		
		super.draw(batch, alpha);
		
		if (direction != Direction.NORTH)
			drawEquipped(batch, stateTime);
		
		if (lastMoved < 0.1)
			isMoving = true;
		else
			isMoving = false;
	}
	
	
	public void drawEquipped(Batch batch, float st) {
		equipped = new Sprite(getCurrentSword(st));
		equipped.setPosition(getX()-16 , getY()-24);
		equipped.setScale(0.5f);
		equipped.draw(batch);
	}
}
