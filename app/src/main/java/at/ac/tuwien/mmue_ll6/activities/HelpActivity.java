package at.ac.tuwien.mmue_ll6.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import at.ac.tuwien.mmue_ll6.R;

/**
 * Activity to see instruction to play / tutorial
 * @author Renate Zhang
 */
public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
    }
}