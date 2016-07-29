package com.trading.networking;

import java.io.IOException;
import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.trading.networking.packets.ClientRequest;
import com.trading.networking.packets.Disconnection;
import com.trading.networking.packets.NewConnection;
import com.trading.networking.packets.NpcMovePacket;
import com.trading.networking.packets.PlayerMovePacket;

public class GameServer extends ApplicationAdapter implements ApplicationListener {
	
	public static Server server;
	static Stage stage;
	GameWorld gameWorld;
	TiledMap map;
	Camera camera;
	static NpcMovePacket npcs[];
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
		
		map = new TmxMapLoader().load("Maps/map.tmx");
		mapRenderer = new IsometricTiledMapRenderer(map);
		
		Game.stage = stage;
		
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();
		startServer();
	}
	
	public static Stage getStage() {
		return stage;
	}
	
	Connection connection;
	float tick = 0;
	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		tick += Gdx.graphics.getDeltaTime();
		
		stage.getViewport().setCamera(camera);
		mapRenderer.setView((OrthographicCamera) stage.getCamera());
		mapRenderer.render(backgroundLayers);
		
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		stage.setDebugAll(true);
		
		mapRenderer.render(foreground);
		
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
        	stage.getCamera().translate(10, 0, 0);
        } else if(Gdx.input.isKeyPressed(Input.Keys.A)) {
        	stage.getCamera().translate(-10, 0, 0);
        }
		
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			stage.getCamera().translate(0, 10, 0);
        } else if(Gdx.input.isKeyPressed(Input.Keys.S)) {
        	stage.getCamera().translate(0, -10, 0);
        }
		
		if (tick > 5) {
			if (stage.getActors().items[101] != null) {
				Player p = (Player) stage.getActors().items[101];
				p.playerData.health -= 5;
				p.playerData.stamina -= 5;
				p.playerData.pId = 101;
				server.sendToAllTCP(p.playerData);
			}
			tick = 0;
		}
		
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
	        		newConnection(res, connection);
	        	} else if (object instanceof PlayerMovePacket) {
	        		PlayerMovePacket request = (PlayerMovePacket)object;
	        		System.out.print(connection.getID() + " ");
	  	             PlayerMovePacket packet = new PlayerMovePacket(request.pos, connection.getID());
	  	             System.out.println("got from " + (connection.getID()+99) + " " + stage.getActors().items[connection.getID()+99]);
	  	             stage.getActors().items[connection.getID()+99].setPosition(packet.getPos().x, packet.getPos().y);
		  	         server.sendToAllExceptTCP(connection.getID(), packet);
	           }
	        	//Used when client asks the server for data without sending any
	        	else if (object instanceof ClientRequest) {
	        	   ClientRequest r = (ClientRequest)object;
	        	   handleRequest(r);
	           }
	        }
	        
	        public void disconnected (Connection connection) {
	        	handleDisconnect(connection);
	        }
	     });
	}
	
	void handleDisconnect(Connection c) {
    	connectedClients--;
    	server.sendToAllExceptTCP(c.getID(), new Disconnection(c.getID()));
    	stage.getActors().items[c.getID()+99].setPosition(-50, -100);
	}
	
	void handleRequest(ClientRequest req) {
		switch (req.request) {
	 	   case getNpcs:
	 		   for (int i=0;i<100;i++) {
	          	  Npc n = (Npc) stage.getActors().items[i];
	          	  if (n == null)
	          		  return;
	          	  npcs[i] = new NpcMovePacket(n.getX(), n.getY(), i, n.getName());
	          	  server.sendToAllExceptTCP(0, npcs[i]);
	            }
	 		   break;
			default:
				break;
		}
	}
	
	void newConnection(final NewConnection conn, final Connection c) {
		Gdx.app.postRunnable(new Runnable() {
	         @Override
	         public void run() {
	            // process the result, e.g. add it to an Array<Result> field of the ApplicationListener.
	        	Player p = new Player(gameWorld);
	        	p.setPosition(conn.pos);
	        	stage.addActor(p);
	    		//load npcs
	    		for (int i=0;i<100;i++) {
	            	  Npc n = (Npc) stage.getActors().items[i];
	            	  if (n == null)
	            		  return;
	            	  npcs[i] = new NpcMovePacket(n.getX(), n.getY(), i, n.getName());
	            	  server.sendToAllExceptTCP(0, npcs[i]);
	             }
	    		
	    		
	    		//load players already on server - this works
	    		for (int i=100;i<stage.getActors().size;i++) {
	    			if (i == c.getID()+99)
	    				continue;
	    			Actor a = stage.getActors().items[i];
	    			if (a == null)
	    				return;
	    			PlayerMovePacket packet = new PlayerMovePacket(new Vector2(a.getX(), a.getY()), i-99);
	                server.sendToTCP(c.getID(), packet);
	    		}
	    		
	    		//tell every other player on the server where you joined - this isnt working
	    		Player newP = (Player) stage.getActors().items[c.getID()+99];
	    		PlayerMovePacket packet2 = new PlayerMovePacket(new Vector2(newP.getX(), newP.getY()), c.getID());
	    		server.sendToAllExceptTCP(c.getID(), packet2);
	    		connectedClients++;
	        	//((Player)stage.getActors().items[conn.clientId+99]).setPosition(conn.pos);
	         }
	      });
	}
	
	//Handles when a new user connects
	/*void newConnection(final NewConnection conn, final Connection c) {
		Gdx.app.postRunnable(new Runnable() {
	         @Override
	         public void run() {
	            // process the result, e.g. add it to an Array<Result> field of the ApplicationListener.
	        	Player p = new Player(gameWorld);
	        	//p.setPosition(conn.pos);
	        	stage.addActor(p);
	        	for (int i=0;i<stage.getActors().size;i++) {
	    			System.out.println(i + ": " + stage.getActors().items[i] + " " + stage.getActors().items[i].getX() + " " + stage.getActors().items[i].getY());
	    		}
	    		//load npcs
	    		for (int i=0;i<100;i++) {
	            	  Npc n = (Npc) stage.getActors().items[i];
	            	  if (n == null)
	            		  return;
	            	  npcs[i] = new NpcMovePacket(n.getX(), n.getY(), i, n.getName());
	            	  server.sendToAllExceptTCP(0, npcs[i]);
	              }
	    		
	    		//load players
	    		for (int i=100;i<stage.getActors().size;i++) {
	    			if (i == c.getID())
	    				continue;
	    			Actor a = stage.getActors().items[i];
	    			if (a == null)
	    				return;
	    			PlayerMovePacket packet = new PlayerMovePacket(new Vector2(a.getX(), a.getY()), i-100);
	                server.sendToAllExceptTCP(0, packet);
	    		}
	    		connectedClients++;
	        	((Player)stage.getActors().items[conn.clientId+99]).setPosition(conn.pos);
	         }
	      });
	}*/
	
	public static void updateActor(int id) {
		Actor a = stage.getActors().items[id];
		if (a==null)
			return;
		NpcMovePacket n = new NpcMovePacket(a.getX(), a.getY(), id, a.getName());
		server.sendToAllExceptTCP(0, n);
	}
}



