package at.ac.tuwien.mmue_ll6.assets;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

public class Sprite {
    Bitmap bitmap;
    private int x;
    int y;
    int frameWidth;
    int frameHeight;
    int totalFrames;
    int currentFrame;

    long pastTime = 0;
    int frameTime;

    public Sprite(Bitmap bitmap, int totalFrames) {
        this.bitmap = bitmap;
        this.totalFrames = totalFrames;
        currentFrame = 0;

        frameWidth = bitmap.getWidth() / this.totalFrames;
        frameHeight = bitmap.getHeight();

        x = 50;
        y = 500;
        frameTime = 150;
    }

    // checks if its time for the next frame
    public void update(long currentTime) {
        if (currentTime > pastTime + frameTime) {
            pastTime = currentTime;
            currentFrame++;
            currentFrame %= 4;
        }
    }

    // draws the current frame onto the canvas
    public void draw(Canvas canvas) {
        if (canvas != null) {
            Rect targetRect = new Rect(x, y, x + frameWidth * 2, y + frameHeight * 2);
            Rect sourceRect = new Rect(currentFrame * frameWidth + 2, 0, (currentFrame + 1) * frameWidth, frameHeight);
            canvas.drawBitmap(bitmap, sourceRect, targetRect, null);
        }
    }


}
