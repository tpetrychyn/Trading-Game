package com.trading.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.trading.game.Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.width = 1280;
		cfg.height = 720;
		cfg.forceExit = true;  
		cfg.vSyncEnabled = false;
		new LwjglApplication(new Game(), cfg);
	}
}
