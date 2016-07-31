package com.trading.entities;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.trading.entities.WorldObjects.TreePrefab;
import com.trading.game.Game;
import com.trading.game.Instance;
import com.trading.game.Util;
import com.trading.networking.ConnectionHandler;
import com.trading.networking.ConnectionListener;
import com.trading.networking.packets.InstancePacket;
import com.trading.networking.packets.NpcMovePacket;
import com.trading.networking.packets.PlayerDataPacket;
import com.trading.networking.packets.WorldObjectPacket;

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
        		|| getWorldPosition().x > instance.worldWidth || getWorldPosition().y > instance.worldHeight
        		|| instance.isCellBlocked(getWorldPosition().x, getWorldPosition().y)
        		|| instance.actorCollision(this)){
        	setY(oldPos.y);
        	setX(oldPos.x);
        }
        
        if (connectionHandler.client != null && connectionHandler.client.isConnected()) {
        	PlayerDataPacket p = new PlayerDataPacket(connectionHandler.client.getID(), instanceId, getPosition(), direction);
			if (p.playerData.pos.x != oldPos.x || p.playerData.pos.y != oldPos.y)
				connectionHandler.client.sendUDP(p);
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
			connectionHandler.connectToServer(new ConnectionListener());
			
		}
		
		if (keycode == Input.Keys.NUM_4) {
			InstancePacket in = new InstancePacket(instanceId, "leave");
			connectionHandler.client.sendTCP(in);
			instanceId = 1;
			instance.getActors().clear();
			
			instance = new Instance("map.tmx");
			instance.addPlayer(this);
			setWorldPosition(new Vector2(1,1));
			
			in = new InstancePacket(instanceId, "join", getPosition());
			connectionHandler.client.sendTCP(in);
		}
		if (keycode == Input.Keys.NUM_5) {
			InstancePacket in = new InstancePacket(instanceId, "leave");
			connectionHandler.client.sendTCP(in);
			instanceId = 2;
			instance.getActors().clear();
			
			instance = new Instance("house.tmx");
			instance.addPlayer(this);
			setWorldPosition(new Vector2(10,1));
			
			in = new InstancePacket(instanceId, "join", getPosition());
			connectionHandler.client.sendTCP(in);
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
		for (int key: instance.getActors().keySet()) {
			Actor a = instance.actors.get(key);
			if (mousePos.x < a.getX() + a.getWidth() 
			&& mousePos.x > a.getX() 
			&& mousePos.y < a.getY() + a.getHeight() 
			&& mousePos.y > a.getY()) {
		    	if (distanceToPoint(new Vector2(a.getX(),a.getY())) < 30) {
		    		System.out.println(((Npc) a).id);
		    		((Npc) a).setColor(Color.WHITE);
		    	}
			}
		}
		for (int key: instance.worldObjects.keySet()) {
			WorldActor a = instance.worldObjects.get(key);
			if (mousePos.x < a.getX() + a.getWidth() 
			&& mousePos.x > a.getX() 
			&& mousePos.y < a.getY() + a.getHeight() 
			&& mousePos.y > a.getY()) {
		    	if (distanceToPoint(new Vector2(a.getX() + a.getOriginX(),a.getY())) < 50) {
		    		System.out.println(key);
		    	}
			}
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
		return (float) Math.sqrt((getX()+getOriginX()-pt.x)*(getX()+getOriginX()-pt.x) + (getY()+getOriginY()-pt.y)*(getY()+getOriginY()-pt.y));
	}
	
	float distanceToPoint(Vector2 pt, Vector2 other) {
		return (float) Math.sqrt((other.x-pt.x)*(other.x-pt.x) + (other.y-pt.y)*(other.y-pt.y));
	}

}
