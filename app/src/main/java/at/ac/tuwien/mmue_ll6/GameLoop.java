package at.ac.tuwien.mmue_ll6;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.widget.Button;

public class GameLoop implements Runnable {

    public float deltaTime;
    public float lastTime;
    public float nowTime;

    private SurfaceHolder surfaceHolder;
    private GameSurfaceView gameSurfaceView;
    private boolean running;

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
        lastTime = System.nanoTime();
    }

    private void update() {
        //todo: calculate the time delta between the last frame and the current frame
        //Calculate time delta for frame independence
        calculateDeltaTime();

    }

    //@SuppressLint("WrongCall")
    private void render() {
        Canvas canvas = null;
        try{
            canvas = surfaceHolder.lockCanvas();
            synchronized (surfaceHolder){
                if(canvas == null) return;

                gameSurfaceView.draw(canvas);
            }
        } finally {
            if(canvas != null) surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }

    private void calculateDeltaTime() {
        //todo: calculate the time delta between the last frame and the current frame
        nowTime = System.nanoTime();
        // from nano seconds to milliseconds
        deltaTime = (nowTime-lastTime)/ 1000000;
        lastTime = nowTime;

    }

}
