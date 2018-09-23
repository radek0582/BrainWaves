package com.radek.tutorial;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;

import static com.radek.tutorial.MainThread.canvas;


/**
 * Created by Radek on 2018-01-07.
 */

public class Screen extends GameElement{
    private Bitmap spritesheet;
    private Animation animation = new Animation();
    private long startTime;
    private int direction;

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public Bitmap getSpritesheet() {
        return spritesheet;
    }

    // Background Screen
    public Screen (Bitmap res, int xPos, int yPos, int w, int h, int numFrames){
        super.x = xPos;
        super.y = yPos;
        height = h;
        width = w;
        Bitmap [] image = new Bitmap[numFrames];
        spritesheet = res;

        for (int i = 0; i<image.length; i++){
            image[i] = Bitmap.createBitmap(spritesheet, i*width, 0, width, height);
            //image[i] = Bitmap.createScaledBitmap(spritesheet, width*3, height*3, true);
        }

        animation.setFrames(image);
        animation.setDelay(10);
    }

    // Moving objects
    public Screen (Bitmap res, int xPos, int yPos, int w, int h, int direction, int numFrames){
        super.x = xPos;
        super.y = yPos;
        this.direction = direction;
        height = h;
        width = w;
        Bitmap [] image = new Bitmap[numFrames];

        spritesheet = res;

        for (int i = 0; i<image.length; i++){
            image[i] = Bitmap.createBitmap(spritesheet, i*width, 0, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(50);
    }

    public void incrementY(float y){
        super.y += y;
    }

    public void incrementX(float x){
        super.x += x;
    }

    public void decreaseY(float y){
        super.y -= y;
    }

    public void decreaseX(float x){
        super.x -= x;
    }

    public void update (){
        animation.update();
    }

    public void draw (Canvas canvas){

        if (canvas != null)
            canvas.drawBitmap(animation.getImage(), x, y, null);
    }
}
