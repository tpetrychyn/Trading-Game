package com.trading.entities;

import java.io.IOException;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.trading.game.Game;
import com.trading.networking.GameWorld;
import com.trading.networking.Network;
import com.trading.networking.packets.Disconnection;
import com.trading.networking.packets.NpcMovePacket;
import com.trading.networking.packets.PlayerDataPacket;
import com.trading.networking.packets.PlayerMovePacket;

public class PlayerController extends Player implements InputProcessor {
	

    PlayerMovePacket p;
    PlayerMovePacket players[];
    Npc npcs[];
	Client client;
	public boolean isTyping = false;
	
    Vector3 mousePos = new Vector3(0, 0, 0);
	
	public PlayerController(GameWorld world) {
		super(world);
		// TODO Auto-generated constructor stub
		p = new PlayerMovePacket();
		players = new PlayerMovePacket[100];
        npcs = new Npc[100];
	}

	@Override
	public void draw(Batch batch, float alpha) {
		float deltaTime = Gdx.graphics.getDeltaTime();
		if (isTyping)
			return;
		Vector2 playerVelocity = new Vector2();
        // On right or left arrow set the velocity at a fixed rate in that direction
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
        	playerVelocity.x = getSpeed();
        	setDirection(Direction.EAST);
        } else if(Gdx.input.isKeyPressed(Input.Keys.A)) {
        	playerVelocity.x = -getSpeed();
        	setDirection(Direction.WEST);
        } else {
        	playerVelocity.x = 0;
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
        	playerVelocity.y = getSpeed()/2;
        	setDirection(Direction.NORTH);
        } else if(Gdx.input.isKeyPressed(Input.Keys.S)) {
        	playerVelocity.y = -getSpeed()/2;
        	setDirection(Direction.SOUTH);
        } else {
        	playerVelocity.y = 0;
        }
        
        if (playerVelocity.y == 0 && playerVelocity.x == 0)
        	isMoving = false;
        else
        	isMoving = true;
        
        Vector2 oldPos = getPosition();
        setX(getX() + playerVelocity.x * deltaTime);
        setY(getY() + playerVelocity.y * deltaTime);
        
        if (getWorldPosition().x < 0 || getWorldPosition().y < 0.2
        		|| getWorldPosition().x > 99.8 || getWorldPosition().y > 100
        		|| world.isCellBlocked(getWorldPosition().x, getWorldPosition().y)
        		|| world.actorCollision(this)){
        	setY(oldPos.y);
        	setX(oldPos.x);
        }
        
        if (client != null && client.isConnected()) {
			p.pos.x = getX();
			p.pos.y = getY();
			p.clientID = client.getID();
			if (p.pos.x != oldPos.x || p.pos.y != oldPos.y){}
				//client.sendTCP(p);
        }
        
        super.draw(batch, deltaTime);
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.ENTER) {
			//world.setWorldPosition(this, new Vector2(50,50));
			Game.chatbox.showTextEnter = true;
			Game.chatbox.shouldFade = false;
			Game.chatbox.fade = 10f;
			isTyping = true;
			Gdx.input.setInputProcessor(Game.chatbox);
		}
		
		if (keycode == Input.Keys.NUM_3) {
			if (client != null)
				return false;
			connectToServer();
		}
		
		if (keycode == Input.Keys.NUM_4) {
			for (int i=0;i<100;i++) {
				if (npcs[i] == null)
					continue;
				System.out.println(npcs[i].getX());
			}
		}
		if (keycode == Input.Keys.NUM_5) {
			PlayerDataPacket p = new PlayerDataPacket(client.getID(), 1, getPosition());
		    client.sendTCP(p);
		}
		
		if (keycode == Input.Keys.NUM_6) {
			PlayerDataPacket p = new PlayerDataPacket(client.getID(), 2, getPosition());
		    client.sendTCP(p);
		}
		if (keycode == Input.Keys.ESCAPE)
			Gdx.app.exit();
		return false;
	}
	
	public String ip = "";

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		
		mousePos = Game.getCamera().unproject(new Vector3(screenX, screenY, 0));
		try {
			for(Iterator<Actor> i = this.getStage().getActors().iterator(); i.hasNext(); ) {
			    Actor a = i.next();
			    if (mousePos.x < a.getX() + a.getWidth() 
						&& mousePos.x > a.getX() 
						&& mousePos.y < a.getY() + a.getHeight() 
						&& mousePos.y > a.getY()) {
			    	if (distanceToPoint(new Vector2(a.getX(),a.getY())) < 30) {
			    		System.out.println(((Npc) a).id);
			    		//((NpcController) a).stopRandomWalk();
			    		((Npc) a).setColor(Color.WHITE);
			    	}
					return false;
				}
			}
		} catch(Exception e) {
			
		}
		return false;
	}
	

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void connectToServer() {
		client = new Client(20000, 10000);
	    client.start();
	    try {
			client.connect(5000, "localhost", Network.PORT_TCP, Network.PORT_UDP);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    Network.register(client);
	    System.out.println("Connected to server");
	    
	    client.addListener(new Listener() {
	        public void received (Connection connection, Object object) {
	        	if (object instanceof NpcMovePacket) {
		        	   NpcMovePacket response = (NpcMovePacket)object;
		        	   NpcMovePacket n = new NpcMovePacket();
		        	   n.npcId = response.npcId;
		        	   n.x = response.x;
		        	   n.y = response.y;
		        	   Game.stage.getActors().items[n.npcId].setPosition(n.x, n.y);
		        	   Game.stage.getActors().items[n.npcId].setName(response.name);
		        	   
		           }
	        	/*
	           if (object instanceof PlayerMovePacket) {
	        	  PlayerMovePacket response = (PlayerMovePacket)object;
	        	  Game.stage.getActors().items[response.clientID+100].setPosition(response.pos.x, response.pos.y);  
	           } else if (object instanceof NpcMovePacket[]) {
	        	   NpcMovePacket[] response = (NpcMovePacket[])object;
	        	   for (int i=0;i<100;i++) {
	        		   Game.stage.getActors().items[i].setPosition(response[i].x, response[i].y);
	        	   }
	           } else if (object instanceof NpcMovePacket) {
	        	   System.out.println("recieved");
	        	   NpcMovePacket response = (NpcMovePacket)object;
	        	   NpcMovePacket n = new NpcMovePacket();
	        	   n.npcId = response.npcId;
	        	   n.x = response.x;
	        	   n.y = response.y;
	        	   Game.stage.getActors().items[n.npcId].setPosition(n.x, n.y);
	        	   Game.stage.getActors().items[n.npcId].setName(response.name);
	        	   System.out.println(n.npcId);
	        	   
	           } else if (object instanceof Disconnection) {
	        	   Disconnection disc = (Disconnection)object;
	        	   System.out.println("disconnection " + (disc.pId+100));
	        	   Game.stage.getActors().items[disc.pId+100].setPosition(-50, -100);
	           } else if (object instanceof PlayerData) {
	        	   PlayerData pd = (PlayerData)object;
	        	   System.out.println("got playerdata " + pd.pId + " health " + pd.health + " changing " + (pd.pId));
	        	   ((Player) Game.stage.getActors().items[pd.pId]).playerData = pd;
	           }*/
	        }
	        
	     });
	}
	
	public Vector2 getMousePosition() {
		return new Vector2(mousePos.x, mousePos.y);
	}
	
	public Vector2 getTileClicked() {
		Vector2 m = new Vector2(mousePos.x, mousePos.y);
		return world.getWorldPosition(m);//twoDToIso(getTileCoordinates(m, 32));
	}
	
	float distanceToPoint(Vector2 pt) {
		return (float) Math.sqrt((getX()-pt.x)*(getX()-pt.x) + (getY()-pt.y)*(getY()-pt.y));
	}

}
