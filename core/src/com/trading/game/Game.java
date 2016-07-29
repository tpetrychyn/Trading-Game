package com.trading.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.trading.entities.Npc;
import com.trading.entities.Player;
import com.trading.entities.WorldActor;
import com.trading.networking.GameWorld;

public class Game extends ApplicationAdapter implements Screen, ApplicationListener {
	
	public static boolean debug = false;
	
	private static final int VIRTUAL_WIDTH = 1920;
    private static final int VIRTUAL_HEIGHT = 1080;
    private static final float ASPECT_RATIO = (float)VIRTUAL_WIDTH/(float)VIRTUAL_HEIGHT;

    private Rectangle viewport;
    Viewport view;
	
	SpriteBatch batch;
	SpriteBatch debugBatch;
	
	public static ChatBox chatbox;
	
	BitmapFont font;
	
	GameWorld world;
    static Player player;
    
    public static OrthographicCamera camera;
    
    public static Stage stage;
    
    TiledMap map;
	IsometricTiledMapRenderer mapRenderer;
	private int[] backgroundLayers = new int[] {0, 1}, foreground = new int[] {2};
	
	@Override
	public void create () {
		
		//MyTextInputListener listener = new MyTextInputListener();
		//Gdx.input.getTextInput(listener, "Enter ip", "71.17.226.9", "");
		
		batch = new SpriteBatch();
		debugBatch = new SpriteBatch();
		font = new BitmapFont();
		
		world = new GameWorld("Maps/map.tmx");
		
		map = new TmxMapLoader().load("Maps/map.tmx");
		mapRenderer = new IsometricTiledMapRenderer(map);
		
		player = new Player(world);
		player.setPosition(new Vector2(10, 10));
		
		world.addPlayer(player);
        
        camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.
                getHeight());
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.zoom = 0.5f;
        camera.update();
        
        
        
        font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        
        chatbox = new ChatBox();
        
        stage = new Stage();
        //allocated first 100 actors to npcs
		for (int i=0;i<100;i++) {
			Npc w = new Npc(world);
			w.setPosition(10, 10);
			//WorldActor w = new WorldActor(new Texture("male_idle.png"), 10, 10, world, i, 0.5f);
			stage.addActor(w);
		}
		
		//your player becomes 101
		stage.addActor(player);
		
		//then add actors up to 1000
		for (int i=102;i<202;i++) {
			WorldActor w = new WorldActor(new Texture("male_idle.png"), -50, -100, world, i, 0.5f);
			stage.addActor(w);
		}
        Gdx.input.setInputProcessor(player);
	}
	
	public static OrthographicCamera getCamera() {
		return camera;
	}
	
	
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		// set viewport
        Gdx.gl.glViewport((int) viewport.x, (int) viewport.y,
                          (int) viewport.width, (int) viewport.height);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		/*world.getWorld().step(1f/60f, 6, 2);*/
		
		stage.getCamera().position.set(player.getPosition().x + player.getWidth() / 2, player.getPosition().y, 0);
		stage.getViewport().setCamera(camera);
		mapRenderer.setView((OrthographicCamera) stage.getCamera());
		mapRenderer.render(backgroundLayers);
		
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		stage.setDebugAll(true);
		
		mapRenderer.render(foreground);
		
		debugBatch.begin();
		font.setColor(Color.WHITE);
		Vector2 playerPos = player.getWorldPosition();
		font.draw(debugBatch, "World X: " + (int) playerPos.x + " World Y: " + (int) playerPos.y, 50, 50);
		font.draw(debugBatch, "X: " + player.getX() + " Y: " + player.getY(), 50, 35);
		font.draw(debugBatch, player.getMousePosition().toString(), 50, 20);
		if (chatbox.showTextEnter) {
			chatbox.field.draw(debugBatch, 1f);
		}
		if (chatbox.shouldFade && chatbox.fade > 0)
			chatbox.fade -= Gdx.graphics.getDeltaTime();
		chatbox.textArea.draw(debugBatch, chatbox.fade/10);
		
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
	public void hide() {
		// TODO Auto-generated method stub
		
	}   
	
	public void resize (int width, int height) {
		// calculate new viewport
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
        float h = (float)VIRTUAL_HEIGHT*scale;
        viewport = new Rectangle(crop.x, crop.y, w, h);
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		
	}
}
