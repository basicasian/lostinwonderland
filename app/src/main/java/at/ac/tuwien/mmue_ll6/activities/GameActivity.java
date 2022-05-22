package at.ac.tuwien.mmue_ll6.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import at.ac.tuwien.mmue_ll6.R;

/**
 * Activity for playing the the game
 * @author Renate Zhang
 */
public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

}