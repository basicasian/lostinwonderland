package at.ac.tuwien.mmue_ll6;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity if the player lost or won the game
 * @author Renate Zhang
 */
public class AfterGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_game);

        Animation travel = AnimationUtils.loadAnimation(this, R.anim.travel);
        findViewById(R.id.afterGameImagePlayer).startAnimation(travel);
    }

    public void restartGameActivity(View v) {
        startActivity(new Intent(this, GameActivity.class));
    }

    public void returnMenuActivity(View v) {
        startActivity(new Intent(this, MainActivity.class));
    }

}