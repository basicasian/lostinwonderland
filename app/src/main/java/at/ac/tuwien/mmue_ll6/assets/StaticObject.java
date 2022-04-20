package at.ac.tuwien.mmue_ll6.assets;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Encapsulated entity for handling static objects
 * @author Renate Zhang
 */
public class StaticObject {

    //Bitmap to get character from image
    private Bitmap bitmap;
    private Rect rectTarget, rectSrc;

    /**
     * constructor for the class StaticObject
     * @param bitmap the used bitmap
     * @param x the x coordinate of the target rect
     * @param y the y coordinate of the target rect
     */
    public StaticObject(Bitmap bitmap, int x, int y) {
        this.bitmap = bitmap;

        // source and target rectangle
        this.rectTarget = new Rect(x, y - bitmap.getHeight(), x + bitmap.getWidth(), y);
        this.rectSrc = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Rect getRectTarget() { return rectTarget;}

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

