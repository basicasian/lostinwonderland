package at.ac.tuwien.mmue_ll6.assets;

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
    private Bitmap bitmap;
    private Rect rectTarget, rectSrc;

    //coordinates
    private int x;
    private int y;

    //motion speed of the character
    private int speed = 0;

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
        this.rectTarget = new Rect(x, y, bitmap.getWidth()+ x, bitmap.getHeight() + y);
        this.rectSrc = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Rect getRectTarget() { return rectTarget;}

    /**
     * method to update coordinate of character
     * @param deltaX how much the x coordinate should be moved
     */
    public void move(int deltaX){

        this.x += deltaX;

        this.rectTarget.left = x;
        this.rectTarget.right = x + bitmap.getWidth();
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

