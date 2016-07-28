package com.trading.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
    public float fade = 0;
    
    public ChatBox() {
    	Skin skin = new Skin();
    	//dunno how this works but the int values seem to be padding
    	NinePatch bg = new NinePatch(new Texture("chatbox.png"), 5,5,5,5);
    	skin.add("background", bg);
    	
    	TextureAtlas atlas;
    	atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
    	
    	skin.add("cursor", atlas.findRegion("cursor"));
    	
    	Skin skin2 = new Skin(Gdx.files.internal("uiskin.json"));
    	
    	//The field that the player types their text in to
    	TextFieldStyle t = new TextFieldStyle();
    	t.font = new BitmapFont();
    	t.fontColor = Color.RED;
    	//t.cursor = skin.getDrawable("cursor");
        field = new TextField("", skin2);
        field.setWidth(250);
        field.setMaxLength(30);
        field.setPosition(100, 100);
        
        //the area that the chat messages are displayed
        TextFieldStyle box = new TextFieldStyle();
        box.background = skin.getDrawable("background");
        box.font = new BitmapFont();
        box.fontColor = Color.GREEN;
        textArea = new TextArea("", skin2);
        textArea.setWidth(300);
        textArea.setHeight(100);
        textArea.setPosition(100, 120);
        
    }

	@Override
	public boolean keyDown(int keycode) {
		if (!Game.getPlayer().isTyping)
			return false;
		if (keycode == Input.Keys.ENTER) {
			Game.getPlayer().isTyping = false;
			Gdx.input.setInputProcessor(Game.getPlayer());
			textArea.appendText("Player: " + field.getText() + "\n");
			field.setText("");
			fade = 10;
			shouldFade = true;
			showTextEnter = false;
		}
		
		if (keycode == Input.Keys.BACK) {
			//field.
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
