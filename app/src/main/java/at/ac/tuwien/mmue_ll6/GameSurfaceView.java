package at.ac.tuwien.mmue_ll6;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
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

    private static final String TAG = GameSurfaceView.class.getSimpleName();

    private GameLoop gameLoop;
    private Thread gameMainThread;
    private Paint paint;

    private boolean gameLost = false;
    private boolean isJumping = false;

    // objects
    private Flummi flummi;
    private StaticObject enemy;
    private Rect platform1;
    private Sprite fire;

    // assets
    private Background bg1;
    private Background bg2;
    private StaticObject buttonLeft;
    private StaticObject buttonRight;
    private StaticObject buttonUp;

    // information about display
    int displayHeight;
    int displayWidth;
    int barHeight;
    int offset = 270;

    // coordinates of touch
    int touchX;
    int touchY;

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
        // coordinate system starts from top left! (in landscape mode)
        // but elements are initialized from bottom left
        flummi = new Flummi(BitmapFactory.decodeResource(context.getResources(), R.drawable.flummi), 1000, displayHeight - 300);
        enemy = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy), 500, displayHeight - 300);
        fire = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.fire), 4, 100, displayHeight - 300);
        // platform1 = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform), 100, 660, 0.5f);
        //platform1 = new Rect(0, 800, displayWidth + barHeight, displayHeight);
        platform1 = new Rect(0, displayHeight - 300, displayWidth - 500, displayHeight);

        buttonLeft = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowleft), displayWidth - 500, displayHeight - 50);
        buttonRight= new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowright), displayWidth - 250, displayHeight - 50);
        buttonUp= new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowup), barHeight + 50, displayHeight - 50);

        bg1 = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background), displayWidth, displayHeight, barHeight, true);
        bg2 = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background), displayWidth, displayHeight, barHeight, false);

        paint = new Paint();
        paint.setARGB(255,93, 204, 88);
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

    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // left arrow = 21
        if (keyCode == 21) {
            flummi.moveX(-5);
            return true;
        }
        // right arrow = 22
        if (keyCode == 22) {
            flummi.moveX(+5);
            return true;
        }
        // space = 62
        if (keyCode == 62) {
            flummi.jump(-5, platform1);
            return true;
        }
        return true;
    }*/

    /**
     * a touch-event has been triggered, set pressed state to true or false
     * @param e the input motion event
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        Log.d(TAG, "onTouchEvent: " + e);

        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            setPressed(true);

            touchX = (int) e.getX();
            touchY = (int) e.getY();

            // jump motion should be only one click
            // up botton
            if (touchX >= 150 && touchX < (150 + buttonLeft.getBitmap().getWidth())
                    && touchY >= displayHeight - offset && touchY < (displayHeight - offset + buttonLeft.getBitmap().getHeight())) {
                // if this is true, you've started your click inside your bitmap
                isJumping = true;
                flummi.moveY(-100);
            }
        }

        if (e.getAction() == MotionEvent.ACTION_UP) {
            setPressed(false);
            isJumping = false;
        }
        return true;
    }

    /**
     * check if the buttons are pressed, if yes move character
     * can be pressed permanently
     */
    private void touchEvent() {

        // left button
        if (touchX >=displayWidth - offset && touchX < (displayWidth - offset + buttonLeft.getBitmap().getWidth())
                && touchY >= displayHeight - offset && touchY < (displayHeight - offset + buttonLeft.getBitmap().getHeight())) {
            flummi.moveX(+4);
        }
        // right button
        if (touchX >= displayWidth - offset*2 && touchX < (displayWidth - offset*2 + buttonLeft.getBitmap().getWidth())
                && touchY >= displayHeight - offset && touchY < (displayHeight - offset + buttonLeft.getBitmap().getHeight())) {
            flummi.moveX(-4);
        }
    }

    /**
     * check if lose condition is fulfilled, if yes end game
     */
     private void checkLose(){
         // if flummi touches the enemy or
         // if flummi falls from platforms
         if ((Rect.intersects(flummi.getRectTarget(), enemy.getRectTarget())) || (flummi.getRectTarget().top > displayHeight)) {
             gameLost = true;
         }
    }

    /**
     * updates the sprite animation and checks if the screen is still pressed
     * or if lose condition is fulfilled
     */
    public void update() {
        checkLose();

        fire.update(System.currentTimeMillis());

        // gravity simulation
        if (!Rect.intersects(flummi.getRectTarget(), platform1) && !isJumping) {
            flummi.moveY(+10);
        }
        if (isPressed()) {
            touchEvent();
        }
    }

    /**
     * draw the objects created in loadAssets() on the canvas
     * @param canvas which is drawn on
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (canvas != null) {
            if (gameLost) {
                Paint paintText = new Paint();
                paintText.setARGB(255,198, 64, 110);
                paintText.setTextSize(100);
                canvas.drawText("Game Over", displayWidth/3, (displayHeight/2), paintText);
                // endGame();
            }

            bg2.draw(canvas);
            bg1.draw(canvas);

            canvas.drawRect(platform1, paint);

            flummi.draw(canvas);
            enemy.draw(canvas);
            fire.draw(canvas);

            buttonLeft.draw(canvas);
            buttonRight.draw(canvas);
            buttonUp.draw(canvas);
        }
    }
}
