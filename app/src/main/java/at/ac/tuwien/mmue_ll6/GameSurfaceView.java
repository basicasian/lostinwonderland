package at.ac.tuwien.mmue_ll6;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private GameLoop gameLoop;
    private Thread gameMainThread;

    private Bitmap pusheen;
    private Rect pusheenRect, pusheenRectSrc;    // Target and source rectangles
    private Paint paint;

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setFocusable(true);

        loadAssets();
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

    private void loadAssets() {
        // Initialize the assets:

        pusheen = BitmapFactory.decodeResource(getResources(), R.drawable.pusheen);

        pusheenRect = new Rect(100, 50, pusheen.getWidth() + 50, pusheen.getHeight() + 50);
        pusheenRectSrc = new Rect(0, 0, pusheen.getWidth(), pusheen.getHeight());

        paint = new Paint();
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        startGame(surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder,  int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        endGame();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.rgb(253, 200, 218));
        canvas.drawBitmap(pusheen, pusheenRectSrc, pusheenRect, paint);
    }
}
