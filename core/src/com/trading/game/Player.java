package com.trading.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Player {
	
	Texture playerImage;
	Sprite playerSprite;
    Body player;
    
    float playerSpeed = 50f;
    public boolean isMoving = false;
    int direction = 0;
    
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
    	return player.getPosition();
    }
    
    public void setDirection(int dir) {
    	direction = dir;
    }
    
    public int getDirection() {
    	return direction;
    }
    
    public Vector2 size() {
    	return new Vector2(currentFrame.getRegionWidth() * 0.6f, currentFrame.getRegionHeight() * 0.6f);
    }
    
    public TextureRegion getTexture(float stateTime, boolean stopped) {
    	switch(direction) {
    	case 0:
    		currentFrame = walkNorthAnimation.getKeyFrame(stateTime,true);
    		break;
    	case 1:
    		currentFrame = walkWestAnimation.getKeyFrame(stateTime,true);
    		break;
    	case 2:
    		currentFrame = walkSouthAnimation.getKeyFrame(stateTime,true);
    		break;
    	case 3:
    		currentFrame = walkEastAnimation.getKeyFrame(stateTime,true);
    		break;
    	}
    	
    	if (stopped)
    		currentFrame = stoppedAnimation.getKeyFrame(direction);
    	return currentFrame;
    }
    
    public void setTransform(Vector2 transform) {
    	player.setTransform(transform, 0);
    }
    
	public Player(World world) {
    	playerImage = new Texture("badlogic.jpg");
		playerSprite = new Sprite(playerImage);
		// Center the sprite in the top/middle of the screen
        playerSprite.setPosition(Gdx.graphics.getWidth() / 2 - playerSprite.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);
		
		// Now create a BodyDefinition.  This defines the physics objects type and position in the simulation
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // We are going to use 1 to 1 dimensions.  Meaning 1 in physics engine is 1 pixel
        // Set our body to the same position as our sprite
        bodyDef.position.set(playerSprite.getX(), playerSprite.getY());

        // Create a body in the world using our definition
        player = world.createBody(bodyDef);
        
        // Now define the dimensions of the physics shape
        PolygonShape shape = new PolygonShape();
        // We are a box, so this makes sense, no?
        // Basically set the physics polygon to a box with the same dimensions as our sprite
        shape.setAsBox(playerSprite.getWidth()/2, playerSprite.getHeight()/2);
        
        // FixtureDef is a confusing expression for physical properties
        // Basically this is where you, in addition to defining the shape of the body
        // you also define it's properties like density, restitution and others we will see shortly
        // If you are wondering, density and area are used to calculate over all mass
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        player.createFixture(fixtureDef);
        
        // Shape is the only disposable of the lot, so get rid of it
        shape.dispose();
        create();
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
        
        System.out.println(north[6]);
        walkNorthAnimation = new Animation(0.060f, north);      // #11
        walkWestAnimation = new Animation(0.060f, west);      // #11
        walkSouthAnimation = new Animation(0.060f, south);      // #11
        walkEastAnimation = new Animation(0.060f, east);      // #11
        stoppedAnimation = new Animation(1, stopped);
    }
}
