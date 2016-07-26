package com.trading.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animator {

    Animation                       walkNorthAnimation;          // #3
    Animation                       walkWestAnimation;          // #3
    Animation                       walkSouthAnimation;          // #3
    Animation                       walkEastAnimation;          // #3
    Animation						stoppedAnimation;
    Texture                         animSheet;              // #4
    TextureRegion[]                 animFrames;             // #5
   // TextureRegion                   currentFrame;           // #7
    
    public Animation addAnimation(int start, int frames) {
    	Animation newAnim;
    	TextureRegion[] tr = new TextureRegion[frames];
    	
    	int n = 0;
    	for (int i=start;i<(start+frames);i++) {
    		tr[n] = animFrames[i];
    		n++;
    	}
    	newAnim = new Animation(0.060f, tr);
    	return newAnim;
    }
    
    public Animator(int columns, int rows, String filename) {
    	animSheet = new Texture(Gdx.files.internal(filename)); // #9
        TextureRegion[][] tmp = TextureRegion.split(animSheet, animSheet.getWidth()/columns, animSheet.getHeight()/rows);              // #10
        animFrames = new TextureRegion[columns * rows];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                animFrames[index++] = tmp[i][j];
            }
        }
    }
}
