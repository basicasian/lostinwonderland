package at.ac.tuwien.mmue_ll6.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import at.ac.tuwien.mmue_ll6.R;

/**
 * activity for the main menu, starting new intents to other activities
 * @author Renate Zhang
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startSelectGameActivity(View v) {
        startActivity(new Intent(this, SelectGameActivity.class));
    }

    public void startVideoActivity(View v) {
        startActivity(new Intent(this, VideoActivity.class));
    }

    public void startHelpActivity(View v) {
        startActivity(new Intent(this, HelpActivity.class));
    }

    public void startSettingsActivity(View v) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void startHighScoreActivity(View v) {
        startActivity(new Intent(this, HighScoreActivity.class));
    }
}