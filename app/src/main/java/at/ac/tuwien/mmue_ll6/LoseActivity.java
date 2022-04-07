package at.ac.tuwien.mmue_ll6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Activity if the player lost the game
 * @author Renate Zhang
 */
public class LoseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lose);
    }

    public void restartGameActivity(View v) {
        startActivity(new Intent(this, GameActivity.class));
    }

    public void returnMenuActivity(View v) {
        startActivity(new Intent(this, MainActivity.class));
    }

}