package com.trading.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;

public class ChatBox extends Actor implements InputProcessor{
    TextField field;
    TextArea textArea;
    float height;
    public boolean showTextEnter = false;
    public boolean shouldFade = false;
    public float fade = 10;
    
    
    public ChatBox() {
    	/*Skin skin = new Skin();
skin.add(
        "background",
        new NinePatch(this.game.manager.get("hud/ninepatchframe.png",
                Texture.class), 5, 5, 5, 5));
skin.add("cursor", this.game.manager.get("data/cursor.png"));*/
    	Skin skin = new Skin();
    	skin.add("cursor", new Texture("male_walk.png"));
    	
    	TextFieldStyle t = new TextFieldStyle();
    	t.font = new BitmapFont();
    	t.fontColor = Color.RED;
    	t.cursor = skin.newDrawable("cursor", Color.GREEN);
    	t.cursor.setMinWidth(2f);
    	//t.background = skin.getDrawable("background");
        field = new TextField("", t);
        field.setPosition(100, 100);
       
        textArea = new TextArea("", t);
        textArea.setPosition(100, 120);
        height = 20;
        
    }

	@Override
	public boolean keyDown(int keycode) {
		if (!Game.getPlayer().isTyping)
			return false;
		if (keycode == Input.Keys.ENTER) {
			Game.getPlayer().isTyping = false;
			Gdx.input.setInputProcessor(Game.getPlayer());
			if (height < 100)
				height += 20; {
				textArea.setHeight(height);
			}
			textArea.appendText("Player: " + field.getText() + "\n");
			field.setText("");
			fade = 10;
			shouldFade = true;
			showTextEnter = false;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		if (!Game.getPlayer().isTyping)
			return false;
		// TODO Auto-generated method stub
		field.appendText(character + "");
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
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
}
