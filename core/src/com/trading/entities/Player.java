package com.trading.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;

public class Player extends Actor implements InputProcessor{
	
    MapLayers collisionLayers;
    public Vector3 MousePos;
    
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
    	return twoDToIso(getTileCoordinates(getPosition(), 32));
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
    
	public Player(World world, MapLayers collisionLayers) {
		super(new Texture("badlogic.jpg"));
		
		// Center the sprite in the top/middle of the screen
        sprite.setPosition(Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);
        
        this.collisionLayers = collisionLayers;
        
        Animator a = new Animator(9, 4, "male_walkcycle.png");
        walkNorthAnimation = a.addAnimation(1, 7);
        walkWestAnimation = a.addAnimation(10, 7);
        walkSouthAnimation = a.addAnimation(19, 7);
        walkEastAnimation = a.addAnimation(28, 7);
        stoppedAnimation[0] = a.addAnimation(0, 1);
        stoppedAnimation[1] = a.addAnimation(9, 1);
        stoppedAnimation[2] = a.addAnimation(18, 1);
        stoppedAnimation[3] = a.addAnimation(27, 1);
	}
	
	public void draw(SpriteBatch spriteBatch) {
		update(Gdx.graphics.getDeltaTime());
		spriteBatch.draw(getCurrentTexture(), getPosition().x, getPosition().y, size().x, size().y);
		//super.draw(spriteBatch);
	}
	
	
	void update(float deltaTime) {
		stateTime += Gdx.graphics.getDeltaTime();

		Vector2 playerVelocity = new Vector2();
        // On right or left arrow set the velocity at a fixed rate in that direction
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
        	playerVelocity.x = getSpeed();
        	setDirection(Direction.EAST);
        } else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
        	playerVelocity.x = -getSpeed();
        	setDirection(Direction.WEST);
        } else {
        	playerVelocity.x = 0;
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
        	playerVelocity.y = getSpeed()/2;
        	setDirection(Direction.NORTH);
        } else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
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
        		|| isCellBlocked(getWorldPosition().x, getWorldPosition().y)){
        	setY(oldPos.y);
        	setX(oldPos.x);
        }
        
        KeyPressed();
	}
	
	private boolean isCellBlocked(float x, float y) {
		boolean blocked = false;
		for (int i=0;i<3;i++) {
			TiledMapTileLayer coll = (TiledMapTileLayer) collisionLayers.get(i);
			Cell cell = coll.getCell((int) (x), (int) (y));
			blocked = cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("blocked");
			if (blocked == true)
				return true;
		}
		return blocked;
	}
    
    public void dispose() {
    }

	@Override
	public boolean keyDown(int keycode) {
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
			if (keycode == 29) {
				System.out.println("hey");
			}
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
		Vector2 m = new Vector2(MousePos.x, MousePos.y);
		return twoDToIso(getTileCoordinates(m, 32));
	}

	public void KeyPressed() {
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			//setWorldPosition(new Vector2(99,99));
			
			System.out.println(getPosition().x + " " + (getPosition().x + size().x));
			System.out.println(MousePos.x);
			if (MousePos.x < getPosition().x + size().x 
					&& MousePos.x > getPosition().x - size().x
					&& MousePos.y < getPosition().y + size().y
					&& MousePos.y > getPosition().y - size().y) {
				System.out.println("yes");
			}
		}
	}
}
