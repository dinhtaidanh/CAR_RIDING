package net.simplifiedcoding.carriding;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;

import static android.content.Context.AUDIO_SERVICE;


public class GameView extends SurfaceView implements Runnable {
    static MediaPlayer gameOn, killedEnemy;

    Bitmap bmpOver, bmpGround;
    Background background,background2,background3;

    volatile boolean playing;
    private Thread gameThread = null;
    private Player player;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private Enemy enemy1;
    private Enemy2 enemy2;
    private Enemy3 enemy3;
    private Enemy4 enemy4;
    double count = 50;
    private Boom boom;
    int screenX;
    int screenY;
    boolean flag ;//xác định khi nào enemy vào game screen
    public static boolean isGameOver ;
    int score;
    int highScore[] = new int[5];
    SharedPreferences sharedPreferences;
    Context context;
    public GameView(Context context, int screenX, int screenY) {
        super(context);
        bmpOver = BitmapFactory.decodeResource(getResources(),R.drawable.game_over);
        bmpGround = BitmapFactory.decodeResource(getResources(),R.drawable.race_ground);
        bmpGround = getResizedBitmap(bmpGround,screenX,screenY);
        gameOn = MediaPlayer.create(context,R.raw.gameon);
        gameOn.setLooping(true);
        killedEnemy = MediaPlayer.create(context,R.raw.killedenemy);
        background = new Background(context,0);
        background.setBitmap(getResizedBitmap(background.getBitmap(),screenX,screenY));
        background2 = new Background(context,-screenY);
        background2.setBitmap(getResizedBitmap(background2.getBitmap(),screenX,screenY));
        player = new Player(context, screenX, screenY);
        surfaceHolder = getHolder();
        paint = new Paint();
        enemy1 = new Enemy(context, screenX, screenY);
        enemy2 = new Enemy2(context,screenX,screenY);
        enemy3 = new Enemy3(context,screenX,screenY);
        enemy4 = new Enemy4(context,screenX,screenY);
        boom = new Boom(context);
        this.screenX = screenX;
        this.screenY = screenY;
        isGameOver = false;
        score = 0;
        sharedPreferences = context.getSharedPreferences("SHAR_PREF_NAME",Context.MODE_PRIVATE);
        highScore[0] = sharedPreferences.getInt("score1",0);
        highScore[1] = sharedPreferences.getInt("score2",0);
        highScore[2] = sharedPreferences.getInt("score3",0);
        highScore[3] = sharedPreferences.getInt("score4",0);
        gameOn.start();
        //initializing context

        this.context = context;
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }
    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void pause() {
        playing = false;
        gameOn.pause();
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }
    public void resume() {
        if(!isGameOver) {
            gameOn.start();
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
        else {
            context.startActivity(new Intent(context,MainActivity.class));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                break;
        }
        //if the game's over, tappin on game Over screen sends you to MainActivity
        if(isGameOver){
            if(motionEvent.getAction()==MotionEvent.ACTION_DOWN )
                   {
                       context.startActivity(new Intent(context,MainActivity.class));
                   }
        }
    return true;
    }

    private void update() {
        //incrementing score as time passes
        score++;
        if(score==count) {
            enemy1.IncreaseSpeed();
            enemy2.IncreaseSpeed();
            enemy3.IncreaseSpeed();
            enemy4.IncreaseSpeed();
            count += 50;
        }
        player.update();
        //setting boom outside the screen
        boom.setX(-screenX);
        boom.setY(-screenY);
        //setting the flag true when the enemy just enters the screen
        if(enemy1.getX()==screenX){
            flag = true;
        }
        enemy1.update();
        if(enemy2.getX()==screenX){
            flag = true;
        }
        enemy2.update();
        if(enemy3.getX()==screenX){
            flag = true;
        }
        enemy3.update();
        if(enemy4.getX()==screenX){
            flag = true;
        }
        enemy4.update();

        if (Rect.intersects(player.getDetectCollision(), enemy1.getDetectCollision()) ||
                Rect.intersects(player.getDetectCollision(), enemy2.getDetectCollision()) ||
                Rect.intersects(player.getDetectCollision(), enemy3.getDetectCollision()) ||
                Rect.intersects(player.getDetectCollision(), enemy4.getDetectCollision())) {
            if(Rect.intersects(player.getDetectCollision(), enemy1.getDetectCollision())){
            boom.setX(enemy1.getX());
            boom.setY(enemy1.getY());}
            if(Rect.intersects(player.getDetectCollision(), enemy2.getDetectCollision())){
            boom.setX(enemy2.getX());
            boom.setY(enemy2.getY());}
            if(Rect.intersects(player.getDetectCollision(), enemy3.getDetectCollision())){
                boom.setX(enemy3.getX());
                boom.setY(enemy3.getY());}
            if(Rect.intersects(player.getDetectCollision(), enemy4.getDetectCollision())){
                boom.setX(enemy4.getX());
                boom.setY(enemy4.getY());}
            enemy1.setY(-enemy1.getBitmap().getHeight());
            enemy2.setY(-enemy2.getBitmap().getHeight());
            enemy3.setY(-screenY/2-2*enemy3.getBitmap().getHeight());
            enemy4.setY(-screenY/2-2*enemy4.getBitmap().getHeight());
            killedEnemy.start();
            playing = false;
            isGameOver = true;
            gameOn.stop();
            for(int i=3;i>=0;i--){
                if(score > highScore[i]){
                    highScore[i+1] = highScore[i];
                    highScore[i]=score;}
                }
            }
            SharedPreferences.Editor e = sharedPreferences.edit();
            for(int i=0;i<4;i++){
                int j = i+1;
                e.putInt("score"+j,highScore[i]);
            }
            e.apply();
    }
    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            paint.setColor(Color.WHITE);
            paint.setTextSize(20);

            paint.setTextSize(30);

            canvas.drawBitmap(bmpGround,0,0,null);
            canvas.drawBitmap(background.getBitmap(),0,background.update(screenY,enemy1.getSpeed()),null);
            canvas.drawBitmap(background2.getBitmap(),0,background2.update(screenY,enemy1.getSpeed()),null);

            canvas.drawText("Score:"+score,100,50,paint);
            canvas.drawBitmap(player.getBitmap(), player.getX(), player.getY(), paint);

            canvas.drawBitmap(enemy1.getBitmap(), enemy1.getX(), enemy1.getY(), paint);
            canvas.drawBitmap(enemy2.getBitmap(), enemy2.getX(), enemy2.getY(), paint);
            canvas.drawBitmap(enemy3.getBitmap(), enemy3.getX(), enemy3.getY(), paint);
            canvas.drawBitmap(enemy4.getBitmap(), enemy4.getX(), enemy4.getY(), paint);

            canvas.drawBitmap(boom.getBitmap(), boom.getX(), boom.getY(), paint);

            if(isGameOver){
                canvas.drawBitmap(bmpOver,(screenX  - bmpOver.getWidth()) * 0.5f,(screenY  - bmpOver.getHeight()) * 0.5f,null);
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }
    public static void stopSoundOn()
    {
            if(gameOn!= null) {
                gameOn.stop();
                gameOn = null;
            }
    }
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

}