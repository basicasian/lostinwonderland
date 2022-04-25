package at.ac.tuwien.mmue_ll6;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * The game loop for running and updating the game
 * @author Renate Zhang
 */
public class GameLoop implements Runnable {

    private static final String TAG = GameLoop.class.getSimpleName();

    private final SurfaceHolder surfaceHolder;
    private final GameSurfaceView gameSurfaceView;
    private boolean running;

    private final static int  MAX_FPS = 50;
    private final static int  MAX_FRAME_SKIPS = 5;
    private final static int  FRAME_PERIOD = 1000 / MAX_FPS;

    public double deltaTime;
    public double lastTime;
    public double nowTime;
    int framesSkipped;
    int sleepTime;

    /**
     * constructor for the class GameLoop
     * @param gameSurfaceView corresponding game view
     * @param surfaceHolder corresponding surface holder needed for locking the canvas
     */
    public GameLoop(SurfaceHolder surfaceHolder, GameSurfaceView gameSurfaceView) {
        this.surfaceHolder = surfaceHolder;
        this.gameSurfaceView = gameSurfaceView;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * run method, starts the timer and updates the game while running
     */
    @Override
    public void run() {
        Log.i(TAG, "Starting game loop");

        //one time updates before first frame update is called
        start();
        setRunning(true);

        while (running){
            //Update game logic
            update();
            //Render assets
            render();
        }
    }

    /**
     * start the timer to calculate the deltaTime later
     */
    private void start() {
        lastTime = System.currentTimeMillis();
        sleepTime = 0;
    }

    /**
     * updates the game logic in gameSurfaceView, and calculates the deltaTime
     */
    private void update() {
        // Calculate time delta for frame independence
        calculateDeltaTime();

        // this deltaTime is in milliseconds, but we pass the deltaTime in seconds
        this.gameSurfaceView.update(deltaTime/1000);
    }

    /**
     * creates a locked canvas, which is drawn onto with the gameSurfaceView
     * thread will sleep for frame independence
     */
    private void render() {
        Canvas canvas = null;
        try {
            // creates a canvas, which no other code can write onto until unlockCanvasAndPost() is called
            canvas = surfaceHolder.lockCanvas();
            synchronized (surfaceHolder){
                if (canvas == null) return;

                gameSurfaceView.draw(canvas);

                // for frame independence
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        Log.e("Error", e.getMessage());
                    }
                }
                while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                    sleepTime += FRAME_PERIOD;
                    framesSkipped++;
                }
            }
        } finally {
            if (canvas != null) surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * calculate the time delta between the last frame and the current frame
     */
    private void calculateDeltaTime() {
        nowTime = System.currentTimeMillis();
        deltaTime = nowTime-lastTime;
        lastTime = nowTime;

        sleepTime = (int)(FRAME_PERIOD - deltaTime);
    }

}
