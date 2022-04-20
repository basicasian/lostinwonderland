package at.ac.tuwien.mmue_ll6.assets;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

/**
 * Encapsulated entity for handling the background
 * @author Renate Zhang
 */
public class Background {

    //Bitmap to get character from image
    private Bitmap bitmap;
    private Rect rectTarget, rectSrc;

    //coordinates
    private int x;
    private int y;

    private int left;
    private int right;
    private int height;

    /**
     * constructor for the class Background
     * @param bitmap the used bitmap
     */
    public Background(Bitmap bitmap, int left, int right, int height) {
        this.bitmap = bitmap;

        this.left = left;
        this.right = right;
        this.height = height;

        // source and target rectangle
        Log.d("bg", "left: " + left);
        Log.d("bg", "right: " + right);
        Log.d("bg", "height: " + height);
        rectTarget = new Rect(left, 0, right, height);
        rectSrc = new Rect(0, 0, this.bitmap.getWidth(), this.bitmap.getHeight());
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

    public void move(int deltaX){
        // updating x coordinate
        x += deltaX;

        rectTarget.left = left + x;
        rectTarget.right = right + x;

    }
}

