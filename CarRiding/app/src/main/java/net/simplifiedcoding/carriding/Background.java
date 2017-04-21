package net.simplifiedcoding.carriding;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Danh on 4/22/2017.
 */

public class Background {
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setY(int y) {
        Y = y;
    }

    public int getY() {
        return Y;

    }

    private int Y;
    public Background(Context context,int y) {
        bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.racetrack);
        Y=y;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int update(int screenY, int EnemySpeed) {
        Y = Y+EnemySpeed;
        if(Y>=screenY )
            Y=-screenY;

        return Y;
    }

}
