package com.trading.game;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;

public class Util {
	static Random rand = new Random();
	
	public static int randomRange(int min, int max) {
	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	public static Vector2 twoDToIso(Vector2 pt) {
		  Vector2 tempPt = new Vector2(0,0);
		  tempPt.x = pt.x + 0.5f - pt.y;
		  tempPt.y = pt.x + pt.y;
		  return(tempPt);
	}
	
	public static Vector2 isoToTwoD(Vector2 pt) {
		Vector2 tempPt = new Vector2(0,0);
		tempPt.x = (pt.x + ((pt.y-pt.x)/2)) * 64;
		tempPt.y = (pt.y - pt.x) * 16;
		return(tempPt);
	}
	
	public static Vector2 getTileCoordinates(Vector2 pt, float tileHeight) {
		  Vector2 tempPt = new Vector2(0, 0);
		  tempPt.x = pt.x / tileHeight;
		  tempPt.y = pt.y / tileHeight / 2;
		  return(tempPt);
	}
}
