package at.ac.tuwien.mmue_ll6.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import at.ac.tuwien.mmue_ll6.GameSurfaceView;
import at.ac.tuwien.mmue_ll6.R;

/**
 * Activity for playing the the game
 * @author Renate Zhang
 */
public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        int level = b.getInt("level"); // parameter from last activity

        setContentView(R.layout.activity_game); // set content view first
        GameSurfaceView gameSurfaceView = this.findViewById(R.id.gameSurfaceView); // use id to set level

        gameSurfaceView.setLevel(level);

        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}