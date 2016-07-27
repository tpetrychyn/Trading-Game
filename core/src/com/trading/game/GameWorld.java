package com.trading.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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
import com.trading.entities.Npc;
import com.trading.entities.Player;

public class GameWorld {
	
	World world;
	TiledMap map;
	IsometricTiledMapRenderer mapRenderer;
	List<Actor> actors;
	private int[] backgroundLayers = new int[] {0, 1}, foreground = new int[] {2};
	MapLayers collisionLayers;
	ShapeRenderer sr;
	
	public GameWorld(String mapFile) {
		map = new TmxMapLoader().load(mapFile);
		mapRenderer = new IsometricTiledMapRenderer(map);
		world = new World(new Vector2(0, 0), true);
		this.collisionLayers = (MapLayers) getTiledMap().getLayers();
		actors = new ArrayList<Actor>();
		for (int i=0;i<100;i++) {
			Npc npc = new Npc(new Texture("male_walk.png"), Util.randomRange(0, 50), Util.randomRange(0, 50), this, i, 0.5f);
			npc.startRandomWalk(5);
			actors.add(npc);
		}
		sr = new ShapeRenderer();
	}
	
	public void addPlayer(Player p) {
		actors.add(p);
	}
	
	public void Update(Player player, SpriteBatch batch) {
		mapRenderer.setView(Game.getCamera());
		mapRenderer.render(backgroundLayers);
		
		batch.setProjectionMatrix(Game.getCamera().combined);
		
		batch.begin();
		player.draw(batch);
		for(Iterator<Actor> i = actors.iterator(); i.hasNext(); ) {
			try {
				Npc n = (Npc) i.next();
				n.Draw(batch);
			} catch(Exception e) {
				
			}
			
		}
		batch.end();
		
		for(Iterator<Actor> i = actors.iterator(); i.hasNext(); ) {
			try {
				Actor n = i.next();
				sr.setProjectionMatrix(Game.getCamera().combined);
				sr.begin(ShapeType.Line);
				sr.setColor(new Color(0,0,1,0));
				sr.rect(n.getX(), n.getY(), n.getWidth(), n.getHeight());
				sr.end();
			} catch(Exception e) {
				
			}
		}
		mapRenderer.render(foreground);
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
		for(Iterator<Actor> i = actors.iterator(); i.hasNext(); ) {
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
