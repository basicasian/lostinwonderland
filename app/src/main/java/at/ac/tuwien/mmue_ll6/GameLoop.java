package at.ac.tuwien.mmue_ll6;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Button;

public class GameLoop implements Runnable {

    public long deltaTime;
    public long lastTime;
    public long nowTime;
    int framesSkipped;
    int sleepTime;

    private SurfaceHolder surfaceHolder;
    private GameSurfaceView gameSurfaceView;
    private boolean running;

    private static final String TAG = GameLoop.class.getSimpleName();

    private final static int  MAX_FPS = 50;
    private final static int  MAX_FRAME_SKIPS = 5;
    private final static int  FRAME_PERIOD = 1000 / MAX_FPS;

    public GameLoop(SurfaceHolder surfaceHolder, GameSurfaceView gameSurfaceView) {
        this.surfaceHolder = surfaceHolder;
        this.gameSurfaceView = gameSurfaceView;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        Canvas canvas;
        Log.d(TAG, "Starting game loop");

        //One time Updates before first frame update is called
        start();

        setRunning(true);

        while (running){
            //Update game logic
            update();
            //Render assets
            render();
        }
    }

    private void start() {
        //todo: calculate the time delta between the last frame and the current frame
        lastTime = System.currentTimeMillis();
        sleepTime = 0;
    }

    private void update() {
        //todo: calculate the time delta between the last frame and the current frame
        //Calculate time delta for frame independence
        calculateDeltaTime();

        this.gameSurfaceView.update();
    }

    //@SuppressLint("WrongCall")
    private void render() {
        Canvas canvas = null;
        try {
            canvas = surfaceHolder.lockCanvas();
            synchronized (surfaceHolder){
                if (canvas == null) return;

                gameSurfaceView.draw(canvas);

                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {}
                }
                while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                    this.gameSurfaceView.update();
                    sleepTime += FRAME_PERIOD;
                    framesSkipped++;
                }
            }
        } finally {
            if (canvas != null) surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }

    private void calculateDeltaTime() {
        //todo: calculate the time delta between the last frame and the current frame
        nowTime = System.currentTimeMillis();
        // from nano seconds to milliseconds
        deltaTime = nowTime-lastTime;
        lastTime = nowTime;

        sleepTime = (int)(FRAME_PERIOD - deltaTime);
    }

}
