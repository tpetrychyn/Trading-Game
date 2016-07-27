package com.trading.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.trading.entities.Player;

public class Game extends ApplicationAdapter implements Screen {
	SpriteBatch batch;
	SpriteBatch debugBatch;
	
	BitmapFont font;
	
	GameWorld world;
    static Player player;
    
    public static OrthographicCamera camera;
    
    ShapeRenderer sr;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		debugBatch = new SpriteBatch();
		font = new BitmapFont();
		
		world = new GameWorld("Maps/map.tmx");
		
		player = new Player(world);
		player.setPosition(new Vector2(10,10));
		
		world.addPlayer(player);
        
        camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.
                getHeight());
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.zoom = 0.5f;
        camera.update();
        
        sr = new ShapeRenderer();
        
        Gdx.input.setInputProcessor(player);
	}
	
	public static OrthographicCamera getCamera() {
		return camera;
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		world.getWorld().step(1f/60f, 6, 2);
		
		camera.position.set(player.getPosition().x + player.getCurrentTexture().getRegionWidth() / 2, player.getPosition().y, 0);
		camera.update();
		
		world.Update(player, batch);
		
		debugBatch.begin();
		font.setColor(Color.WHITE);
		Vector2 playerPos = player.getWorldPosition();
		font.draw(debugBatch, "World X: " + (int) playerPos.x + " World Y: " + (int) playerPos.y, 50, 50);
		font.draw(debugBatch, "X: " + player.getX() + " Y: " + player.getY(), 50, 35);
		font.draw(debugBatch, player.getMousePosition().toString(), 50, 20);
		debugBatch.end();
	}
	
	public static Player getPlayer() {
		return player;
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		world.dispose();
	}
	

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}   
}
