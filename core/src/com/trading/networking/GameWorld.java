package com.trading.networking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.trading.entities.NpcController;
import com.trading.entities.Player;
import com.trading.game.Game;
import com.trading.game.Util;

public class GameWorld {
	
	World world;
	TiledMap map;
	IsometricTiledMapRenderer mapRenderer;
	List<Actor> actors;
	MapLayers collisionLayers;
	ShapeRenderer sr;
	
	private static String[] Beginning = { "Kr", "Ca", "Ra", "Mrok", "Cru",
	         "Ray", "Bre", "Zed", "Drak", "Mor", "Jag", "Mer", "Jar", "Mjol",
	         "Zork", "Mad", "Cry", "Zur", "Creo", "Azak", "Azur", "Rei", "Cro",
	         "Mar", "Luk" };
	   private static String[] Middle = { "air", "ir", "mi", "sor", "mee", "clo",
	         "red", "cra", "ark", "arc", "miri", "lori", "cres", "mur", "zer",
	         "marac", "zoir", "slamar", "salmar", "urak" };
	   private static String[] End = { "d", "ed", "ark", "arc", "es", "er", "der",
	         "tron", "med", "ure", "zur", "cred", "mur" };
	   
	   private static Random rand = new Random();

	   public static String generateName() {

	      return Beginning[rand.nextInt(Beginning.length)] + 
	            Middle[rand.nextInt(Middle.length)]+
	            End[rand.nextInt(End.length)];

	   }
	
	public GameWorld(String mapFile) {
		map = new TmxMapLoader().load(mapFile);
		mapRenderer = new IsometricTiledMapRenderer(map);
		world = new World(new Vector2(0, 0), true);
		this.collisionLayers = (MapLayers) getTiledMap().getLayers();
		actors = new ArrayList<Actor>();
		for (int i=0;i<100;i++) {
			NpcController npc = new NpcController(new Texture("male_idle.png"), Util.randomRange(0, 50), Util.randomRange(0, 50), this, i, 0.5f);
			npc.startRandomWalk(1);
			npc.setName(generateName());
			actors.add(npc);
		}
		sr = new ShapeRenderer();
	}
	
	public void addPlayer(Player p) {
		actors.add(p);
	}
	
	public List<Actor> getActors() {
		return actors;
	}
	
	public World getWorld() {
		return world;
	}
	
	public TiledMap getTiledMap() {
		return map;
	}

	public void dispose() {
		// TODO Auto-generated method stub
		world.dispose();
		map.dispose();
		mapRenderer.dispose();
	}
	
	public void setWorldPosition(Actor p, Vector2 pos) {
		p.setX((pos.x + ((pos.y-pos.x)/2)) * 64);
		p.setY((pos.y - pos.x) * 16);
    }
	
	public boolean isCellBlocked(float x, float y) {
		boolean blocked = false;
		for (int i=0;i<3;i++) {
			TiledMapTileLayer coll = (TiledMapTileLayer) collisionLayers.get(i);
			Cell cell = coll.getCell((int) (x), (int) (y));
			blocked = cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("blocked");
			if (blocked == true)
				return true;
		}
		return blocked;
	}
	
	Vector2 getTileCoordinates(Vector2 pt, float tileHeight) {
		  Vector2 tempPt = new Vector2(0, 0);
		  tempPt.x = pt.x / tileHeight / 2;
		  tempPt.y = pt.y / tileHeight;
		  return(tempPt);
	}
	
    public Vector2 getWorldPosition(Vector2 pt) {
    	return Util.twoDToIso(getTileCoordinates(pt, 32));
    }
	
	public boolean actorCollision(Actor self) {
		for(Iterator<Actor> i = Game.stage.getActors().iterator(); i.hasNext(); ) {
		    Actor a = i.next();
		    if (a.hashCode() == self.hashCode())
		    	continue;
			Rectangle p = new Rectangle(self.getX(), self.getY(), self.getWidth(), self.getHeight());
			Rectangle n = new Rectangle(a.getX(), a.getY(), a.getWidth(), a.getHeight());
			if (Intersector.overlaps(p, n)) {
				return true;
			}
		}
		return false;
	}
}
