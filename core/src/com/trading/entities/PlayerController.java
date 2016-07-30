package com.trading.entities;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.trading.game.Game;
import com.trading.game.Instance;
import com.trading.networking.ConnectionHandler;
import com.trading.networking.packets.InstancePacket;
import com.trading.networking.packets.NpcMovePacket;
import com.trading.networking.packets.PlayerDataPacket;

public class PlayerController extends Player implements InputProcessor {
	

    public int instanceId = 1;
	public boolean isTyping = false;
	public Instance instance;
	ConnectionHandler connectionHandler;
	public Listener connectionListener;
	
    Vector3 mousePos = new Vector3(0, 0, 0);
	
	public PlayerController(Instance instance) {
		super(instance);
        this.instance = instance;
        connectionHandler = new ConnectionHandler();
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
        		|| instance.isCellBlocked(getWorldPosition().x, getWorldPosition().y)
        		|| instance.actorCollision(this)){
        	setY(oldPos.y);
        	setX(oldPos.x);
        }
        
        if (connectionHandler.client != null && connectionHandler.client.isConnected()) {
        	PlayerDataPacket p = new PlayerDataPacket(connectionHandler.client.getID(), instanceId, getPosition());
			if (p.playerData.pos.x != oldPos.x || p.playerData.pos.y != oldPos.y)
				connectionHandler.client.sendTCP(p);
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
			if (connectionHandler.client != null)
				return false;
			connectionListener = new Listener() {
		        public void received (Connection connection, final Object object) {
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
			        		        	 instance.getActors().put(response.npcId, newn);
			        		         }
			        		      });
			        	   } else {
			        		   instance.getActors().get(n.npcId).setPosition(n.x, n.y);
			        	   }
			        }
		        	if (object instanceof PlayerDataPacket) {
			        	  PlayerDataPacket packet = (PlayerDataPacket)object;
			        	  if (instance.getPlayers().get(packet.id) == null) {
			        		   Gdx.app.postRunnable(new Runnable() {
			        		         @Override
			        		         public void run() {
			        		        	 PlayerDataPacket packet = (PlayerDataPacket)object;
			        		        	 Player p = new Player(instance);
			        		        	 instance.getPlayers().put(packet.id, p);
			        		         }
			        		      });
			        	   } else {
			        		   instance.getPlayers().get(packet.id).setPosition(packet.playerData.pos);
			        	   }
			         }
		        	
		        	if (object instanceof InstancePacket) {
			        	  InstancePacket packet = (InstancePacket)object;
			        	  if (packet.action == "leave") {
			        		  instance.getPlayers().remove(packet.clientId);
			        	  }
			         }
		        }
		        
		     };
			connectionHandler.connectToServer(connectionListener);
			
		}
		
		if (keycode == Input.Keys.NUM_4) {
			InstancePacket in = new InstancePacket(instanceId, "leave");
			connectionHandler.client.sendTCP(in);
			instanceId = 1;
			instance.getActors().clear();
		}
		if (keycode == Input.Keys.NUM_5) {
			InstancePacket in = new InstancePacket(instanceId, "leave");
			connectionHandler.client.sendTCP(in);
			instanceId = 2;
			instance.getActors().clear();
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
	
	public Vector2 getMousePosition() {
		return new Vector2(mousePos.x, mousePos.y);
	}
	
	public Vector2 getTileClicked() {
		Vector2 m = new Vector2(mousePos.x, mousePos.y);
		return instance.getWorldPosition(m);
	}
	
	float distanceToPoint(Vector2 pt) {
		return (float) Math.sqrt((getX()-pt.x)*(getX()-pt.x) + (getY()-pt.y)*(getY()-pt.y));
	}

}
