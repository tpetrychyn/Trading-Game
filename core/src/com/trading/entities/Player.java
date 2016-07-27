package com.trading.entities;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.trading.game.Game;
import com.trading.game.GameWorld;

public class Player extends Actor implements InputProcessor {
	
	ShapeRenderer sr;
	
    Vector3 mousePos = new Vector3(0, 0, 0);
    GameWorld world;
    
    float playerSpeed = 200f;
    public boolean isMoving = false;
    Direction direction = Direction.NORTH;
    
    Animation walkNorthAnimation, walkWestAnimation, walkSouthAnimation, walkEastAnimation;
    Animation[] stoppedAnimation = new Animation[4];
    TextureRegion currentFrame;
    float stateTime = 0;
    
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
	
	Vector2 twoDToIso(Vector2 pt) {
		  Vector2 tempPt = new Vector2(0,0);
		  tempPt.x = pt.x + 0.5f - pt.y;
		  tempPt.y = pt.x + pt.y;
		  return(tempPt);
	}
	
	Vector2 isoToTwoD(Vector2 pt) {
		Vector2 tempPt = new Vector2(0,0);
		tempPt.x = (pt.x + ((pt.y-pt.x)/2)) * 64;
		tempPt.y = (pt.y - pt.x) * 16;
		return(tempPt);
	}
	
	Vector2 getTileCoordinates(Vector2 pt, float tileHeight) {
		  Vector2 tempPt = new Vector2(0, 0);
		  tempPt.x = pt.x / tileHeight / 2;
		  tempPt.y = pt.y / tileHeight;
		  return(tempPt);
	}
	
    public Vector2 getWorldPosition() {
    	return world.getWorldPosition(getPosition());
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
    
    public Vector2 size() {
    	return new Vector2(currentFrame.getRegionWidth() * 0.5f, currentFrame.getRegionHeight() * 0.5f);
    }
    
    public TextureRegion getCurrentTexture() {
    	
		switch(direction) {
    	case NORTH:
    		currentFrame = walkNorthAnimation.getKeyFrame(stateTime,true);
    		break;
    	case WEST:
    		currentFrame = walkWestAnimation.getKeyFrame(stateTime,true);
    		break;
    	case SOUTH:
    		currentFrame = walkSouthAnimation.getKeyFrame(stateTime,true);
    		break;
    	case EAST:
    		currentFrame = walkEastAnimation.getKeyFrame(stateTime,true);
    		break;
    	}
    	
    	if (!isMoving)
    		currentFrame = stoppedAnimation[direction.getValue()].getKeyFrame(stateTime);
    	
    	return currentFrame;
    }
    
    public void setPosition(Vector2 transform) {
    	setX(transform.x);
    	setY(transform.y);
    }
    
    public Sprite sprite;
	public Player(GameWorld world) {
		
		this.world = world;
		//super(new Texture("badlogic.jpg"));
		Texture t = new Texture("male_idle.png");
		sprite = new Sprite(t);
		// Center the sprite in the top/middle of the screen
        sprite.setPosition(Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);
        
        Animator a = new Animator(9, 4, "male_walk.png");
        walkNorthAnimation = a.addAnimation(1, 7);
        walkWestAnimation = a.addAnimation(10, 7);
        walkSouthAnimation = a.addAnimation(19, 7);
        walkEastAnimation = a.addAnimation(28, 7);
        stoppedAnimation[0] = a.addAnimation(0, 1);
        stoppedAnimation[1] = a.addAnimation(9, 1);
        stoppedAnimation[2] = a.addAnimation(18, 1);
        stoppedAnimation[3] = a.addAnimation(27, 1);
        
        sr = new ShapeRenderer();
	}
	
	public void draw(SpriteBatch batch) {
		update(Gdx.graphics.getDeltaTime());
		sprite = new Sprite(currentFrame);
		batch.draw(sprite, getPosition().x, getPosition().y, size().x, size().y);
	}
	
	
	void update(float deltaTime) {
		stateTime += Gdx.graphics.getDeltaTime();
		
		Vector2 playerVelocity = new Vector2();
        // On right or left arrow set the velocity at a fixed rate in that direction
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
        	playerVelocity.x = getSpeed();
        	setDirection(Direction.EAST);
        } else if(Gdx.input.isKeyPressed(Input.Keys.A)) {
        	playerVelocity.x = -getSpeed();
        	setDirection(Direction.WEST);
        } else {
        	playerVelocity.x = 0;
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
        	playerVelocity.y = getSpeed()/2;
        	setDirection(Direction.NORTH);
        } else if(Gdx.input.isKeyPressed(Input.Keys.S)) {
        	playerVelocity.y = -getSpeed()/2;
        	setDirection(Direction.SOUTH);
        } else {
        	playerVelocity.y = 0;
        }
        
        if (playerVelocity.y == 0 && playerVelocity.x == 0)
        	isMoving = false;
        else
        	isMoving = true;
        
        Vector2 oldPos = getPosition();
        setX(getX() + playerVelocity.x * deltaTime);
        setY(getY() + playerVelocity.y * deltaTime);
        
        if (getWorldPosition().x < 0 || getWorldPosition().y < 0.2
        		|| getWorldPosition().x > 99.8 || getWorldPosition().y > 100
        		|| world.isCellBlocked(getWorldPosition().x, getWorldPosition().y)
        		|| actorCollision()){
        	setY(oldPos.y);
        	setX(oldPos.x);
        }
	}
	
	public Vector2 getMousePosition() {
		return new Vector2(mousePos.x, mousePos.y);
	}
	
	public boolean actorCollision() {
		for(Iterator<Actor> i = world.getActors().iterator(); i.hasNext(); ) {
		    Actor a = i.next();
			Rectangle p = new Rectangle(getX(), getY(), size().x, size().y);
			Rectangle n = new Rectangle(a.getX(), a.getY(), a.getWidth(), a.getHeight());
			if (Intersector.overlaps(p, n)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.NUM_1) {
			world.setWorldPosition(this, new Vector2(50,50));
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		
		mousePos = Game.getCamera().unproject(new Vector3(screenX, screenY, 0));
		try {
			for(Iterator<Actor> i = world.getActors().iterator(); i.hasNext(); ) {
			    Actor a = i.next();
			    //System.out.println(a.getWidth() + " : " + a.getHeight());
			    if (mousePos.x < a.getX() + a.getWidth() 
						&& mousePos.x > a.getX() 
						&& mousePos.y < a.getY() + a.getHeight() 
						&& mousePos.y > a.getY()) {
			    	if (distanceToPoint(new Vector2(a.getX(),a.getY())) < 30) {
			    		System.out.println(((Npc) a).Id);
			    		((Npc) a).stopRandomWalk();
			    		((Npc) a).setColor(Color.WHITE);
			    	}
					return false;
				}
			}
		} catch(Exception e) {
			
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Vector2 getTileClicked() {
		Vector2 m = new Vector2(mousePos.x, mousePos.y);
		return world.getWorldPosition(m);//twoDToIso(getTileCoordinates(m, 32));
	}
	
	float distanceToPoint(Vector2 pt) {
		return (float) Math.sqrt((getX()-pt.x)*(getX()-pt.x) + (getY()-pt.y)*(getY()-pt.y));
	}
}
