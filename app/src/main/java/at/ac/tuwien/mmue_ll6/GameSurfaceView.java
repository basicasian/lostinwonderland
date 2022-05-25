package at.ac.tuwien.mmue_ll6;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Objects;

import at.ac.tuwien.mmue_ll6.activities.AfterGameActivity;
import at.ac.tuwien.mmue_ll6.objects.DynamicObject;
import at.ac.tuwien.mmue_ll6.objects.SpriteObject;
import at.ac.tuwien.mmue_ll6.objects.StaticObject;
import at.ac.tuwien.mmue_ll6.persistence.Score;
import at.ac.tuwien.mmue_ll6.persistence.ScoreRoomDatabase;
import at.ac.tuwien.mmue_ll6.util.Concurrency;

/**
 * The game view for loading assets and starting and ending the game
 * @author Renate Zhang
 */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    private static final String TAG = GameSurfaceView.class.getSimpleName();

    // game variables
    private GameLoop gameLoop;
    private Thread gameMainThread;
    private final Context context;
    private SurfaceHolder surfaceHolder;
    private double deltaTime;
    private int level;

    // check variables
    private boolean isJumping = false;
    private boolean isGameOver = false;
    private boolean isGameWin = false;
    private boolean isGoingRight = true;
    private int jumpTimer;
    private int jumpCounter = 0;

    // timer
    private double currentTime = 0;

    // coordinates of touch
    int touchX;
    int touchY;

    // graphics
    private final GameGraphic gameGraphic;

    // sound
    private final GameSound gameSound;


    /**
     * constructor for the class GameSurfaceView
     * @param attrs attribute set
     * @param context needed to get access to resource (bitmaps)
     */
    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        // callback to add events
        getHolder().addCallback(this);
        // so events can be handled
        setFocusable(true);

        // initialize graphics
        this.level = 2; // TODO: setlevel() is called after this !!
        gameGraphic = new GameGraphic(context, this.level);

        // initialize sounds
        gameSound = new GameSound(context);
    }

    public void setLevel(int level) {
        Log.d(TAG, "set level: " + level);
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    /**
     * surfaceView has been created, create game loop and start
     * @param surfaceHolder needed for the game loop
     */
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
        startGame(this.surfaceHolder);
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
        gameSound.mediaPlayer.release();
        gameSound.soundPool.release();
    }

    /**
     * create a new game loop and game thread, and starts it
     * @param holder surface holder needed for the game loop
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

            // up button
            if (Objects.requireNonNull(gameGraphic.staticObjectsFixed.get("buttonUp")).getRectTarget().contains(touchX, touchY)) {
                // jump motion is not handled here, but in longTouchEvent() for smoother movement

                gameSound.playJumpSound();
                isJumping = true;
                jumpTimer = 0; // how 'high' the player is jumping

                // improved jump mechanics but not quite smooth
                /*
                if (jumpCounter <= 3) {
                    playJumpSound();
                    isJumping = true;
                    jumpTimer = 0; // how 'high' the player is jumping
                    jumpCounter++; // how often he can jump in a sequence
                } else if (checkCollision()) {
                   jumpCounter = 0;
                   isJumping = false;
                }*/
            }

            // pause button
            if (Objects.requireNonNull(gameGraphic.staticObjectsVariable.get("pauseButton")).getRectTarget().contains(touchX, touchY)) {
                Log.d(TAG, "onTouchEvent: pause");
                if (gameLoop.isRunning()) {
                    gameSound.mediaPlayer.pause();
                    endGame();
                } else {
                    gameSound.mediaPlayer.start();
                    startGame(this.surfaceHolder);
                }
            }
        }

        if (e.getAction() == MotionEvent.ACTION_UP) {
            setPressed(false);
            isJumping = false;
        }

        // if the game's over, touching on the screen sends you to AfterGameActivity
        if (isGameOver || isGameWin){
            if (e.getAction()==MotionEvent.ACTION_DOWN){
                context.startActivity(new Intent(context, AfterGameActivity.class));
            }
        }
        return true;
    }

    /**
     * check if the buttons are pressed, if yes do the move according to button
     * can be pressed permanently, unlike onTouchEvent() method
     */
    private void longTouchEvent() {
        // check intersection here + gravity

        // right button
        if (checkButton("right") && gameGraphic.player.getX() < (gameGraphic.displayWidth / 2)) {
            gameGraphic.player.move(+300 * this.deltaTime, 0); // velocity * dt
        }

        // left button
        if (checkButton("left") && gameGraphic.player.getX() > gameGraphic.displayWidth * 0.1) {
            gameGraphic.player.move(-300 * this.deltaTime, 0);
        }
        // up button
        if (isJumping && jumpTimer < 4) {
            // jumpCounter controls the max time of jumping, so the character cant jump indefGraphicely
            jumpTimer++;
            if (isGoingRight) {
                gameGraphic.player.move(200 * deltaTime,-2000 * this.deltaTime);
            } else {
                gameGraphic.player.move(-200 * deltaTime,-2000 * this.deltaTime);
            }
        }
    }

    /**
     * updates the game logic
     * @param deltaTime the delta time needed for frame independence
     */
    public void update(double deltaTime) {
        this.deltaTime = deltaTime;
        currentTime += deltaTime;
        currentTime = ((double)((int)(currentTime * 100.0))) / 100.0; //only two decimals

        // lose condition
        // if the player touches the enemy or player falls from platforms
        if ((Rect.intersects(gameGraphic.player.getRectTarget(), gameGraphic.enemy.getRectTarget())) || (gameGraphic.player.getRectTarget().top > gameGraphic.displayHeight)) {
            Log.d(TAG, "update: game lost");

            if (gameGraphic.player.getNumberOfLives() != 0) {
                gameGraphic.player.setToStart(500, 500);
                gameGraphic.staticObjectsFixed.remove("heart" + gameGraphic.player.getNumberOfLives());
                gameGraphic.player.reduceLive();

            } else {
                isGameOver = true;
                // don't call endGame() here! only if the you go back to the main screen
            }
        }

        // win condition
        // if player touches the goal
        if (Rect.intersects(gameGraphic.player.getRectTarget(), gameGraphic.goal.getRectTarget())) {
            Log.d(TAG, "update: game win");
            isGameWin = true;

            // Save score
            Concurrency.executeAsync(() -> saveScore(context, new Score(currentTime, level)));
        }

        // if button is pressed, move character
        if (isPressed()) {
            longTouchEvent();
        }

        // gravity simulation
        if (!isJumping && !checkCollision()) {
            if (isGoingRight) {
                gameGraphic.player.move(+ 200 * this.deltaTime,+300 * this.deltaTime);
            } else {
                gameGraphic.player.move(- 200 * this.deltaTime,+300 * this.deltaTime);
            }
        }
        for (SpriteObject s: gameGraphic.spritesObjects) {
            s.update(System.currentTimeMillis());
        }

        // move scene to the right
        if (gameGraphic.player.getX() >= (gameGraphic.displayWidth / 2)
                && !(checkButton("left"))) {
            for (DynamicObject d: gameGraphic.dynamicObjects.values()) {
                d.move(-200 * this.deltaTime, 0);
            }
            for (DynamicObject p: gameGraphic.platformObjects) {
                p.move(-200 * this.deltaTime, 0);
            }
            for (SpriteObject s: gameGraphic.spritesObjects) {
                s.move(-200 * this.deltaTime, 0);
            }
        }

        // move scene to the left
        if (gameGraphic.player.getX() <= 250) {
            for (DynamicObject d: gameGraphic.dynamicObjects.values()) {
                d.move(+200 * this.deltaTime, 0);
            }
            for (DynamicObject p: gameGraphic.platformObjects) {
                p.move(+200 * this.deltaTime, 0);
            }
            for (SpriteObject s: gameGraphic.spritesObjects) {
                s.move(+200 * this.deltaTime, 0);
            }
        }
    }

    /**
     * draw the objects created in GameGraphic class on the canvas
     * @param canvas which is drawn on
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (canvas != null) {
            gameGraphic.bg.draw(canvas); // has to drawn first, because it's in the back

            // draw all platforms first
            for (DynamicObject p: gameGraphic.platformObjects) {
                p.draw(canvas);
            }
            // then all other dynamic objects
            for (DynamicObject d: gameGraphic.dynamicObjects.values()) {
                d.draw(canvas);
            }
            // then all sprites
            for (SpriteObject s: gameGraphic.spritesObjects) {
                s.draw(canvas);
            }
            // and static objects (such as buttons) on top
            for (StaticObject s: gameGraphic.staticObjectsFixed.values()) {
                s.draw(canvas);
            }

            // font height
            Paint.FontMetrics fm = gameGraphic.textPaint.getFontMetrics();
            float height = fm.descent - fm.ascent;

            canvas.drawText("Time: " + currentTime, gameGraphic.displayWidth* 0.5f, gameGraphic.padding + height, gameGraphic.textPaint);

            // draw pause image when the game is paused
            if (gameLoop.isRunning()){
                Objects.requireNonNull(gameGraphic.staticObjectsVariable.get("pauseButton")).draw(canvas);
            } else {
                gameGraphic.overlay.draw(canvas);
                Objects.requireNonNull(gameGraphic.staticObjectsVariable.get("playButton")).draw(canvas);
                Objects.requireNonNull(gameGraphic.staticObjectsVariable.get("gamePauseImage")).draw(canvas);
            }

            // draw game win image when the game is won
            if (isGameWin){
                gameGraphic.overlay.draw(canvas);
                Objects.requireNonNull(gameGraphic.staticObjectsVariable.get("gameWinImage")).draw(canvas);
                gameLoop.setRunning(false);
            }
            // draw game over image when the game is over
            if (isGameOver){
                gameGraphic.overlay.draw(canvas);
                Objects.requireNonNull(gameGraphic.staticObjectsVariable.get("gameOverImage")).draw(canvas);
                gameLoop.setRunning(false);
            }
        }
    }

    /**
     * save score to the database
     */
    public static void saveScore(Context context, Score score) {
        ScoreRoomDatabase.getInstance(context).scoreDao().insert(score);
    }


    /**
     * help method to check which button is pressed
     * @param button the specified direction
     * @return boolean if input button is pressed
     */
    public boolean checkButton(String button) {
        boolean result = false;
        switch (button) {
            case "right":
                if (Objects.requireNonNull(gameGraphic.staticObjectsFixed.get("buttonRight")).getRectTarget().contains(touchX, touchY)) {
                    isGoingRight = true;
                    result = true;
                }
                break;
            case "left":
                if (Objects.requireNonNull(gameGraphic.staticObjectsFixed.get("buttonLeft")).getRectTarget().contains(touchX, touchY)) {
                    isGoingRight = false;
                    result = true;
                }
                break;
        }
        return result;
    }

    /**
     * help method to check if the player is colliding against platforms
     * @return true if character is colliding against platform
     */
    public boolean checkCollision() {
        boolean result = false;
        for (DynamicObject p: gameGraphic.platformObjects) {
            if (Rect.intersects(gameGraphic.player.getRectTarget(), p.getRectTarget())) {
                result = true;
            }
        }
        return result;
    }
}
