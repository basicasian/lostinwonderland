package at.ac.tuwien.mmue_ll6.activities;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import at.ac.tuwien.mmue_ll6.R;

/**
 * Activity for settings (language settings)
 * @author Renate Zhang
 */
public class SettingsActivity extends AppCompatActivity {
    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";

    TextView messageView;
    RadioButton englishButton, germanButton;
    RadioButton soundButtonYes, soundButtonNo;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // to set sound settings in game
        // create object of SharedPreferences.
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();

        // referencing the text and button views
        messageView = findViewById(R.id.textView);
        saveButton = findViewById(R.id.saveButton);
        englishButton = findViewById(R.id.englishButton);
        germanButton = findViewById(R.id.germanButton);
        soundButtonYes = findViewById(R.id.soundButtonYes);
        soundButtonNo = findViewById(R.id.soundButtonNo);

        // setting up on click listener event over the button
        // in order to change the language and sound with the help of the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // language
                if (englishButton.isChecked()) {
                    setLocale(SettingsActivity.this, "en");
                }
                if (germanButton.isChecked()) {
                    setLocale(SettingsActivity.this, "de");
                }

                // music
                if (soundButtonYes.isChecked()) {
                    editor.putBoolean("sound", true);
                }
                if (soundButtonNo.isChecked()) {
                    editor.putBoolean("sound", false);
                }
                // commits your edits
                editor.apply();
            }
        });

        // language
        englishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                germanButton.setChecked(false);
            }
        });
        germanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                englishButton.setChecked(false);
            }
        });

        // music
        soundButtonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundButtonNo.setChecked(false);
            }
        });
        soundButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundButtonYes.setChecked(false);
            }
        });
    }

    // the method is used to set the language at runtime
    public void setLocale(Context context, String language) {
        persist(context, language);
        updateResources(context, language);
        Intent refresh = new Intent(this, MainActivity.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(refresh);
    }

    private static void persist(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }

    // the method is used update the language of application by creating
    // object of inbuilt Locale class and passing language argument to it
    private static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(locale);
        conf.setLayoutDirection(locale);
        res.updateConfiguration(conf, dm);
    }
}