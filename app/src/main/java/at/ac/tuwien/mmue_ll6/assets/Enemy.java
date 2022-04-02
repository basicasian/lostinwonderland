package at.ac.tuwien.mmue_ll6.assets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import at.ac.tuwien.mmue_ll6.R;

public class Enemy {

    //Bitmap to get character from image
    private Bitmap bitmap;
    private Rect rectTarget, rectSrc;

    //coordinates
    private int x;
    private int y;

    //constructor
    public Enemy(Context context) {
        // getting bitmap from drawable resource
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);

        x = 800;
        y = 500;

        // source and target rectangle
        rectTarget = new Rect(x, y, bitmap.getWidth()+ x, bitmap.getHeight() + y);
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


}

