package com.trading.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Character extends WorldActor {
	
	Animation[] walkAnimations;
    Animation swordAnimations[];
	public Direction direction = Direction.SOUTH;
	Vector2 velocity = new Vector2();
	float stateTime = 0;
    BitmapFont font;
    public float lastMoved = 0;
    public boolean isMoving = false;
    public float attackTime = 0;
    
    public Equipment sword;
	
	public TextureRegion getCurrentTexture(float st) {
		if (attackTime > 0) {
			return walkAnimations[direction.getValue()+8].getKeyFrame(st, true);
		}
		
		if (!isMoving)
    		return walkAnimations[direction.getValue() + 4].getKeyFrame(st);
		
		if (direction == Direction.NORTH || direction == Direction.SOUTH) {
			offsetX = 17/2*3;
	        offsetY = 25/2*3;
		} else {
			offsetX = 24;
	        offsetY = 25/2*3;
		}
    	
    	return walkAnimations[direction.getValue()].getKeyFrame(st, true);
    }
	
    public TextureRegion getCurrentSword(float st) {
    	if (attackTime > 0) {
    		return swordAnimations[direction.getValue()+8].getKeyFrame(st, true);
    	}
        if (!isMoving)
        	return swordAnimations[direction.getValue() + 4].getKeyFrame(st);
        
        return swordAnimations[direction.getValue()].getKeyFrame(st, true);
    }
    
    public float getWidth() {
		return realWidth;
	}
	
	public float getHeight() {
		return realHeight;
	}
	
	public void equipWeapon(String file) {
		sword.equipped = true;
		swordAnimations = new Animation[12];
		Animator a = new Animator(9, 8, "sword-sheet.png");
        swordAnimations[0] = a.addAnimation(1, 7);
        swordAnimations[1] = a.addAnimation(10, 7);
        swordAnimations[2] = a.addAnimation(19, 7);
        swordAnimations[3] = a.addAnimation(28, 7);

        swordAnimations[4] = a.addAnimation(0, 1);
        swordAnimations[5] = a.addAnimation(9, 1);
        swordAnimations[6] = a.addAnimation(18, 1);
        swordAnimations[7] = a.addAnimation(27, 1);
        
        swordAnimations[8] = a.addAnimation(37, 4, 0.08f);
        swordAnimations[9] = a.addAnimation(46, 4, 0.08f);
        swordAnimations[10] = a.addAnimation(55, 4, 0.08f);
        swordAnimations[11] = a.addAnimation(64, 4, 0.08f);
		
	}

	public Character() {
		walkAnimations = new Animation[12];
        Animator a = new Animator(9, 8, "male_walk3.png");
        walkAnimations[0] = a.addAnimation(1, 7);
        walkAnimations[1] = a.addAnimation(10, 7);
        walkAnimations[2] = a.addAnimation(19, 7);
        walkAnimations[3] = a.addAnimation(28, 7);

        walkAnimations[4] = a.addAnimation(0, 1);
        walkAnimations[5] = a.addAnimation(9, 1);
        walkAnimations[6] = a.addAnimation(18, 1);
        walkAnimations[7] = a.addAnimation(27, 1);
        
        walkAnimations[8] = a.addAnimation(37, 4, 0.08f);
        walkAnimations[9] = a.addAnimation(46, 4, 0.08f);
        walkAnimations[10] = a.addAnimation(55, 4, 0.08f);
        walkAnimations[11] = a.addAnimation(64, 4, 0.08f);
        
        sprite = new Sprite(walkAnimations[0].getKeyFrame(0));
        font = new BitmapFont();
        
        setScale(0.5f);
        offsetX = 17/2*3;
        offsetY = 25/2*3;
        realHeight = 25;
        realWidth = 16;
        
        sword = new Equipment();
	}
}
