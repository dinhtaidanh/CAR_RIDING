package net.simplifiedcoding.carriding;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Created by Danh on 3/27/2017.
 */
public class Player {
    private Bitmap bitmap;
    private int x;
    private int y;
    private int speed = 0;
    private boolean boosting;
    private final int GRAVITY = -25;
    private int maxX;
    private int minX;
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 50;

    private Rect detectCollision;

    public Player(Context context, int screenX, int screenY) {
        x = 0;
        y = 1500;
        speed = 1;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.car_player);
        maxX = screenX - bitmap.getWidth();
        minX = 0;
        boosting = false;

        //initializing rect object
        detectCollision =  new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

    public void setBoosting() {
        boosting = true;
    }

    public void stopBoosting() {
        boosting = false;
    }

    public void update() {
        if (boosting) {
            speed += 30;
        } else {
            speed -= 5;
        }

        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }

        if (speed < MIN_SPEED) {
            speed = MIN_SPEED;
        }

        x -= speed + GRAVITY;
        if (x < minX) {
            x = minX;
        }
        if (x > maxX) {
            x = maxX;
        }

        //adding top, left, bottom and right to the rect object
        detectCollision.left = x;
        detectCollision.top = y;
        detectCollision.right = x + bitmap.getWidth();
        detectCollision.bottom = y + bitmap.getHeight();

    }

    //one more getter for getting the rect object
    public Rect getDetectCollision() {
        return detectCollision;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
