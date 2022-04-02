package at.ac.tuwien.mmue_ll6.assets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import at.ac.tuwien.mmue_ll6.R;

public class Background {

    //Bitmap to get character from image
    private Bitmap bitmap;
    private Rect rectTarget, rectSrc;

    //coordinates
    private int x;
    private int y;

    //constructor
    public Background(Bitmap bitmap) {
        this.bitmap = bitmap;

        x = 0;
        y = 0;

        // source and target rectangle
        rectTarget = new Rect(x, y, 1900, 930);
        rectSrc = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Rect getRectTarget() {
        return rectTarget;}

    public Rect getRectSrc() {
        return rectSrc;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // draws the current frame onto the canvas
    public void draw(Canvas canvas) {
        if (canvas != null) {
            canvas.drawBitmap(bitmap, rectSrc, rectTarget, null);
        }
    }
}

