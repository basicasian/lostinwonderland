package at.ac.tuwien.mmue_ll6.assets;

import android.app.blob.BlobHandle;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

/**
 * Encapsulated entity for handling Flummi, the main character
 * @author Renate Zhang
 */
public class Flummi {

    //Bitmap to get character from image
    private final Bitmap bitmap;
    private final Rect rectSrc;
    private Rect rectTarget;

    //coordinates
    private int x;
    private int y;

    private boolean isJumping;


    /**
     * constructor for the class Flummi
     * @param bitmap the used bitmap
     * @param x the x coordinate of the target rect
     * @param y the y coordinate of the target rect
     */
    public Flummi(Bitmap bitmap, int x, int y) {
        this.bitmap = bitmap;

        this.x = x;
        this.y = y;

        // source and target rectangle
        this.rectTarget = new Rect(x, y - bitmap.getHeight(), x + bitmap.getWidth(), y);
        this.rectSrc = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    public Rect getRectTarget() { return rectTarget;}

    public int getX() { return x;}

    /**
     * method to update x coordinate of character
     * @param deltaX how much the x coordinate should be moved
     */
    public void moveX(int deltaX){
        this.x += deltaX;

        this.rectTarget.left = x;
        this.rectTarget.right = x + bitmap.getWidth();
    }

    /**
     * method to update y coordinate of character
     * @param deltaY how much the y coordinate should be moved
     */
    public void moveY(int deltaY){
        this.y += deltaY;

        this.rectTarget.top = y - bitmap.getHeight();
        this.rectTarget.bottom = y;
    }

    /**
     * draws the current frame onto the canvas
     * @param canvas which is drawn on
     */
    public void draw(Canvas canvas) {
        if (canvas != null) {
            canvas.drawBitmap(bitmap, rectSrc, rectTarget, null);
        }
    }

}

