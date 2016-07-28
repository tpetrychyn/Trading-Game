package com.trading.game;

import java.io.IOException;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.trading.game.PlayerMovePacket;

public class GameServer {
	
	Server server;
	public GameServer() {
		server = new Server();
	    server.start();
	    try {
			server.bind(54555, 54777);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
	    Kryo kryo = server.getKryo();
	    kryo.register(PlayerMovePacket.class);
	    kryo.register(Vector2.class);
	    
	    server.addListener(new Listener() {
	        public void received (Connection connection, Object object) {
	           if (object instanceof PlayerMovePacket) {
	              PlayerMovePacket request = (PlayerMovePacket)object;
	              PlayerMovePacket packet = new PlayerMovePacket(request.pos, connection.getID());
	              System.out.println(packet.getPos());
	              
	              
	             // PlayerArray response = new PlayerArray();
	              server.sendToAllExceptTCP(0, packet);
	              //connection.sendTCP(response);
	           }
	        }
	     });
	}
}
