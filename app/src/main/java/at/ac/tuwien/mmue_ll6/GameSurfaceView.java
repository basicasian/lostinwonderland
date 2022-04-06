package at.ac.tuwien.mmue_ll6;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import at.ac.tuwien.mmue_ll6.assets.Background;
import at.ac.tuwien.mmue_ll6.assets.Flummi;
import at.ac.tuwien.mmue_ll6.assets.Sprite;
import at.ac.tuwien.mmue_ll6.assets.StaticCharacter;


public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private GameLoop gameLoop;
    private Thread gameMainThread;

    private Paint paint;

    private Flummi flummi;
    private StaticCharacter enemy;
    private StaticCharacter buttonLeft;
    private StaticCharacter buttonRight;
    private Sprite fire;
    private Background bg1;
    private Background bg2;

    int displayHeight;
    int displayWidth;
    int barHeight;
    int offset;

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // callback to add events
        getHolder().addCallback(this);
        // so events can be handled
        setFocusable(true);

        // initialize resources
        loadAssets(context);

    }

    private void startGame(SurfaceHolder holder) {

        gameLoop = new GameLoop(holder, this);
        gameMainThread = new Thread(gameLoop);
        gameMainThread.start();
    }

    private void endGame() {
        gameLoop.setRunning(false);
        try {
            gameMainThread.join();
        } catch (InterruptedException e) {
            Log.e("Error", e.getMessage());
        }
    }

    private void loadAssets(Context context) {
        // get the size of the screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        displayWidth = displayMetrics.widthPixels;
        displayHeight = displayMetrics.heightPixels;

        // because we removed the notification bar, we have to add the height manually
        barHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            barHeight = getResources().getDimensionPixelSize(resourceId);
        }

        // Initialize the assets
        flummi = new Flummi(BitmapFactory.decodeResource(context.getResources(), R.drawable.flummi), 1000, displayHeight/2);
        enemy = new StaticCharacter(BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy), 700, displayHeight/2);
        fire = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.fire), 4, 100, displayHeight/2);

        offset = 270;
        buttonLeft = new StaticCharacter(BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowleft), 150, displayHeight - offset);
        buttonRight= new StaticCharacter(BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowright), displayWidth - offset, displayHeight - offset);

        bg1 = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background), displayWidth, displayHeight, barHeight, true);
        bg2 = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background), displayWidth, displayHeight, barHeight, false);

        paint = new Paint();
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // SurfaceView has been created
        // e.g. create game loop and start
        startGame(surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder,  int format, int width, int height) {
        // SurfaceView has been changed (size, format,…)
        // e.g. end game loop cleanly

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        // SurfaceView has been hidden
        endGame();
    }



    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // a touch-event has been triggered
        Log.d("test", "onTouchEvent: true");
        Log.d("test", String.valueOf(e));

        if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE) {

            Log.d("test", "onTouchEvent: true 2");
            int x = (int) e.getX();
            int y = (int) e.getY();

            if (x >= 150 && x < (150 + buttonLeft.getBitmap().getWidth())
                    && y >= displayHeight - offset && y < (displayHeight - offset + buttonLeft.getBitmap().getHeight())) {
                // if this is true, you've started your click inside your bitmap
                flummi.move(-1);
            }
            if (x >=displayWidth - offset && x < (displayWidth - offset + buttonLeft.getBitmap().getWidth())
                    && y >= displayHeight - offset && y < (displayHeight - offset + buttonLeft.getBitmap().getHeight())) {
                flummi.move(+1);
            }

            if (Rect.intersects(flummi.getRectTarget(), enemy.getRectTarget())) {
                collisionEvent();
            }
        }
        return true;

    }

     private void collisionEvent(){
        Toast.makeText(getContext(), "Lose Game!", Toast.LENGTH_LONG).show();
        endGame();
    }


    public void update() {
        fire.update(System.currentTimeMillis());
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (canvas != null) {
            bg2.draw(canvas);
            bg1.draw(canvas);

            flummi.draw(canvas);
            enemy.draw(canvas);
            fire.draw(canvas);

            buttonLeft.draw(canvas);
            buttonRight.draw(canvas);

        }
    }
}
