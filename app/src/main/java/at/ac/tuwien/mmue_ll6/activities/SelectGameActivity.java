package at.ac.tuwien.mmue_ll6.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import at.ac.tuwien.mmue_ll6.R;

/**
 * Activity for selecting the level, thus difficulty
 * @author Michelle Lau
 */
public class SelectGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);
    }

    public void startGameActivity(View v) {
        startActivity(new Intent(this, GameActivity.class));
    }

}