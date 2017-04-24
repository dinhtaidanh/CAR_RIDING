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
    private Enemy enemies1;
    private Enemy2 enemies2;
    double count = 50;
    //defining a boom object to display blast
    private Boom boom;
    //a screenX holder
    int screenX;
    int screenY;
    //to count the number of Misses
    int countMisses;
    //indicator that the enemy has just entered the game screen
    boolean flag ;
    //an indicator if the game is Over
    private boolean isGameOver ;
    //the score holder
    int score;
    //the high Scores Holder
    int highScore[] = new int[4];
    //Shared Prefernces to store the High Scores
    SharedPreferences sharedPreferences;
    //the mediaplayer objects to configure the background music
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
        //single enemy initialization
        enemies1 = new Enemy(context, screenX, screenY);
        enemies2 = new Enemy2(context,screenX,screenY);
        //initializing boom object
        boom = new Boom(context);
        this.screenX = screenX;
        this.screenY = screenY;
        isGameOver = false;
        //setting the score to 0 initially
        score = 0;
        sharedPreferences = context.getSharedPreferences("SHAR_PREF_NAME",Context.MODE_PRIVATE);
        //initializing the array high scores with the previous values
        highScore[0] = sharedPreferences.getInt("score1",0);
        highScore[1] = sharedPreferences.getInt("score2",0);
        highScore[2] = sharedPreferences.getInt("score3",0);
        highScore[3] = sharedPreferences.getInt("score4",0);
        //initializing the media players for the game sounds
        //starting the game music as the game starts
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
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
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
                       stopSoundOn();
                       context.startActivity(new Intent(context,MainActivity.class));
                   }


        }
        return true;

    }

    private void update() {
        //incrementing score as time passes
        score++;
        if(score==count) {
            enemies1.IncreaseSpeed();
            enemies2.IncreaseSpeed();
            count += 50;
        }
        player.update();
        //setting boom outside the screen
        boom.setX(-screenX);
        boom.setY(-screenY);
        //setting the flag true when the enemy just enters the screen
        if(enemies1.getX()==screenX){
            flag = true;
        }
        enemies1.update();
        if(enemies2.getX()==screenX){
            flag = true;
        }
        enemies2.update();
        //if collision occurs with player
        if (Rect.intersects(player.getDetectCollision(), enemies1.getDetectCollision()) ||
                Rect.intersects(player.getDetectCollision(), enemies2.getDetectCollision())) {
            if(Rect.intersects(player.getDetectCollision(), enemies1.getDetectCollision())){//displaying boom at that location
            boom.setX(player.getX());
            boom.setY(player.getY());}
            if(Rect.intersects(player.getDetectCollision(), enemies2.getDetectCollision())){
            boom.setX(enemies2.getX());
            boom.setY(enemies2.getY());}
            //playing a sound at the collision between player and the enemy
            killedEnemy.start();
            enemies1.setY(-enemies1.getBitmap().getHeight());
            enemies2.setY(-enemies2.getBitmap().getHeight());
            playing = false;
            isGameOver = true;
            //stopping the gameon music
            gameOn.stop();
            //play the game over sound
            //Assigning the scores to the highscore integer array
            for(int i=0;i<4;i++){
                if(highScore[i]<score){
                    final int finalI = i;
                    highScore[i] = score;
                    break;
                }
            }
            //storing the scores through shared Preferences
            SharedPreferences.Editor e = sharedPreferences.edit();

            for(int i=0;i<4;i++){

                int j = i+1;
                e.putInt("score"+j,highScore[i]);
            }
            e.apply();
        }
    }
    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            paint.setColor(Color.WHITE);
            paint.setTextSize(20);
            //drawing the score on the game screen
            paint.setTextSize(30);

            canvas.drawBitmap(bmpGround,0,0,null);
            canvas.drawBitmap(background.getBitmap(),0,background.update(screenY,enemies1.getSpeed()),null);
            canvas.drawBitmap(background2.getBitmap(),0,background2.update(screenY,enemies1.getSpeed()),null);

            canvas.drawText("Score:"+score,100,50,paint);
            canvas.drawBitmap(player.getBitmap(), player.getX(), player.getY(), paint);

            canvas.drawBitmap(enemies1.getBitmap(), enemies1.getX(), enemies1.getY(), paint);
            canvas.drawBitmap(enemies2.getBitmap(), enemies2.getX(), enemies2.getY(), paint);
            //drawing boom image
            canvas.drawBitmap(boom.getBitmap(), boom.getX(), boom.getY(), paint);
            //draw game Over when the game is over
            if(isGameOver){
                //int yPos=(int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
                //canvas.drawBitmap(bmpOver,canvas.getWidth()/2,yPos,null);
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
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

}