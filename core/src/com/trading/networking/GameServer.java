package com.trading.networking;

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
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.trading.entities.Npc;
import com.trading.entities.Player;
import com.trading.game.Game;
import com.trading.game.GameWorld;
import com.trading.networking.packets.ClientRequest;
import com.trading.networking.packets.NewConnection;
import com.trading.networking.packets.NpcMovePacket;
import com.trading.networking.packets.PlayerMovePacket;

public class GameServer extends ApplicationAdapter implements ApplicationListener {
	
	static Server server;
	static Stage stage;
	GameWorld gameWorld;
	TiledMap map;
	Player players[];
	Camera camera;
	NpcMovePacket npcs[];
	BitmapFont font;
	Batch debugBatch;
	
	IsometricTiledMapRenderer mapRenderer;
	private int[] backgroundLayers = new int[] {0, 1}, foreground = new int[] {2};
	int connectedClients;
	
	@Override
	public void create () {
		connectedClients = 0;
		gameWorld = new GameWorld("Maps/map.tmx");
		stage = new Stage();
		debugBatch = new SpriteBatch();
		font = new BitmapFont();
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.
                getHeight());
		
		npcs = new NpcMovePacket[100];
		for (int i=0;i<100;i++)
			npcs[i] = new NpcMovePacket();
		
		
			//first 100 actors are npcs
		for(Iterator<Actor> i = gameWorld.getActors().iterator(); i.hasNext(); ) {
			try {
				Npc n = (Npc) i.next();
				stage.addActor(n);
			} catch(Exception e) {
				
			}
		}
		
		// then add players
		players = new Player[100];
		for (int i=0;i<100;i++) {
			players[i] = new Player(gameWorld);
			stage.addActor(players[i]);
		}
		
		map = new TmxMapLoader().load("Maps/map.tmx");
		mapRenderer = new IsometricTiledMapRenderer(map);
		
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
		
		
		//stage.getCamera().position.set(player.getPosition().x + player.getWidth() / 2, player.getPosition().y, 0);
		stage.getViewport().setCamera(camera);
		mapRenderer.setView((OrthographicCamera) stage.getCamera());
		mapRenderer.render(backgroundLayers);
		
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		stage.setDebugAll(true);
		
		mapRenderer.render(foreground);
		
		debugBatch.begin();
		font.setColor(Color.WHITE);
		font.draw(debugBatch, "Connected Clients: " + connectedClients, 50, 50);
		debugBatch.end();
	}	
	
	public void startServer() {
		server = new Server();
	    server.start();
	    try {
			server.bind(Network.PORT_TCP, Network.PORT_UDP);
			System.out.println("Server started");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
	    Network.register(server);
	    
	    server.addListener(new Listener() {
	        public void received (Connection connection, Object object) {
	        	if (object instanceof NewConnection) {
	        		NewConnection res = (NewConnection)object;
	        		((Player)stage.getActors().items[res.clientId+100]).setPosition(res.pos);
	        		connectedClients++;
	        	} else if (object instanceof PlayerMovePacket) {
	              PlayerMovePacket request = (PlayerMovePacket)object;
	              PlayerMovePacket packet = new PlayerMovePacket(request.pos, connection.getID());
	              stage.getActors().items[connection.getID()+100].setPosition(packet.getPos().x, packet.getPos().y);
	              server.sendToAllExceptTCP(0, packet);
	              
	           } else if (object instanceof ClientRequest) {
	        	   ClientRequest r = (ClientRequest)object;
	        	   switch (r.request) {
	        	   case getNpcs:
	        		   for (int i=0;i<100;i++) {
	 	            	  Npc n = (Npc) stage.getActors().items[i];
	 	            	  if (n == null)
	 	            		  return;
	 	            	  npcs[i] = new NpcMovePacket(new Vector2(n.getX(), n.getY()), i);
	 	            	  server.sendToAllExceptTCP(0, npcs[i]);
	 	              }
	 	              //server.sendToAllExceptTCP(0, npcs);
	        		   break;
				default:
					break;
	        	   }
	           }
	        }
	        public void disconnected (Connection connection) {
	        	connectedClients--;
	        }
	     });
	}
	
	public static void updateActor(int id) {
		Actor a = stage.getActors().items[id];
		if (a==null)
			return;
		NpcMovePacket n = new NpcMovePacket();
		n.pos = new Vector2(a.getX(), a.getY());
		n.npcId = id;
		server.sendToAllExceptTCP(0, n);
	}
}



