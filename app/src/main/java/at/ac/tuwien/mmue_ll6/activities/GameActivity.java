package at.ac.tuwien.mmue_ll6.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;

import at.ac.tuwien.mmue_ll6.game.GameSurfaceView;
import at.ac.tuwien.mmue_ll6.R;

/**
 * Activity for playing the game
 * @author Renate Zhang
 */
public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        int level = b.getInt("level"); // parameter from last activity

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean sound = sharedPref.getBoolean("sound", true);

        Log.d("GameActivity", "onCreate sound: " + sound);

        setContentView(R.layout.activity_game); // set content view first
        GameSurfaceView gameSurfaceView = this.findViewById(R.id.gameSurfaceView); // use id to set level
        gameSurfaceView.setLevel(level);
        gameSurfaceView.setSound(sound);

        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}