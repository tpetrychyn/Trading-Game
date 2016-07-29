package com.trading.entities;

import java.io.IOException;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.trading.game.Game;
import com.trading.networking.GameWorld;
import com.trading.networking.Network;
import com.trading.networking.packets.ClientRequest;
import com.trading.networking.packets.NewConnection;
import com.trading.networking.packets.NpcMovePacket;
import com.trading.networking.packets.PlayerMovePacket;
import com.trading.networking.packets.Requests;

public class Player extends WorldActor implements InputProcessor {
	
	public boolean isTyping = false;
	
    Vector3 mousePos = new Vector3(0, 0, 0);
    GameWorld world;
    
    float playerSpeed = 200f;
    public boolean isMoving = false;
    
    PlayerMovePacket p;
    PlayerMovePacket players[];
    Npc npcs[];
	Client client;
	public int myId = 0;
    
    float stateTime = 0;
    
    public float getSpeed() {
    	return playerSpeed;
    }
    
    public void setSpeed(float speed) {
    	playerSpeed = speed;
    }
    
    public Vector2 getPosition() {
    	Vector2 pos = new Vector2(getX(), getY());
		return pos;
    }
    
    public Vector2 getWorldPosition() {
    	return world.getWorldPosition(getPosition());
    }
    
    public void setWorldPosition(Vector2 pos) {
    	setX((pos.x + ((pos.y-pos.x)/2)) * 64);
		setY((pos.y - pos.x) * 16);
    }
    
    public void setDirection(Direction dir) {
    	direction = dir;
    }
    
    public Direction getDirection() {
    	return direction;
    }
    
    public void setPosition(Vector2 transform) {
    	setX(transform.x);
    	setY(transform.y);
    }
	
	public Player(GameWorld world) {
		this.world = world;
		
		Texture t = new Texture(Gdx.files.internal("male_idle.png"), true);
		sprite = new Sprite(t);
		// Center the sprite in the top/middle of the screen
        sprite.setPosition(Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);
        p = new PlayerMovePacket();
        setScale(0.5f);
        
		walkAnimations = new Animation[8];
		Animator a = new Animator(9, 4, "male_walk.png");
        walkAnimations[0] = a.addAnimation(1, 7);
        walkAnimations[1] = a.addAnimation(10, 7);
        walkAnimations[2] = a.addAnimation(19, 7);
        walkAnimations[3] = a.addAnimation(28, 7);

        walkAnimations[4] = a.addAnimation(0, 1);
        walkAnimations[5] = a.addAnimation(9, 1);
        walkAnimations[6] = a.addAnimation(18, 1);
        walkAnimations[7] = a.addAnimation(27, 1);
        
        players = new PlayerMovePacket[100];
        npcs = new Npc[100];
	}
	
	public TextureRegion getCurrentTexture(float st) {
    	if (!isMoving)
    		return walkAnimations[direction.getValue() + 4].getKeyFrame(st);
    	
    	return walkAnimations[direction.getValue()].getKeyFrame(st, true);
    		
    }
	
	@Override
	public void draw(Batch batch, float alpha) {
		update(Gdx.graphics.getDeltaTime());
		sprite = new Sprite(getCurrentTexture(stateTime));
		batch.draw(sprite, getPosition().x, getPosition().y, getWidth(), getHeight());
	}
	
	
	void update(float deltaTime) {
		stateTime += deltaTime;
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
			p.clientID = 1;
			if (p.pos.x != oldPos.x || p.pos.y != oldPos.y)
				client.sendTCP(p);
        }
	}
	
	public Vector2 getMousePosition() {
		return new Vector2(mousePos.x, mousePos.y);
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
		NewConnection n = new NewConnection(client.getID(), new Vector2(getX(), getY()));
		client.sendTCP(n);
		myId = client.getID();
	    
	    client.addListener(new Listener() {
	        public void received (Connection connection, Object object) {
	           if (object instanceof PlayerMovePacket) {
	        	  PlayerMovePacket response = (PlayerMovePacket)object;
	        	  if (response.clientID != connection.getID())
	        		  Game.stage.getActors().items[response.clientID+100].setPosition(response.pos.x, response.pos.y);
	           } 
	           else if (object instanceof NpcMovePacket[]) {
	        	   NpcMovePacket[] response = (NpcMovePacket[])object;
	        	   for (int i=0;i<100;i++) {
	        		   System.out.println("got npcs");
	        		   Game.stage.getActors().items[i].setPosition(response[i].pos.x, response[i].pos.y);
	        	   }
	           }
	           else if (object instanceof NpcMovePacket) {
	        	   NpcMovePacket response = (NpcMovePacket)object;
	        	   NpcMovePacket n = new NpcMovePacket();
	        	   n.npcId = response.npcId;
	        	   n.pos = response.pos;
	        	   Game.stage.getActors().items[n.npcId].setPosition(n.pos.x, n.pos.y);
	           }
	        }
	        
	     });
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
			ClientRequest c = new ClientRequest(Requests.getNpcs);
			client.sendTCP(c);
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
			    		((Npc) a).stopRandomWalk();
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
	
	public Vector2 getTileClicked() {
		Vector2 m = new Vector2(mousePos.x, mousePos.y);
		return world.getWorldPosition(m);//twoDToIso(getTileCoordinates(m, 32));
	}
	
	float distanceToPoint(Vector2 pt) {
		return (float) Math.sqrt((getX()-pt.x)*(getX()-pt.x) + (getY()-pt.y)*(getY()-pt.y));
	}
}
