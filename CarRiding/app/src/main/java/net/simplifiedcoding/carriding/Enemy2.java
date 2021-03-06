package net.simplifiedcoding.carriding;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import java.util.Random;

public class Enemy2 {
    private Bitmap bitmap;
    private int x;//hoanh do cua enemy
    private int y;//tung do cua enemy
    private int speed = 10;

    private int maxX;//chieu rong man hinh
    //private int minX;

    private int maxY;//chieu cao man hinh
    private int minY;
    //creating a rect object
    private Rect detectCollision;

    public Enemy2(Context context, int screenX, int screenY) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_car);
        maxX = screenX;
        maxY = screenY;
        minY = -bitmap.getHeight();
        x = maxX/2+bitmap.getWidth()/2;
        y = minY;
        detectCollision = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

    public void update() {

        y += speed;
        if (y > maxY) {
            x =maxX/2+bitmap.getWidth()/2;
            y = minY;
        }

        //Adding the top, left, bottom and right to the rect object
        detectCollision.left = x ;
        detectCollision.top = y + bitmap.getHeight()/2;
        detectCollision.right = x + bitmap.getWidth();
        detectCollision.bottom = y + bitmap.getHeight();
    }

    //adding a setter to x coordinate so that we can change it after collision
    public void setY(int y){
        this.y = y;
    }

    //one more getter for getting the rect object
    public Rect getDetectCollision() {
        return detectCollision;
    }

    //getters
    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }
    public void IncreaseSpeed() { speed=speed+1;}
}
