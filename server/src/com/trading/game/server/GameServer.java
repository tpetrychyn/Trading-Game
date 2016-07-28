package com.trading.game.server;

import java.io.IOException;
import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.trading.entities.Npc;
import com.trading.entities.Player;
import com.trading.game.Game;
import com.trading.game.GameWorld;
import com.trading.game.NpcMovePacket;
import com.trading.game.PlayerMovePacket;

public class GameServer extends ApplicationAdapter implements ApplicationListener {
	
	Server server;
	Stage stage;
	GameWorld gameWorld;
	TiledMap map;
	Player players[];
	Camera camera;
	NpcMovePacket npcs[];
	BitmapFont font;
	Batch debugBatch;
	
	Actor actors[];
	
	@Override
	public void create () {
		
		gameWorld = new GameWorld("Maps/map.tmx");
		stage = new Stage();
		debugBatch = new SpriteBatch();
		font = new BitmapFont();
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.
                getHeight());
		
		actors = new Actor[200];
		npcs = new NpcMovePacket[100];
		for (int i=0;i<100;i++)
			npcs[i] = new NpcMovePacket();
		
		players = new Player[100];
		for (int i=0;i<100;i++) {
			players[i] = new Player(gameWorld);
			stage.addActor(players[i]);
			actors[i] = players[i];
		}
			
		for(Iterator<Actor> i = gameWorld.getActors().iterator(); i.hasNext(); ) {
			try {
				Npc n = (Npc) i.next();
				stage.addActor(n);
			} catch(Exception e) {
				
			}
		}
		
		Game.stage = stage;
		
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();
		
		startServer();
	}
	
	Connection connection;
	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//stage.getCamera().translate(1, 0, 0);
		stage.getViewport().setCamera(camera);
		
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		stage.setDebugAll(true);
		
		debugBatch.begin();
		font.setColor(Color.WHITE);
		font.draw(debugBatch, (server == null) + " ", 50, 50);
		debugBatch.end();
	}	
	
	public void startServer() {
		server = new Server();
	    server.start();
	    try {
			server.bind(54555, 54777);
			System.out.println("Server started");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
	    Kryo kryo = server.getKryo();
	    kryo.register(Vector2.class);
	    kryo.register(PlayerMovePacket.class);
	    //kryo.register(NpcMovePacket.class);
	    //kryo.register(NpcMovePacket[].class);
	    
	    server.addListener(new Listener() {
	        public void received (Connection connection, Object object) {
	           if (object instanceof PlayerMovePacket) {
	        	   System.out.println("got packet");
	              PlayerMovePacket request = (PlayerMovePacket)object;
	              PlayerMovePacket packet = new PlayerMovePacket(request.pos, connection.getID());
	              stage.getActors().items[connection.getID()].setPosition(packet.getPos().x, packet.getPos().y);
	              
	              
	              // PlayerArray response = new PlayerArray();
	              server.sendToAllExceptTCP(0, packet);
	              //server.sendToAllExceptTCP(0, npcs);
	              //connection.sendTCP(response);
	           }
	        }
	     });
	}
}
