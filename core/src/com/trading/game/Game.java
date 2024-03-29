package com.trading.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.trading.entities.PlayerController;
import com.trading.entities.WorldObjects;

public class Game extends ApplicationAdapter implements Screen, ApplicationListener {
	
	public enum GameState {
		Loading(0), Playing(1), Menu(2);
		
		private final int value;
		private GameState(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}
	
	public static boolean debug = false;
	
	Batch batch;
	SpriteBatch debugBatch;
	
	public static ChatBox chatbox;
	BitmapFont font;
    public static PlayerController player;
    public static OrthographicCamera camera;
    TiledMap map;
	IsometricTiledMapRenderer mapRenderer;
	Instance instance;
	public static String ip;
	
	public static GameState state;
	
	/*public class MyTextInputListener implements TextInputListener {
		   @Override
		   public void input (String text) {
			   ip = text;
		   }

		   @Override
		   public void canceled () {
		   }
		}*/
	
	@Override
	public void create () {
		
		//MyTextInputListener listener = new MyTextInputListener();
		//Gdx.input.getTextInput(listener, "Enter ip", "71.17.226.9", "");
		batch = new SpriteBatch();
		debugBatch = new SpriteBatch();
		font = new BitmapFont();
		
		instance = new Instance("map.tmx");
		
		player = new PlayerController(instance);
		player.setPosition(new Vector2(10, 10));
		
		instance.addPlayer(player);
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.
                getHeight());
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.zoom = 0.5f;
        camera.update();
        map = new TmxMapLoader().load("Maps/map.tmx");
		mapRenderer = new IsometricTiledMapRenderer(map);
		
		state = GameState.Playing;
		
		//load the world objects
		WorldObjects w = new WorldObjects();
		
        Gdx.input.setInputProcessor(player);
	}
	
	public static OrthographicCamera getCamera() {
		return camera;
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if (state == GameState.Playing) {
			
			camera.position.set(player.getPosition().x + player.getWidth() / 2, player.getPosition().y, 0);
			camera.update();
			mapRenderer.setMap(player.instance.map);
			mapRenderer.setView((OrthographicCamera) camera);
			
			mapRenderer.render(player.instance.backgroundLayers);
			
			batch.setProjectionMatrix(camera.combined);
			
			//function starts and ends the batch
			player.instance.Draw(batch, 1f);
			
			mapRenderer.render(player.instance.foregroundLayers);
			
			debugBatch.begin();
			font.setColor(Color.WHITE);
			Vector2 playerPos = player.getWorldPosition();
			font.draw(debugBatch, "World X: " + (int) playerPos.x + " World Y: " + (int) playerPos.y, 50, 50);
			font.draw(debugBatch, "X: " + player.getX() + " Y: " + player.getY(), 50, 35);
			font.draw(debugBatch, player.getMousePosition().toString(), 50, 20);
			
			/*if (chatbox.showTextEnter) {
				chatbox.field.draw(debugBatch, 1f);
			}
			if (chatbox.shouldFade && chatbox.fade > 0)
				chatbox.fade -= Gdx.graphics.getDeltaTime();
			chatbox.textArea.draw(debugBatch, chatbox.fade/10);*/
			
			debugBatch.end();
		
		}
		
		if (state == GameState.Loading) {
			debugBatch.begin();
			font.setColor(Color.WHITE);
			if (stateTime <= 0.5f) {
				font.draw(debugBatch, "Loading.", 50, 50);
			} else if (stateTime <= 1.5f) {
				font.draw(debugBatch, "Loading..", 50, 50);
			} else if (stateTime <= 2.5f) {
				font.draw(debugBatch, "Loading...", 50, 50);
			} else if (stateTime <= 3)
				stateTime = 0;
			stateTime += Gdx.graphics.getDeltaTime();
			debugBatch.end();
		}
	}
	
	float stateTime = 0;
	
	public static PlayerController getPlayer() {
		return player;
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		instance.dispose();
	}
	

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}   
	
	public void resize (int width, int height) {
		/*// calculate new viewport
        float aspectRatio = (float)width/(float)height;
        float scale = 1f;
        Vector2 crop = new Vector2(0f, 0f); 
        if(aspectRatio > ASPECT_RATIO)
        {
            scale = (float)height/(float)VIRTUAL_HEIGHT;
            crop.x = (width - VIRTUAL_WIDTH*scale)/2f;
        }
        else if(aspectRatio < ASPECT_RATIO)
        {
            scale = (float)width/(float)VIRTUAL_WIDTH;
            crop.y = (height - VIRTUAL_HEIGHT*scale)/2f;
        }
        else
        {
            scale = (float)width/(float)VIRTUAL_WIDTH;
        }

        float w = (float)VIRTUAL_WIDTH*scale;
        float h = (float)VIRTUAL_HEIGHT*scale;*/
        camera.setToOrtho(false);
        batch.setProjectionMatrix(camera.combined);
        
        OrthographicCamera c = new OrthographicCamera(width, height);
        c.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        c.update();
        debugBatch.setProjectionMatrix(c.combined);
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		
	}
}
