package at.ac.tuwien.mmue_ll6.objects;

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

    /**
     * constructor for the class StaticObject
     * @param bitmap the used bitmap
     * @param left the left coordinate of the target rect
     * @param right the right coordinate of the target rect
     * @param top the top coordinate of the target rect
     * @param bottom the bottom coordinate of the target rect
     */
    public StaticObject(Bitmap bitmap, int left, int right, int top, int bottom) {
        this.bitmap = bitmap;

        // source and target rectangle
        rectTarget = new Rect(left, top, right, bottom);
        rectSrc = new Rect(0, 0, this.bitmap.getWidth(), this.bitmap.getHeight());
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

