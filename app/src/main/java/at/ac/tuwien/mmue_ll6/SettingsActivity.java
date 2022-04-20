package at.ac.tuwien.mmue_ll6;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * Activity for settings (planned: sound settings, etc.)
 * @author Renate Zhang
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
}