package com.trading.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.trading.game.Instance;

public class Player extends Character  {
	
    
    float playerSpeed = 200f;
	public int myId = 0;
    public PlayerData playerData;
    
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
    
	public Player(Instance instance) {
		this.instance = instance;
		
		// Center the sprite in the top/middle of the screen
        sprite.setPosition(Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);
        
        setName("Taylor");
        playerData = new PlayerData();
	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		stateTime += Gdx.graphics.getDeltaTime();
		lastMoved += Gdx.graphics.getDeltaTime();
		attackTime -= Gdx.graphics.getDeltaTime();
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
    	if (sword.equipped) {
    		sword.set(new Sprite(getCurrentSword(st)));
            sword.setPosition(getX()-offsetX , getY()-offsetY);
            sword.setScale(0.5f);
            sword.draw(batch);
    	}
    }
}
