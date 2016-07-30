package com.trading.networking;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;

public class ConnectionHandler {
	
	public Client client;
	/*
	void newConnection(final NewConnection conn, final Connection c) {
		Gdx.app.postRunnable(new Runnable() {
	         @Override
	         public void run() {
	            // process the result, e.g. add it to an Array<Result> field of the ApplicationListener.
	        	Player p = new Player(new Instance(""));
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
	}*/
	
	public void connectToServer(Listener connectionListener) {
		client = new Client();
	    client.start();
	    try {
			client.connect(5000, "localhost", Network.PORT_TCP, Network.PORT_UDP);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    Network.register(client);
	    System.out.println("Connected to server");
	    
	    client.addListener(connectionListener);
	}
}
