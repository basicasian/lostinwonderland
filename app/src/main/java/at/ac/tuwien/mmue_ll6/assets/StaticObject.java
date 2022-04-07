package at.ac.tuwien.mmue_ll6.assets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import at.ac.tuwien.mmue_ll6.R;

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
     * constructor for the class StaticObject with scale factor if bitmap is too big/ small
     * @param bitmap the used bitmap
     * @param x the x coordinate of the target rect
     * @param y the y coordinate of the target rect
     * @param scale scale factor to reduce/ increase size of bitmap
     */
    public StaticObject(Bitmap bitmap, int x, int y, float scale) {
        this.bitmap = bitmap;

        // source and target rectangle
        this.rectTarget = new Rect(x, y, (int) (bitmap.getWidth() * scale + x), (int) (bitmap.getHeight()*scale + y));
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

