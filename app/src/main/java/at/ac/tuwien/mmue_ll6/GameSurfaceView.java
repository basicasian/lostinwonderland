package at.ac.tuwien.mmue_ll6;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, SoundPool.OnLoadCompleteListener, MediaPlayer.OnCompletionListener {

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

    // sound
    private MediaPlayer mediaPlayer;
    private SoundPool soundPool;
    private int jumpSoundID;

    private final GameInit gameInit;

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
        this.level = 1; // TODO: setlevel() is called after this !!
        gameInit = new GameInit(context, this.level);

        // initialize sounds
        loadSounds(context);
    }

    public void setLevel(int level) {
        Log.d(TAG, "set level: " + level);
        this.level = level;
    }

    /**
     * load the media player and initializing them with sources
     * @param context to get the sound
     */
    private void loadSounds(Context context) {
        //Init media player with a song. Create audio pool
        mediaPlayer = MediaPlayer.create(context, R.raw.bgmusic);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        createSoundPool();
        // load sound file from resource and returns it as id that can be played by the sound pool
        jumpSoundID = soundPool.load(context, R.raw.jumpsound, 1);
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
        mediaPlayer.release();
        soundPool.release();
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
            if (Objects.requireNonNull(gameInit.staticObjectsFixed.get("buttonUp")).getRectTarget().contains(touchX, touchY)) {
                // jump motion is not handled here, but in longTouchEvent() for smoother movement

                playJumpSound();
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
            if (Objects.requireNonNull(gameInit.staticObjectsVariable.get("pauseButton")).getRectTarget().contains(touchX, touchY)) {
                Log.d(TAG, "onTouchEvent: pause");
                if (gameLoop.isRunning()) {
                    mediaPlayer.pause();
                    endGame();
                } else {
                    mediaPlayer.start();
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
        if (checkButton("right") && gameInit.player.getX() < (gameInit.displayWidth / 2)) {
            gameInit.player.move(+300 * this.deltaTime, 0); // velocity * dt
        }

        // left button
        if (checkButton("left") && gameInit.player.getX() > gameInit.displayWidth * 0.1) {
            gameInit.player.move(-300 * this.deltaTime, 0);
        }
        // up button
        if (isJumping && jumpTimer < 4) {
            // jumpCounter controls the max time of jumping, so the character cant jump indefinitely
            jumpTimer++;
            if (isGoingRight) {
                gameInit.player.move(200 * deltaTime,-2000 * this.deltaTime);
            } else {
                gameInit.player.move(-200 * deltaTime,-2000 * this.deltaTime);
            }
        }
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
                if (Objects.requireNonNull(gameInit.staticObjectsFixed.get("buttonRight")).getRectTarget().contains(touchX, touchY)) {
                    isGoingRight = true;
                    result = true;
                }
                break;
            case "left":
                if (Objects.requireNonNull(gameInit.staticObjectsFixed.get("buttonLeft")).getRectTarget().contains(touchX, touchY)) {
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
        for (DynamicObject p: gameInit.platformObjects) {
            if (Rect.intersects(gameInit.player.getRectTarget(), p.getRectTarget())) {
                result = true;
            }
        }
        return result;
    }

    /**
     * updates the game logic
     * @param deltaTime the delta time needed for frame independence
     */
    public void update(double deltaTime) {
        this.deltaTime = deltaTime;
        currentTime += deltaTime;
        currentTime = ((double)((int)(currentTime *100.0))) / 100.0; //only two decimals

        // lose condition
        // if the player touches the enemy or player falls from platforms
        if ((Rect.intersects(gameInit.player.getRectTarget(), gameInit.enemy.getRectTarget())) || (gameInit.player.getRectTarget().top > gameInit.displayHeight)) {
            Log.d(TAG, "update: game lost");

            if (gameInit.player.getNumberOfLives() != 0) {
                gameInit.staticObjectsFixed.remove("heart" + gameInit.player.getNumberOfLives());
                gameInit.player.reduceLive();
                gameInit.player.setToStart(700, 300);
            } else {
                isGameOver = true;
                // don't call endGame() here! only if the you go back to the main screen
            }
        }

        // win condition
        // if player touches the goal
        if (Rect.intersects(gameInit.player.getRectTarget(), gameInit.goal.getRectTarget())) {
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
                gameInit.player.move(+ 200 * this.deltaTime,+300 * this.deltaTime);
            } else {
                gameInit.player.move(- 200 * this.deltaTime,+300 * this.deltaTime);
            }
        }
        for (SpriteObject s: gameInit.spritesObjects) {
            s.update(System.currentTimeMillis());
        }

        // move scene to the right
        if (gameInit.player.getX() >= (gameInit.displayWidth / 2)
                && !(checkButton("left"))) {
            for (DynamicObject d: gameInit.dynamicObjects.values()) {
                d.move(-200 * this.deltaTime, 0);
            }
            for (DynamicObject p: gameInit.platformObjects) {
                p.move(-200 * this.deltaTime, 0);
            }
            for (SpriteObject s: gameInit.spritesObjects) {
                s.move(-200 * this.deltaTime, 0);
            }
        }

        // move scene to the left
        if (gameInit.player.getX() <= 250) {
            for (DynamicObject d: gameInit.dynamicObjects.values()) {
                d.move(+200 * this.deltaTime, 0);
            }
            for (DynamicObject p: gameInit.platformObjects) {
                p.move(+200 * this.deltaTime, 0);
            }
            for (SpriteObject s: gameInit.spritesObjects) {
                s.move(+200 * this.deltaTime, 0);
            }
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
            gameInit.bg.draw(canvas); // has to drawn first, because it's in the back

            // draw all platforms first
            for (DynamicObject p: gameInit.platformObjects) {
                p.draw(canvas);
            }
            // then all other dynamic objects
            for (DynamicObject d: gameInit.dynamicObjects.values()) {
                d.draw(canvas);
            }
            // then all sprites
            for (SpriteObject s: gameInit.spritesObjects) {
                s.draw(canvas);
            }
            // and static objects (such as buttons) on top
            for (StaticObject s: gameInit.staticObjectsFixed.values()) {
                s.draw(canvas);
            }

            // font
            Paint.FontMetrics fm = gameInit.textPaint.getFontMetrics();
            float height = fm.descent - fm.ascent;

            canvas.drawText("Time: " + currentTime, gameInit.displayWidth* 0.5f, gameInit.padding + height, gameInit.textPaint);

            // draw pause image when the game is paused
            if (gameLoop.isRunning()){
                Objects.requireNonNull(gameInit.staticObjectsVariable.get("pauseButton")).draw(canvas);
            } else {
                gameInit.overlay.draw(canvas);
                Objects.requireNonNull(gameInit.staticObjectsVariable.get("playButton")).draw(canvas);
                Objects.requireNonNull(gameInit.staticObjectsVariable.get("gamePauseImage")).draw(canvas);
            }

            // draw game win image when the game is won
            if (isGameWin){
                gameInit.overlay.draw(canvas);
                Objects.requireNonNull(gameInit.staticObjectsVariable.get("gameWinImage")).draw(canvas);
                gameLoop.setRunning(false);
            }
            // draw game over image when the game is over
            if (isGameOver){
                gameInit.overlay.draw(canvas);
                Objects.requireNonNull(gameInit.staticObjectsVariable.get("gameOverImage")).draw(canvas);
                gameLoop.setRunning(false);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private SoundPool createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        return new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(5)
                .build();
    }

    private void createSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = createNewSoundPool();
        } else {
            soundPool = createLegacySoundPool();
        }
    }

    private SoundPool createLegacySoundPool() {
        return new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //callback when media player has finished playing
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        //callback when sound pool has finished loading
    }

    public void playJumpSound() {
        Log.d(TAG, "playJumpSound: jump");
        soundPool.play(jumpSoundID, 0.4f, 0.4f, 1, 0, 1.0f);
    }

    /**
     * save score to the database
     */
    public static void saveScore(Context context, Score score) {
        ScoreRoomDatabase.getInstance(context).scoreDao().insert(score);
    }

}
