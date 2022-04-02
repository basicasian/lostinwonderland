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
import android.widget.Button;
import android.widget.Toast;

import at.ac.tuwien.mmue_ll6.assets.Background;
import at.ac.tuwien.mmue_ll6.assets.Enemy;
import at.ac.tuwien.mmue_ll6.assets.Flummi;
import at.ac.tuwien.mmue_ll6.assets.Sprite;


public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private GameLoop gameLoop;
    private Thread gameMainThread;

    private Paint paint;

    private Flummi flummi;
    private Enemy enemy;
    private Sprite fire;
    private Background background;

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
        // Initialize the assets
        flummi = new Flummi(BitmapFactory.decodeResource(context.getResources(), R.drawable.flummi));
        enemy = new Enemy(BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy));
        fire = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.fire), 4);

        background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background));
        paint = new Paint();
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // SurfaceView has been created
        // e.g. create Gameloop and start
        startGame(surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder,  int format, int width, int height) {
        // SurfaceView has been changed verändert (size, format,…)
        // e.g. end Gameloop cleanly

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        // SurfaceView has been hidden
        endGame();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // a touch-event has been triggered
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) e.getX();

            if (!Rect.intersects(flummi.getRectTarget(), enemy.getRectTarget())) {
                if (x > flummi.getX()) {
                    flummi.updateRight();
                }
                if (x < flummi.getX()) {
                    flummi.updateLeft();
                }

                if (Rect.intersects(flummi.getRectTarget(), enemy.getRectTarget())) {
                    collisionEvent();
                }
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
            canvas.drawColor(Color.rgb(255, 255, 255));

            background.draw(canvas);

            flummi.draw(canvas);
            enemy.draw(canvas);
            fire.draw(canvas);

        }
    }
}
