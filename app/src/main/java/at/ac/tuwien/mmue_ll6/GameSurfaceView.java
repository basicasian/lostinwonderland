package at.ac.tuwien.mmue_ll6;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import at.ac.tuwien.mmue_ll6.assets.Background;
import at.ac.tuwien.mmue_ll6.assets.Flummi;
import at.ac.tuwien.mmue_ll6.assets.Sprite;
import at.ac.tuwien.mmue_ll6.assets.StaticObject;

/**
 * The game view for loading assets and starting and ending the game
 * @author Renate Zhang
 */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private GameLoop gameLoop;
    private Thread gameMainThread;

    // objects
    private Flummi flummi;
    private StaticObject enemy;
    private StaticObject buttonLeft;
    private StaticObject buttonRight;
    private StaticObject buttonUp;
    private Sprite fire;
    private Background bg1;
    private Background bg2;

    // information about display
    int displayHeight;
    int displayWidth;
    int barHeight;
    int offset;

    /**
     * constructor for the class GameSurfaceView
     */
    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // callback to add events
        getHolder().addCallback(this);
        // so events can be handled
        setFocusable(true);

        // initialize resources
        loadAssets(context);
    }

    /**
     * create a new game loop and game thread, and starts it
     */
    private void startGame(SurfaceHolder holder) {
        gameLoop = new GameLoop(holder, this);
        gameMainThread = new Thread(gameLoop);
        gameMainThread.start();
    }

    /**
     * ends the game and joins the game thread
     */
    private void endGame() {
        gameLoop.setRunning(false);
        try {
            gameMainThread.join();
        } catch (InterruptedException e) {
            Log.e("Error", e.getMessage());
        }
    }

    /**
     * loading the assets (character, background, etc) and initializing them with x and y coordinates
     * also getting the display sizes for the background
     * @param context to get the bitmap
     */
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
        enemy = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy), 700, displayHeight/2);
        fire = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.fire), 4, 100, displayHeight/2);

        offset = 270;
        buttonLeft = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowleft), 150, displayHeight - offset);
        buttonRight= new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowright), displayWidth - offset, displayHeight - offset);
        buttonUp= new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowup), 400 , displayHeight - offset);

        bg1 = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background), displayWidth, displayHeight, barHeight, true);
        bg2 = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background), displayWidth, displayHeight, barHeight, false);
    }

    /**
     * surfaceView has been created, create game loop and start
     * @param surfaceHolder needed for the game loop
     */
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        startGame(surfaceHolder);
    }

    /**
     * SurfaceView has been changed (size, format,…)
     */
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder,  int format, int width, int height) {
    }

    /**
     * SurfaceView has been hidden
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        endGame();
    }

    /**
     * When the screen is touched, trigger movement of Flummi
     * @param e the input motion event
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // a touch-event has been triggered

        if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE) {
            int x = (int) e.getX();
            int y = (int) e.getY();

            if (x >= 150 && x < (150 + buttonLeft.getBitmap().getWidth())
                    && y >= displayHeight - offset && y < (displayHeight - offset + buttonLeft.getBitmap().getHeight())) {
                // if this is true, you've started your click inside your bitmap
                flummi.moveX(-1);
            }
            if (x >=displayWidth - offset && x < (displayWidth - offset + buttonLeft.getBitmap().getWidth())
                    && y >= displayHeight - offset && y < (displayHeight - offset + buttonLeft.getBitmap().getHeight())) {
                flummi.moveX(+1);
            }
            if (x >= 400 && x < (400 + buttonLeft.getBitmap().getWidth())
                    && y >= displayHeight - offset && y < (displayHeight - offset + buttonLeft.getBitmap().getHeight())) {
                // if this is true, you've started your click inside your bitmap
                flummi.jump(-1);
            }

            if (Rect.intersects(flummi.getRectTarget(), enemy.getRectTarget())) {
                collisionEvent();
            }
        }
        return true;
    }

    /**
     * if flummi collides with the enemy, create a lose message and end the game
     */
     private void collisionEvent(){
        Toast.makeText(getContext(), "Lose Game!", Toast.LENGTH_LONG).show();
        endGame();
    }

    /**
     * updates the sprite animation
     */
    public void update() {
        fire.update(System.currentTimeMillis());
    }

    /**
     * draw the objects created in loadAssets() on the canvas
     * @param canvas which is drawn on
     */
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
            buttonUp.draw(canvas);
        }
    }
}
