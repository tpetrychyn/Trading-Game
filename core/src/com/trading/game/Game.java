package com.trading.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	
	TiledMap map;
	IsometricTiledMapRenderer mapRenderer;
	World world;
    Player player;
    float stateTime;
    
    private OrthographicCamera camera;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		
		map = new TmxMapLoader().load("Maps/test.tmx");
		mapRenderer = new IsometricTiledMapRenderer(map);
		
		world = new World(new Vector2(0, 0), true);
		player = new Player(world);
		
        stateTime = 0f;
        
        camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.
                getHeight());
       camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
       camera.update();
       
        
	}

	Texture currentFrame;
	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		world.step(1f/60f, 6, 2);
		
		camera.position.set(player.getPosition().x + player.getTexture(0, true).getRegionWidth() / 2, player.getPosition().y, 0);
		camera.update();
		
		mapRenderer.setView(camera);
		mapRenderer.render();
		
		stateTime += Gdx.graphics.getDeltaTime();

		Vector2 playerVelocity = new Vector2();
        // On right or left arrow set the velocity at a fixed rate in that direction
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
        	playerVelocity.x = player.getSpeed();
        	player.setDirection(3);
        	player.isMoving = true;
        } else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
        	playerVelocity.x = -player.getSpeed();
        	player.setDirection(1);
        	player.isMoving = true;
        } else {
        	playerVelocity.x = 0;
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
        	playerVelocity.y = player.getSpeed();
        	player.setDirection(0);
        	player.isMoving = true;
        } else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
        	playerVelocity.y = -player.getSpeed();
        	player.setDirection(2);
        	player.isMoving = true;
        } else {
        	playerVelocity.y = 0;
        	
        }
        
        if (playerVelocity.y == 0 && playerVelocity.x == 0)
        	player.isMoving = false;
        
        if (player.getPosition().x < 0)
        	player.setTransform(new Vector2(0, player.getPosition().y));
        if (player.getPosition().y < 0)
        	player.setTransform(new Vector2(player.getPosition().x,0));
        
        player.setVelocity(new Vector2(playerVelocity.x, playerVelocity.y));
        
        
        //currentFrame = player.getTexture(stateTime);  // #16
        
        // Now update the sprite position accordingly to it's now updated Physics body
        //player.test().setPosition(player.getPosition().x, player.getPosition().y);
		batch.setProjectionMatrix(camera.combined);
		camera.zoom = 0.9f;
		batch.begin();
		batch.draw(player.getTexture(stateTime, !player.isMoving), player.getPosition().x, player.getPosition().y, player.size().x, player.size().y);
		//batch.draw(player.test(), player.test().getX(), player.test().getY());             // #17
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		player.dispose();
		world.dispose();
	}


        
}
