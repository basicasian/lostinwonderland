package at.ac.tuwien.mmue_ll6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class SplashScreenActivity extends AppCompatActivity implements Animation.AnimationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        startAnimations();
    }

    private void startAnimations() {
        Animation fade = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fade.setAnimationListener(this);
        findViewById(R.id.splashImage).startAnimation(fade);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // When all animations have finished - start the next activity
        Intent intent = new Intent(this, VideoActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

}