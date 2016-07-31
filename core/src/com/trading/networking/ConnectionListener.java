package com.trading.networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.trading.entities.Npc;
import com.trading.entities.Player;
import com.trading.entities.Tree;
import com.trading.entities.WorldObjects;
import com.trading.game.Game;
import com.trading.game.Instance;
import com.trading.networking.packets.InstancePacket;
import com.trading.networking.packets.NpcMovePacket;
import com.trading.networking.packets.PlayerDataPacket;
import com.trading.networking.packets.WorldObjectPacket;

public class ConnectionListener extends Listener {
	static Instance instance;
	public void received (Connection connection, final Object object) {
		instance = Game.player.instance;
		
    	if (object instanceof NpcMovePacket) {
		   NpcMovePacket response = (NpcMovePacket)object;
    	   NpcMovePacket n = new NpcMovePacket();
    	   n.npcId = response.npcId;
    	   n.x = response.x;
    	   n.y = response.y;
    	   if (instance.getActors().get(n.npcId) == null) {
    		   Gdx.app.postRunnable(new Runnable() {
    		         @Override
    		         public void run() {
    		        	 NpcMovePacket response = (NpcMovePacket)object;
    		            // process the result, e.g. add it to an Array<Result> field of the ApplicationListener.
    		        	 Npc newn = new Npc(new Texture("male_walk.png"), response.x, response.y, instance, response.npcId, 0.5f, response.name);
    		        	 newn.direction = response.direction;
    		        	 instance.getActors().put(response.npcId, newn);
    		         }
    		      });
    	   } else {
    		   ((Npc) instance.getActors().get(n.npcId)).direction = response.direction;
    		   ((Npc) instance.getActors().get(n.npcId)).isMoving = true;
    		   ((Npc) instance.getActors().get(n.npcId)).timeSinceMove = 0;
    		   instance.getActors().get(n.npcId).setPosition(n.x, n.y);
    		   
    	   }
        }
    	
    	if (object instanceof NpcMovePacket[]) {
    	   final NpcMovePacket[] response = (NpcMovePacket[])object;
    	   Gdx.app.postRunnable(new Runnable() {
	         @Override
	         public void run() {
	        	 for (int i=0;i<response.length;i++) {
	        		 NpcMovePacket n = response[i];
	        		 if (Game.player.instance.getActors().get(n.npcId) == null) {
    		        	 Npc newn = new Npc(new Texture("male_walk.png"), n.x, n.y, instance, n.npcId, 0.5f, n.name);
    		        	 Game.player.instance.getActors().put(n.npcId, newn);
	        		 }
	        	 }
	         }
	      });
    	}
    	
    	if (object instanceof PlayerDataPacket) {
        	  PlayerDataPacket packet = (PlayerDataPacket)object;
        	  if (instance.getPlayers().get(packet.id) == null) {
        		   Gdx.app.postRunnable(new Runnable() {
        		         @Override
        		         public void run() {
        		        	 PlayerDataPacket packet = (PlayerDataPacket)object;
        		        	 Player p = new Player(instance);
        		        	 p.setPosition(packet.playerData.pos);
        		        	 instance.getPlayers().put(packet.id, p);
        		         }
        		      });
        	   } else {
        		   instance.getPlayers().get(packet.id).setPosition(packet.playerData.pos);
        		   instance.getPlayers().get(packet.id).direction = packet.playerData.direction;
        		   instance.getPlayers().get(packet.id).lastMoved = 0;
        	   }
         }
    	
    	if (object instanceof InstancePacket) {
        	  InstancePacket packet = (InstancePacket)object;
        	  if (packet.action.equals("leave")) {
        		  System.out.println("got leave from " + packet.clientId);
        		  instance.getPlayers().remove(packet.clientId);
        	  }
         }
    	
    	if (object instanceof WorldObjectPacket[]) {
    		final WorldObjectPacket[] packet = (WorldObjectPacket[])object;
    		Gdx.app.postRunnable(new Runnable() {
		         @Override
		         public void run() {
		        	 for (int i=0;i<packet.length;i++) {
		        		 WorldObjectPacket p = packet[i];
		        		 if (Game.player.instance.worldObjects.get(p.id) == null) {
		        			 if (p.type.equals("tree")) {
		        				 Tree tree = new Tree(p.x, p.y, Game.player.instance, WorldObjects.trees.get(p.typeId));
		        				 Game.player.instance.worldObjects.put(p.id, tree);
		        			 }
		        		 }
		        	 }
		         }
		      });
    	}
    	
    	if (object instanceof PlayerDataPacket[]) {
    		final PlayerDataPacket[] packet = (PlayerDataPacket[])object;
    		Gdx.app.postRunnable(new Runnable() {
		         @Override
		         public void run() {
		        	 for (int i=0;i<packet.length;i++) {
		        		 PlayerDataPacket p = packet[i];
		        		 if (Game.player.instance.players.get(p.id) == null) {
		        			 Player player = new Player(Game.player.instance);
		        			 player.setPosition(p.playerData.pos);
		        			 player.id = p.id;
		        			 Game.player.instance.players.put(p.id, player);
		        		 }
		        	 }
		         }
		    });
    	}
    	
    }
}
