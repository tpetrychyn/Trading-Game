package com.trading.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.trading.game.Instance;

public class Npc extends WorldActor {
	
	float stateTime;
	BitmapFont font;
	
	public Npc(Instance instance) {
		this.instance = instance;
		font = new BitmapFont();
		
		Texture t = new Texture(Gdx.files.internal("male_idle.png"), true);
		sprite = new Sprite(t);
		// Center the sprite in the top/middle of the screen
        sprite.setPosition(Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);
        setScale(0.5f);
        
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
        setName("");
	}
	
	public Npc(Texture image, float x, float y, Instance instance, int id, float scale, String name) {
		this.id = id;
		font = new BitmapFont();
		font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		this.instance = instance;
		
		instance.setWorldPosition(this, new Vector2(x, y));

		Texture t = new Texture(Gdx.files.internal("male_idle.png"), true);
		sprite = new Sprite(t);
		
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
        setName(name);
	}
	
	public TextureRegion getCurrentTexture(float st) {
    	if (!isMoving)
    		return walkAnimations[direction.getValue() + 4].getKeyFrame(st);
    	
    	return walkAnimations[direction.getValue()].getKeyFrame(st, true);
    }
	
	float timeSinceMove = 0;
	@Override
	public void draw(Batch batch, float alpha) {
		stateTime += Gdx.graphics.getDeltaTime();
		sprite = new Sprite(getCurrentTexture(stateTime));
		
		font.setColor(Color.WHITE);
		font.getData().setScale(0.5f);
		font.draw(batch, getName(), getX() + getWidth()/2 - getName().length()*7/2, getY()+getHeight() + 10);
		super.draw(batch, alpha);
		
		if (timeSinceMove > 0.2)
			isMoving = false;
		timeSinceMove += Gdx.graphics.getDeltaTime();
	}
}
