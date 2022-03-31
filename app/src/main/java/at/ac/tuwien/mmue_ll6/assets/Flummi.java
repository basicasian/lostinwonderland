package at.ac.tuwien.mmue_ll6.assets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import at.ac.tuwien.mmue_ll6.R;

public class Flummi {

    //Bitmap to get character from image
    private Bitmap bitmap;

    //coordinates
    private int x;
    private int y;

    //motion speed of the character
    private int speed = 0;

    //constructor
    public Flummi(Context context) {
        x = 75;
        y = 50;
        speed = 1;

        // Getting bitmap from drawable resource
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pusheen);
    }

    //Method to update coordinate of character
    public void update(){
        //updating x coordinate
        x++;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }


}

