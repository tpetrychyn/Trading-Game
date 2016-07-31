package com.trading.networking;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import com.trading.game.Game;
import com.trading.networking.packets.InstancePacket;

public class ConnectionHandler {
	
	public Client client;
	
	public void connectToServer(Listener connectionListener) {
		client = new Client(20000, 20000);
	    client.start();
	    try {
			client.connect(5000, Game.ip, Network.PORT_TCP, Network.PORT_UDP);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    Network.register(client);
	    System.out.println("Connected to server");
	    
	    client.addListener(connectionListener);
	    
	    InstancePacket in = new InstancePacket(Game.player.instanceId, "join", Game.player.getPosition());
		client.sendTCP(in);
	}
}
