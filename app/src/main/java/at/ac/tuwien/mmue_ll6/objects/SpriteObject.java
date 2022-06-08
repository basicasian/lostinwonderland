package at.ac.tuwien.mmue_ll6.objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Encapsulated entity for handling sprites
 * @author Renate Zhang
 */
public class SpriteObject {

    //Bitmap to get character from image
    Bitmap bitmap;
    private Rect rectSrc;
    private Rect rectTarget;

    //coordinates
    private int x;
    private int y;
    //to extract the frame out of the bitmap
    private final int frameWidth;
    private final int frameHeight;
    private int currentFrame;
    private int totalFrames;

    private long pastTime = 0;
    private final int frameTime;

    /**
     * constructor for the class Sprite
     * @param bitmap the used bitmap
     * @param totalFrames number of total frames of sprite sheet
     * @param x the y coordinate of the target rect
     * @param y the y coordinate of the target rect
     */
    public SpriteObject(Bitmap bitmap, int totalFrames, int x, int y) {
        this.bitmap = bitmap;
        this.currentFrame = 0;
        this.totalFrames = totalFrames;

        this.frameWidth = bitmap.getWidth() / totalFrames;
        this.frameHeight = bitmap.getHeight();

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
            currentFrame %= this.totalFrames;
        }
    }

    /**
     * method to update x coordinate of character
     * @param deltaX how much the x coordinate should be moved
     * @param deltaY how much the y coordinate should be moved
     */
    public void move(double deltaX, double deltaY){
        this.x += deltaX;
        this.y += deltaY;

        this.rectTarget.left = x;
        this.rectTarget.right = x + bitmap.getWidth();
    }

    /**
     * draws the current frame onto the canvas
     * @param canvas which is drawn on
     */
    public void draw(Canvas canvas) {
        if (canvas != null) {
            // source and target rectangle
            this.rectTarget = new Rect(x, y - frameHeight * 2, x + frameWidth * 2, y);
            this.rectSrc = new Rect(currentFrame * frameWidth + 2, 0, (currentFrame + 1) * frameWidth, frameHeight);

            canvas.drawBitmap(bitmap, rectSrc, rectTarget, null);
        }
    }


}
