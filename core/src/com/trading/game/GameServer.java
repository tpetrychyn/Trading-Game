package com.trading.game;

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
	}
}
