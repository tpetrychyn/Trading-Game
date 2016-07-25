package com.trading.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TmxMapLoader.Parameters;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.trading.entities.Player;

public class Game extends ApplicationAdapter implements Screen {
	SpriteBatch batch;
	SpriteBatch debugBatch;
	
	BitmapFont font;
	TiledMap map;
	IsometricTiledMapRenderer mapRenderer;
	World world;
    Player player;
    
    private OrthographicCamera camera;
    
    private int[] background = new int[] {0}, foreground = new int[] {1};
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		debugBatch = new SpriteBatch();
		font = new BitmapFont();
		
		Parameters params = new Parameters();
		map = new TmxMapLoader().load("Maps/map.tmx", params);
	
		mapRenderer = new IsometricTiledMapRenderer(map);
	
		
		world = new World(new Vector2(0, 0), true);
		player = new Player(world, (TiledMapTileLayer) map.getLayers().get(1));
		player.setPosition(new Vector2(10,10));
        
        camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.
                getHeight());
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.zoom = 0.5f;
        camera.update();
       
	}

	Texture currentFrame;
	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		world.step(1f/60f, 6, 2);
		
		camera.position.set(player.getPosition().x + player.getCurrentTexture().getRegionWidth() / 2, player.getPosition().y, 0);
		camera.update();
		
		mapRenderer.setView(camera);
		mapRenderer.render(background);
		
		batch.setProjectionMatrix(camera.combined);
		
		batch.begin();
		
		player.draw(batch);
		
		batch.end();
		
		mapRenderer.render(foreground);
		debugBatch.begin();
		font.setColor(Color.WHITE);
		Vector2 playerPos = player.getWorldPosition();
		font.draw(debugBatch, "X: " + (int) playerPos.x + " Y: " + (int) playerPos.y, 50, 50);
		
		debugBatch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		player.dispose();
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
