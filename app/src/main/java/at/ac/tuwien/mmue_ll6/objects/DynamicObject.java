package at.ac.tuwien.mmue_ll6.objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Encapsulated entity for handling dynamic objects, such as the main character or enemies
 * @author Renate Zhang
 */
public class DynamicObject{

    // bitmap to get character from image
    private final Bitmap bitmap;

    // source and target rectangles
    private final Rect rectSrc;
    private Rect rectTarget;

    // coordinates
    private int x;
    private int y;

    // player values
    private int lives = 3;

    /**
     * constructor for the dynamic object
     * @param bitmap the used bitmap
     * @param x the x coordinate of the target rect
     * @param y the y coordinate of the target rect
     */
    public DynamicObject(Bitmap bitmap, int x, int y) {
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
     * method to update x and y coordinate of character
     * @param deltaX how much the x coordinate should be moved
     * @param deltaY how much the x coordinate should be moved
     */
    public void move(double deltaX, double deltaY){
        this.x += deltaX;
        this.y += deltaY;

        this.rectTarget.left = x;
        this.rectTarget.right = x + bitmap.getWidth();

        // coordinate system starts from top left! (in landscape mode)
        // but elements are initialized from bottom left
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

    /**
     * reduce the number of this.lives
     */
    public void reduceLive() {
        lives--;
    }

    /**
     * get the number of current lives
     * @return the number of lives
     */
    public int getNumberOfLives() {
        return lives;
    }

    /**
     * set the character back to a certain point
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void setToStart (int x, int y) {
        this.x = x;
        this.y = y;
    }
}

