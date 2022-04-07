package at.ac.tuwien.mmue_ll6.assets;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

/**
 * Encapsulated entity for handling sprites
 * @author Renate Zhang
 */
public class Sprite {

    //Bitmap to get character from image
    Bitmap bitmap;
    //coordinates
    private final int x;
    private final int y;
    //to extract the frame out of the bitmap
    private final int frameWidth;
    private final int frameHeight;
    private int currentFrame;

    private long pastTime = 0;
    private final int frameTime;

    /**
     * constructor for the class Sprite
     * @param bitmap the used bitmap
     * @param totalFrames number of total frames of sprite sheet
     * @param x the y coordinate of the target rect
     * @param y the y coordinate of the target rect
     */
    public Sprite(Bitmap bitmap, int totalFrames, int x, int y) {
        this.bitmap = bitmap;
        currentFrame = 0;

        frameWidth = bitmap.getWidth() / totalFrames;
        frameHeight = bitmap.getHeight();

        this.x = x;
        this.y = y;
        this.frameTime = 150;
    }

    /**
     * checks if its time for the next frame
     * @param currentTime the current time to check if its time for the next frame
     */
    public void update(long currentTime) {
        if (currentTime > pastTime + frameTime) {
            pastTime = currentTime;
            currentFrame++;
            currentFrame %= 4;
        }
    }

    /**
     * draws the current frame onto the canvas
     * @param canvas which is drawn on
     */
    public void draw(Canvas canvas) {
        if (canvas != null) {
            Rect targetRect = new Rect(x, y - frameHeight * 2, x + frameWidth * 2, y);
            Rect sourceRect = new Rect(currentFrame * frameWidth + 2, 0, (currentFrame + 1) * frameWidth, frameHeight);
            canvas.drawBitmap(bitmap, sourceRect, targetRect, null);
        }
    }


}
