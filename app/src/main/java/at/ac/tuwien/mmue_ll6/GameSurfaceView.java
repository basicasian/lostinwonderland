package at.ac.tuwien.mmue_ll6;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
    private Context context;
    private Paint paint;
    private Paint paint2;

    // check variables
    private boolean isJumping = false;
    private boolean isGameOver = false;
    private boolean isGameWin = false;

    // objects
    private Flummi flummi;
    private StaticObject enemy;
    private StaticObject goal;
    private Rect platform1;
    private Rect platform2;
    private Sprite fire;

    // assets
    private Background bg1;
    private Background bg2;
    private StaticObject buttonLeft;
    private StaticObject buttonRight;
    private StaticObject buttonUp;
    private StaticObject gameOverImage;
    private StaticObject gameWinImage;

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
        this.context = context;

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

        Log.d(TAG, "Starting Game Thread");
        gameMainThread.start();
    }

    /**
     * ends the game and joins the game thread
     */
    private void endGame() {
        gameLoop.setRunning(false);
        try {
            Log.d(TAG, "Joining Game Thread");
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
        flummi = new Flummi(BitmapFactory.decodeResource(context.getResources(), R.drawable.flummi), 700, displayHeight - 300);
        enemy = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy), 500, displayHeight - 300);
        goal = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.goal), displayWidth - 120, displayHeight - 300);
        fire = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.fire), 4, 100, displayHeight - 300);
        platform1 = new Rect(0, displayHeight - 300, displayWidth - 500, displayHeight);
        platform2 = new Rect(displayWidth - 300, displayHeight - 300, displayWidth + barHeight, displayHeight);

        buttonLeft = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowleft), displayWidth - 500, displayHeight - 50);
        buttonRight= new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowright), displayWidth - 250, displayHeight - 50);
        buttonUp= new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowup), barHeight + 50, displayHeight - 50);
        gameOverImage = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.gameover), displayWidth/5, 2*displayHeight/3);
        gameWinImage = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.youwin), displayWidth/5, displayHeight/2);

        bg1 = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background), barHeight, displayWidth+barHeight, displayHeight);
        bg2 = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background), displayWidth+barHeight, displayWidth+barHeight+displayWidth, displayHeight);

        paint = new Paint();
        paint.setARGB(255,93, 204, 88);
        paint2 = new Paint();
        paint2.setARGB(255,255, 2255, 255);
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
     * SurfaceView has been hidden, end the game loop
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        endGame();
    }

    /**
     * a touch-event has been triggered, set pressed state to true or false
     * can be pressed only once, unlike longTouchEvent() method
     * @param e the input motion event
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        Log.d(TAG, "onTouchEvent: " + e);

        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            setPressed(true); // needed for LongTouchEvent()

            touchX = (int) e.getX();
            touchY = (int) e.getY();

            // jump motion should be only one click
            // up botton
            if (touchX >= 150 && touchX < (150 + buttonLeft.getBitmap().getWidth())
                    && touchY >= displayHeight - offset && touchY < (displayHeight - offset + buttonLeft.getBitmap().getHeight())) {
                // if this is true, you've started your click inside the up button
                isJumping = true;
                flummi.moveY(-150);
            }
        }

        if (e.getAction() == MotionEvent.ACTION_UP) {
            setPressed(false);
            isJumping = false;
        }

        // if the game's over, touching on the screen sends you to MainActivity
        if (isGameOver || isGameWin){
            if (e.getAction()==MotionEvent.ACTION_DOWN){
                context.startActivity(new Intent(context,AfterGameActivity.class));
            }
        }

        return true;
    }

    /**
     * check if the buttons are pressed, if yes move character to the left or right
     * can be pressed permanently, unlike onTouchEvent() method
     */
    private void longTouchEvent() {

        /*
        // move background if flummi moves too much to the right
        if (flummi.getX() >= (displayWidth/2)) {
            bg1.move(-4);
            bg2.move(-4);
        }

        // move background if flummi moves too much to the left
        if (flummi.getRectTarget().left <= barHeight) {
            bg1.move(4);
            bg2.move(4);
        }*/

        // right button
        if (touchX >= displayWidth - offset && touchX < (displayWidth - offset + buttonLeft.getBitmap().getWidth())
                && touchY >= displayHeight - offset && touchY < (displayHeight - offset + buttonLeft.getBitmap().getHeight())) {
            flummi.moveX(+4);
        }
        // left button
        if (touchX >= displayWidth - offset * 2 && touchX < (displayWidth - offset * 2 + buttonLeft.getBitmap().getWidth())
                && touchY >= displayHeight - offset && touchY < (displayHeight - offset + buttonLeft.getBitmap().getHeight())) {
            flummi.moveX(-4);
        }
    }

    /**
     * updates the sprite animation and checks if the screen is still pressed
     * or if lose condition is fulfilled
     */
    public void update() {
        // lose condition
        // if flummi touches the enemy or flummi falls from platforms
        if ((Rect.intersects(flummi.getRectTarget(), enemy.getRectTarget())) || (flummi.getRectTarget().top > displayHeight)) {
            Log.d(TAG, "update: game over");
            isGameOver = true;
            // don't call endGame() here! only if the you go back to the main screen
        }

        // win condition
        // if flummi touches the goal
        if (Rect.intersects(flummi.getRectTarget(), goal.getRectTarget())) {
            Log.d(TAG, "update: game won");
            isGameWin = true;
        }

        fire.update(System.currentTimeMillis());

        // gravity simulation
        if (!Rect.intersects(flummi.getRectTarget(), platform1) && !isJumping && !Rect.intersects(flummi.getRectTarget(), platform2)) {
            flummi.moveY(+10);
        }
        if (isPressed()) {
            longTouchEvent();
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

            bg1.draw(canvas);
            bg2.draw(canvas);
            canvas.drawRect(platform1, paint);
            canvas.drawRect(platform2, paint);

            flummi.draw(canvas);
            enemy.draw(canvas);
            goal.draw(canvas);
            fire.draw(canvas);

            buttonLeft.draw(canvas);
            buttonRight.draw(canvas);
            buttonUp.draw(canvas);

            // draw game won when the game is won
            if (isGameWin){
                gameWinImage.draw(canvas);
            }

            // draw game over when the game is over
            if (isGameOver){
                gameOverImage.draw(canvas);
                gameLoop.setRunning(false);
            }
        }
    }
}
