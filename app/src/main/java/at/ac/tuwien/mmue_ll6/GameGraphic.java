package at.ac.tuwien.mmue_ll6;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import at.ac.tuwien.mmue_ll6.objects.DynamicObject;
import at.ac.tuwien.mmue_ll6.objects.SpriteObject;
import at.ac.tuwien.mmue_ll6.objects.StaticObject;
import at.ac.tuwien.mmue_ll6.persistence.Score;
import at.ac.tuwien.mmue_ll6.persistence.ScoreRoomDatabase;

/**
 * Help class for GameSurfaceView to initialize graphic assets
 * @author Michelle Lau
 */
public class GameGraphic {

    private static final String TAG = GameSurfaceView.class.getSimpleName();

    // general
    private final Context context;
    private final int level;

    // information about display
    protected int displayHeight;
    protected int displayWidth;
    protected int padding;
    protected int actionBarHeight = 56;

    // paint for timer
    protected Paint textPaint = new Paint();

    // assets
    protected StaticObject bg;
    protected StaticObject overlay;

    // objects
    protected ArrayList<DynamicObject> platformObjects = new ArrayList<>();
    protected ArrayList<SpriteObject> spritesObjects = new ArrayList<>();
    protected HashMap<String, StaticObject> staticObjectsFixed = new HashMap<>();
    protected HashMap<String, StaticObject> staticObjectsVariable = new HashMap<>();
    protected HashMap<String, DynamicObject> dynamicObjects = new HashMap<>();
    protected DynamicObject player;
    protected DynamicObject enemy;
    protected DynamicObject goal;

    /**
     * load the assets (character, background, etc) and initializing them with x and y coordinates
     * also getting the display sizes for the background
     */
    GameGraphic(Context context, int level) {
        Log.d(TAG, "use level: " + level);

        this.context = context;
        this.level = level;

        // general
        setDisplaySize();
        setTextPaint(); // text for high score

        // objects
        setStaticObjectsFixed();
        setStaticObjectsVariable();
        setSpriteObjects();
        setDynamicObjects();
        setPlatforms();
    }

    private void setDisplaySize() {
        // get the size of the screen
        WindowManager wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        // set size variables
        // coordinate system starts from top left! (in landscape mode)
        // but elements are initialized from bottom left
        this.displayWidth = (size.x - actionBarHeight);
        this.displayHeight = size.y;
        this.padding = (int) (displayWidth * 0.02f);
    }

    /**
     * set text paint
     */
    public void setTextPaint() {
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(60);
        textPaint.setTypeface(Typeface.create("Monospace",Typeface.NORMAL));
    }

    /**
     * generate platforms based on level
     */
    public void setPlatforms() {

        if (this.level == 1) {
            DynamicObject platform1 = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform2), 100, displayHeight - 150);
            DynamicObject platform2 = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform2), 1100, displayHeight - 150);
            DynamicObject platform3 = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform2), 2000, displayHeight - 300);
            DynamicObject platform4 = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform2), 3200, displayHeight - 150);
            DynamicObject platform5 = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform2), 3900, displayHeight - 400);
            DynamicObject platform6 = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform2), 5000, displayHeight - 300);

            // the order of array is the order of draw calls!
            platformObjects = new ArrayList<>(Arrays.asList(platform1, platform2, platform3, platform4, platform5, platform6));
        }

        if (this.level == 2) {
            DynamicObject platform1 = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform2), 200, displayHeight - 200);
            DynamicObject platform2 = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform2), 1100, displayHeight - 100);
            DynamicObject platform3 = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform2), 2000, displayHeight - 300);
            DynamicObject platform4 = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform2), 3000, displayHeight - 400);
            DynamicObject platform5 = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform2), 3800, displayHeight - 100);
            DynamicObject platform6 = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform2), 5000, displayHeight - 250);
            DynamicObject platform7 = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.platform2), 5900, displayHeight - 300);

            // the order of array is the order of draw calls!
            platformObjects = new ArrayList<>(Arrays.asList(platform1, platform2, platform3, platform4, platform5, platform6, platform7));
        }
    }

    public void setStaticObjectsFixed() {
        // buttons and hearts
        StaticObject buttonLeft = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowleft), displayWidth - 600,displayHeight - padding);
        StaticObject buttonRight = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowright), displayWidth - 300,displayHeight - padding);
        StaticObject buttonUp = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowup),  100, displayHeight - (int) padding);
        StaticObject heart1 = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.heart), 100, padding + BitmapFactory.decodeResource(context.getResources(), R.drawable.heart).getHeight());
        StaticObject heart2 = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.heart), 300, padding + BitmapFactory.decodeResource(context.getResources(), R.drawable.heart).getHeight());
        StaticObject heart3 = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.heart), 500, padding + BitmapFactory.decodeResource(context.getResources(), R.drawable.heart).getHeight());

        // background
        bg = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.background), 0, displayWidth, 0, displayHeight);
        overlay = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.overlay), 0, displayWidth, 0, displayHeight);

        staticObjectsFixed.put("buttonLeft", buttonLeft);
        staticObjectsFixed.put("buttonRight", buttonRight);
        staticObjectsFixed.put("buttonUp", buttonUp);
        staticObjectsFixed.put("heart1", heart1);
        staticObjectsFixed.put("heart2", heart2);
        staticObjectsFixed.put("heart3", heart3);
    }

    public void setStaticObjectsVariable() {
        StaticObject pauseButton = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.pause), displayWidth - 300, (int) (padding + BitmapFactory.decodeResource(context.getResources(), R.drawable.pause).getHeight()));
        StaticObject playButton = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.play), displayWidth - 300, (int) (padding + BitmapFactory.decodeResource(context.getResources(), R.drawable.play).getHeight()));
        StaticObject gameOverImage = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.gameover), displayWidth/2 - BitmapFactory.decodeResource(context.getResources(), R.drawable.gameover).getWidth()/2, displayHeight/2 + BitmapFactory.decodeResource(context.getResources(), R.drawable.gameover).getHeight()/2);
        StaticObject gameWinImage = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.youwin), displayWidth/2 - BitmapFactory.decodeResource(context.getResources(), R.drawable.youwin).getWidth()/2, displayHeight/2 + BitmapFactory.decodeResource(context.getResources(), R.drawable.youwin).getHeight()/2);
        StaticObject gamePauseImage = new StaticObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.paused), displayWidth/2 - BitmapFactory.decodeResource(context.getResources(), R.drawable.paused).getWidth()/2, displayHeight/2 + BitmapFactory.decodeResource(context.getResources(), R.drawable.paused).getHeight()/2);

        staticObjectsVariable.put("pauseButton", pauseButton);
        staticObjectsVariable.put("playButton", playButton);
        staticObjectsVariable.put("gameOverImage", gameOverImage);
        staticObjectsVariable.put("gameWinImage", gameWinImage);
        staticObjectsVariable.put("gamePauseImage", gamePauseImage);
    }

    public void setDynamicObjects() {
        // player, enemy, goal is set in extra variable because its used frequently
        player = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.player), 600, displayHeight - 300);

        if (this.level == 1) {
            enemy = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy), 300, displayHeight - 300);
            goal = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.goal), 6000, displayHeight/2);
        }
        if (this.level == 2) {
            enemy = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy), 900, displayHeight - 300);
            goal = new DynamicObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.goal), 6020, displayHeight - 100);
        }

        // put in hashmap for easier iteration
        dynamicObjects.put("player", player);
        dynamicObjects.put("enemy", enemy);
        dynamicObjects.put("goal", goal);
    }

    public void setSpriteObjects() {

        if (this.level == 1) {
            SpriteObject fire = new SpriteObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.fire), 4, 100, displayHeight - 300);
            spritesObjects.add(fire);
        }

        if (this.level == 2) {
            SpriteObject fire = new SpriteObject(BitmapFactory.decodeResource(context.getResources(), R.drawable.fire), 4, 200, displayHeight - 300);
            spritesObjects.add(fire);
        }
    }





}
