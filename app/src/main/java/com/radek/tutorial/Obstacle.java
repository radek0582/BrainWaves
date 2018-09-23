package com.radek.tutorial;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.shapes.RoundRectShape;

/**
 * Created by Radek on 2017-11-12.
 */

public class Obstacle implements GameObject{
    private Rect rectangle;
    private int color;
    private int direction;
    //private Rect rectangle2;
    private int startX;
    private int startY;

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public Rect getRectangle(){
        return rectangle;
    }

    public void incrementY(float y){
        rectangle.top += y;
        rectangle.bottom += y;
        //rectangle2.top += y;
        //rectangle2.bottom += y;
    }

    public void incrementX(float x){
        rectangle.left += x;
        rectangle.right += x;
        //rectangle2.top += y;
        //rectangle2.bottom += y;
    }

    public void decreaseY(float y){
        rectangle.top -= y;
        rectangle.bottom -= y;
        //rectangle2.top += y;
        //rectangle2.bottom += y;
    }

    public void decreaseX(float x){
        rectangle.left -= x;
        rectangle.right -= x;
        //rectangle2.top += y;
        //rectangle2.bottom += y;
    }

    public Obstacle (int rectHeight, int color, int startX, int startY, int size, int direction){
        this.color = color;
        this.startX = startX;
        this.startY = startY;
        //l,t,r,b
        rectangle = new Rect(startX-size, startY-size, startX+size, startY + size);

        this.direction = direction;
        //rectangle2 = new Rect(startX + playerGap, startY, Constants.SCREEN_WIDTH, startY + rectHeight);
    }

    public boolean playerCollide (RectPlayer player){
        //return Rect.intersects(rectangle, player.getRectangle()) || Rect.intersects(rectangle2, player.getRectangle());
        return false;
    }

    @Override
    public void draw(Canvas canvas) {


        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(rectangle, paint);
        //canvas.drawRect(rectangle2, paint);
    }



    @Override
    public void update() {

    }
}
