package com.trading.game;

<<<<<<< HEAD
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GameServer extends ApplicationAdapter implements ApplicationListener {
	
	public class MyActor extends Actor {
        Texture texture = new Texture(Gdx.files.internal("male_idle.png"));
        @Override
        public void draw(Batch batch, float alpha){
            batch.draw(texture,0,0);
        }
    }
	
	Stage stage;
	
	@Override
	public void create () {
		stage = new Stage();
		Actor myActor = new MyActor();
		stage.addActor(myActor);
	
	}
	
	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.draw();
=======
import java.io.IOException;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

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
>>>>>>> 985279adc0746d321055a0c3176510353795fcf4
	}
}
