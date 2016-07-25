package com.trading.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class Player extends Sprite implements InputProcessor {
	
	Texture playerImage;
	Sprite playerSprite;
    Body player;
    
    float playerSpeed = 200f;
    public boolean isMoving = false;
    Direction direction = Direction.NORTH;
    float stateTime = 0;
    
    public float getSpeed() {
    	return playerSpeed;
    }
    
    public void setSpeed(float speed) {
    	playerSpeed = speed;
    }
    
    public void setVelocity(Vector2 velocity) {
    	player.setLinearVelocity(velocity);
    }
    
    public Vector2 getPosition() {
    	Vector2 pos = new Vector2(getX(), getY());
		return pos;
    }
    
    Vector2 getTileCoordinates(Vector2 pt, float tileHeight) {
		  Vector2 tempPt = new Vector2(0, 0);
		  tempPt.x = pt.x / tileHeight / 2;
		  tempPt.y = pt.y / tileHeight;
		  return(tempPt);
	}
	
	Vector2 twoDToIso(Vector2 pt) {
		  Vector2 tempPt = new Vector2(0,0);
		  tempPt.x = pt.x + 0.5f - pt.y;
		  tempPt.y = pt.x + pt.y;
		  return(tempPt);
	}
	
	Vector2 isoToTwoD(Vector2 pt) {
		Vector2 tempPt = new Vector2(0,0);
		tempPt.x = (2 * pt.y + pt.x) / 2;
		tempPt.y = (2 * pt.y - pt.x) / 2;
		return(tempPt);
	}
	
    public Vector2 getWorldPosition() {
    	return twoDToIso(getTileCoordinates(getPosition(), 32));
    }
    
    public void setWorldPosition(Vector2 pos) {
    	Vector2 tempPt = new Vector2(0, 0);
    	tempPt.x = pos.x;
    	tempPt.y = pos.y;
    	player.setTransform(tempPt, 0);
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
    		currentFrame = stoppedAnimation.getKeyFrame(direction.getValue());
    	return currentFrame;
    }
    
    public void setPosition(Vector2 transform) {
    	setX(transform.x);
    	setY(transform.y);
    	//player.setTransform(transform, 0);
    }
    
	public Player(World world) {
    	playerImage = new Texture("badlogic.jpg");
		playerSprite = new Sprite(playerImage);
		// Center the sprite in the top/middle of the screen
        playerSprite.setPosition(Gdx.graphics.getWidth() / 2 - playerSprite.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);
        create();
	}
	
	public void draw(SpriteBatch spriteBatch) {
		update(Gdx.graphics.getDeltaTime());
		spriteBatch.draw(getCurrentTexture(), getPosition().x, getPosition().y, size().x, size().y);
		//super.draw(spriteBatch);
	}
	
	Vector2 oldPos;
	void update(float deltaTime) {
		stateTime += Gdx.graphics.getDeltaTime();

		Vector2 playerVelocity = new Vector2();
        // On right or left arrow set the velocity at a fixed rate in that direction
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
        	playerVelocity.x = getSpeed();
        	setDirection(Direction.EAST);
        	isMoving = true;
        } else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
        	playerVelocity.x = -getSpeed();
        	setDirection(Direction.WEST);
        	isMoving = true;
        } else {
        	playerVelocity.x = 0;
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
        	playerVelocity.y = getSpeed()/2;
        	setDirection(Direction.NORTH);
        	isMoving = true;
        } else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
        	playerVelocity.y = -getSpeed()/2;
        	setDirection(Direction.SOUTH);
        	isMoving = true;
        } else {
        	playerVelocity.y = 0;
        }
        
        if (playerVelocity.y == 0 && playerVelocity.x == 0)
        	isMoving = false;
        
        oldPos = getPosition();
        setX(getX() + playerVelocity.x * deltaTime);
        setY(getY() + playerVelocity.y * deltaTime);
        
        if (getWorldPosition().x < 0 || getWorldPosition().y < 0.2){
        	setY(oldPos.y);
        	setX(oldPos.x);
        }
	}
	
	public Body getBody() {
		return player;
	}
    
    public void dispose() {
    	playerImage.dispose();
    }
    
    private static final int        FRAME_COLS = 9;         // #1
    private static final int        FRAME_ROWS = 4;         // #2

    Animation                       walkNorthAnimation;          // #3
    Animation                       walkWestAnimation;          // #3
    Animation                       walkSouthAnimation;          // #3
    Animation                       walkEastAnimation;          // #3
    Animation						stoppedAnimation;
    Texture                         walkSheet;              // #4
    TextureRegion[]                 walkFrames;             // #5
    TextureRegion                   currentFrame;           // #7
    
    public void create() {
        walkSheet = new Texture(Gdx.files.internal("male_walkcycle.png")); // #9
        TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth()/FRAME_COLS, walkSheet.getHeight()/FRAME_ROWS);              // #10
        walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }
        TextureRegion[] stopped = {walkFrames[0], walkFrames[9], walkFrames[18], walkFrames[27]};
        TextureRegion[] north = new TextureRegion[7];
        TextureRegion[] west = new TextureRegion[7];
        TextureRegion[] south = new TextureRegion[7];
        TextureRegion[] east = new TextureRegion[7];
        int n = 0;
        for (int i=1;i<8;i++) {
        	north[n] = walkFrames[i];
        	n++;
        }
        n = 0;
        for (int i=10;i<17;i++) {
        	west[n] = walkFrames[i];
        	n++;
        }
        n = 0;
        for (int i=19;i<26;i++) {
        	south[n] = walkFrames[i];
        	n++;
        }
        n = 0;
        for (int i=28;i<35;i++) {
        	east[n] = walkFrames[i];
        	n++;
        }
        
        walkNorthAnimation = new Animation(0.060f, north);      // #11
        walkWestAnimation = new Animation(0.060f, west);      // #11
        walkSouthAnimation = new Animation(0.060f, south);      // #11
        walkEastAnimation = new Animation(0.060f, east);      // #11
        stoppedAnimation = new Animation(1, stopped);
    }

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
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
}
