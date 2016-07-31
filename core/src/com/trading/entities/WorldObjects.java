package com.trading.entities;

import java.lang.reflect.Type;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class WorldObjects {
	
	public static List<TreePrefab> trees;
	public static List<RockPrefab> rocks;

	public WorldObjects() {
		Type listType = new TypeToken<List<TreePrefab>>() {}.getType();
		JsonObject jo = (JsonObject) new JsonParser().parse(Gdx.files.internal("Objects/Objects.json").readString());
		trees = new Gson().fromJson(jo.get("trees"), listType);
		rocks = new Gson().fromJson(jo.get("rocks"), listType);
	}
	
	public class TreePrefab {
		public int id;
		public String file;
		public String type;
		public float scale;
		public int width;
		public int height;
		public int offsetX;
		public int offsetY;
		
		public TreePrefab(int id, String file, String type, float scale, int width, int height, int offsetX, int offsetY) {
			this.id = id;
			this.file = file;
			this.type = type;
			this.scale = scale;
			this.width = width;
			this.height = height;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
		}
	}
	
	public class RockPrefab {
		public int id;
		public String file;
		public String type;
		public float scale;
		public int width;
		public int height;
		public int offsetX;
		public int offsetY;
		
		public RockPrefab(int id, String file, String type, float scale, int width, int height, int offsetX, int offsetY) {
			this.id = id;
			this.file = file;
			this.type = type;
			this.scale = scale;
			this.width = width;
			this.height = height;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
		}
	}
}


