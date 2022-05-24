package at.ac.tuwien.mmue_ll6;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Arrays;

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

    // objects
    private DynamicObject player;
    private DynamicObject enemy;
    private DynamicObject goal;
    private SpriteObject fire;
    private ArrayList<DynamicObject> platformObjects = new ArrayList<>();
    private ArrayList<DynamicObject> dynamicObjects = new ArrayList<>();

    // assets
    private StaticObject bg1;
    private StaticObject overlay;
    private StaticObject buttonLeft;
    private StaticObject buttonRight;
    private StaticObject buttonUp;
    private StaticObject pauseButton;
    private StaticObject playButton;
    private StaticObject gameOverImage;
    private StaticObject gameWinImage;
    private StaticObject gamePauseImage;
    private ArrayList<StaticObject> staticObjects = new ArrayList<>();

    // timer
    private final Paint textPaint = new Paint();
    private float padding;
    private double currentTime = 0;

    // information about display
    int displayHeight;
    int displayWidth;
    int actionBarHeight = 56;

    // coordinates of touch
    int touchX;
    int touchY;

    // sound
    private MediaPlayer mediaPlayer;
    private SoundPool soundPool;
    private int jumpSoundID;

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
        loadAssets(context);

        // initialize sounds
        loadSounds(context);
    }

    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * load the assets (character, background, etc) and initializing them with x and y coordinates
     * also getting the display sizes for the background
     * @param context to get the bitmap
     */
    private void loadAssets(Context context) {

        // get the size of the screen
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        this.displayWidth = size.x - this.actionBarHeight;
        this.displayHeight = size.y;
        padding = displayWidth * 0.02f;

        // Initialize the assets
        // coordinate system starts from top left! (in landscape mode)
        // but elements are initialized from bottom left

        // dynamic objects
        player = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.player), 600, displayHeight - 300);
        enemy = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy), 300, displayHeight - 300);
        goal = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.goal), 6000, displayHeight/2);
        DynamicObject platform1 = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform2), 100, displayHeight - 150);
        DynamicObject platform2 = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform2), 1100, displayHeight - 150);
        DynamicObject platform3 = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform2), 2000, displayHeight - 300);
        DynamicObject platform4 = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform2), 3200, displayHeight - 150);
        DynamicObject platform5 = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform2), 3900, displayHeight - 400);
        DynamicObject platform6 = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform2), 5000, displayHeight - 300);

        // sprites
        fire = new SpriteObject(BitmapFactory.decodeResource(getResources(), R.drawable.fire), 4, 100, displayHeight - 300);

        // static objects
        buttonLeft = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowleft), displayWidth - 600,displayHeight - (int) padding);
        buttonRight= new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowright), displayWidth - 300,displayHeight - (int) padding);
        buttonUp = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowup),  100, displayHeight - (int) padding);
        pauseButton = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.pause), displayWidth - 300, (int) (padding + BitmapFactory.decodeResource(context.getResources(), R.drawable.pause).getHeight()));
        playButton = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.play), displayWidth - 300, (int) (padding + BitmapFactory.decodeResource(context.getResources(), R.drawable.play).getHeight()));
        gameOverImage = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.gameover), displayWidth/2 - BitmapFactory.decodeResource(context.getResources(), R.drawable.gameover).getWidth()/2, displayHeight/2 + BitmapFactory.decodeResource(context.getResources(), R.drawable.gameover).getHeight()/2);
        gameWinImage = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.youwin), displayWidth/2 - BitmapFactory.decodeResource(context.getResources(), R.drawable.youwin).getWidth()/2, displayHeight/2 + BitmapFactory.decodeResource(context.getResources(), R.drawable.youwin).getHeight()/2);
        gamePauseImage = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.paused), displayWidth/2 - BitmapFactory.decodeResource(context.getResources(), R.drawable.paused).getWidth()/2, displayHeight/2 + BitmapFactory.decodeResource(context.getResources(), R.drawable.paused).getHeight()/2);

        StaticObject heart1 = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.heart), 100, (int) (padding + BitmapFactory.decodeResource(context.getResources(), R.drawable.heart).getHeight()));
        StaticObject heart2 = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.heart), 300, (int) (padding + BitmapFactory.decodeResource(context.getResources(), R.drawable.heart).getHeight()));
        StaticObject heart3 = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.heart), 500, (int) (padding + BitmapFactory.decodeResource(context.getResources(), R.drawable.heart).getHeight()));
        bg1 = new StaticObject(BitmapFactory.decodeResource(getResources(), R.drawable.background), 0, displayWidth, 0, displayHeight);
        overlay = new StaticObject(BitmapFactory.decodeResource(getResources(), R.drawable.overlay), 0, displayWidth, 0, displayHeight);

        // text for high score
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(60);
        textPaint.setTypeface(Typeface.create("Monospace",Typeface.NORMAL));

        // the order of array is the order of draw calls!
        platformObjects = new ArrayList<>(Arrays.asList(platform1, platform2, platform3, platform4, platform5, platform6));
        dynamicObjects = new ArrayList<>(Arrays.asList(player, enemy, goal));
        staticObjects = new ArrayList<>(Arrays.asList(heart3, heart2, heart1, buttonLeft, buttonRight, buttonUp));
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
            if (buttonUp.getRectTarget().contains(touchX, touchY)) {
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
            if (pauseButton.getRectTarget().contains(touchX, touchY)) {
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
        if (checkButton("right") && player.getX() < (displayWidth/2)) {
            player.move(+300 * this.deltaTime, 0); // velocity * dt
        }

        // left button
        if (checkButton("left") && player.getX() > displayWidth * 0.1) {
            player.move(-300 * this.deltaTime, 0); 
        }
        // up button
        if (isJumping && jumpTimer < 4) {
            // jumpCounter controls the max time of jumping, so the character cant jump indefinitely
            jumpTimer++;
            if (isGoingRight) {
                player.move(200 * deltaTime,-2000 * this.deltaTime); 
            } else {
                player.move(-200 * deltaTime,-2000 * this.deltaTime);
            }
        }
    }

    /**
     * help method to check which button is pressed
     * @param button the specified direction
     * @return boolean if input button is pressed
     */
    private boolean checkButton(String button) {
        boolean result = false;
        switch (button) {
            case "right":
                if (buttonRight.getRectTarget().contains(touchX, touchY)) {
                    isGoingRight = true;
                    result = true;
                }
                break;
            case "left":
                if (buttonLeft.getRectTarget().contains(touchX, touchY)) {
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
    private boolean checkCollision() {
        boolean result = false;
        for (DynamicObject p: platformObjects) {
            if (Rect.intersects(player.getRectTarget(), p.getRectTarget())) {
                result = true;
            }
        }
        return result;
    }


    public void saveScore(Score score) {
        ScoreRoomDatabase.getInstance(context).scoreDao().insert(score);
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
        if ((Rect.intersects(player.getRectTarget(), enemy.getRectTarget())) || (player.getRectTarget().top > displayHeight)) {
            Log.d(TAG, "update: game lost");

            if (player.getNumberOfLives() != 1) {
                player.reduceLive();
                staticObjects.remove(0);
                player.setToStart(700, 300);

            } else {
                isGameOver = true;
                // don't call endGame() here! only if the you go back to the main screen
            }
        }

        // win condition
        // if player touches the goal
        if (Rect.intersects(player.getRectTarget(), goal.getRectTarget())) {
            Log.d(TAG, "update: game win");
            isGameWin = true;

            // Save score
            Concurrency.executeAsync(() -> saveScore(new Score(currentTime, level)));
        }

        // if button is pressed, move character
        if (isPressed()) {
            longTouchEvent();
        }

        // gravity simulation
        if (!isJumping && !checkCollision()) {
            if (isGoingRight) {
                player.move(+ 200 * this.deltaTime,+300 * this.deltaTime);
            } else {
                player.move(- 200 * this.deltaTime,+300 * this.deltaTime);
            }
        }
        fire.update(System.currentTimeMillis());


        // move scene to the right
        if (player.getX() >= (displayWidth/2)
                && !(checkButton("left"))) {
            for (DynamicObject d: dynamicObjects) {
                d.move(-200 * this.deltaTime, 0);
            }
            for (DynamicObject p: platformObjects) {
                p.move(-200 * this.deltaTime, 0);
            }
            fire.move(-200 * this.deltaTime, 0);
        }

        // move scene to the left
        if (player.getX() <= 250) {
            for (DynamicObject d: dynamicObjects) {
                d.move(+200 * this.deltaTime, 0);
            }
            for (DynamicObject p: platformObjects) {
                p.move(+200 * this.deltaTime, 0);
            }
            fire.move(+200 * this.deltaTime, 0);
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
            bg1.draw(canvas); // has to drawn first, because it's in the back
            fire.draw(canvas); // sprite

            // draw all platforms first
            for (DynamicObject p: platformObjects) {
                p.draw(canvas);
            }
            // then all other dynamic
            for (DynamicObject d: dynamicObjects) {
                d.draw(canvas);
            }
            // and static objects (such as buttons) on top
            for (StaticObject s: staticObjects) {
                s.draw(canvas);
            }

            // font
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            float height = fm.descent - fm.ascent;

            canvas.drawText("Time: " + currentTime, displayWidth * 0.5f, padding + height, textPaint);

            // draw pause image when the game is paused
            if (gameLoop.isRunning()){
                pauseButton.draw(canvas);
            } else {
                overlay.draw(canvas);
                playButton.draw(canvas);
                gamePauseImage.draw(canvas);
            }

            // draw game win image when the game is won
            if (isGameWin){
                overlay.draw(canvas);
                gameWinImage.draw(canvas);
                gameLoop.setRunning(false);
            }
            // draw game over image when the game is over
            if (isGameOver){
                overlay.draw(canvas);
                gameOverImage.draw(canvas);
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
}
