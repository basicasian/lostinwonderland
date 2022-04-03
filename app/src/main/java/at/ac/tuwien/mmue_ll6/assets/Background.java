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

    private int width;
    private int height;
    private int barHeight;

    private boolean first;

    //constructor
    public Background(Bitmap bitmap, int height, int width, int barHeight, boolean first) {
        this.bitmap = bitmap;

        this.height = height;
        this.width = width;
        this.barHeight = barHeight;

        // source and target rectangle
        if (first) {
            rectTarget = new Rect(barHeight, 0, height+barHeight, width);
        } else {
            rectTarget = new Rect(height+barHeight, 0, (height+barHeight)*2, width);
        }
        rectSrc = new Rect(0, 0, this.bitmap.getWidth(), this.bitmap.getHeight());
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

    public void move(int deltaX){
        // updating x coordinate
        x += deltaX;

        if (first) {
            rectTarget.left = barHeight + x;
            rectTarget.right = barHeight + height + x;
        } else {
            rectTarget.left = height+barHeight + x;
            rectTarget.right = (height+barHeight)*2 + x;
        }
    }
}

