package at.ac.tuwien.mmue_ll6.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import at.ac.tuwien.mmue_ll6.R;
import at.ac.tuwien.mmue_ll6.persistence.Score;
import at.ac.tuwien.mmue_ll6.persistence.ScoreRoomDatabase;
import at.ac.tuwien.mmue_ll6.util.Concurrency;

/**
 * Activity to see high score (score is currently shortest time to complete level)
 * @author Renate Zhang
 */
public class HighScoreActivity extends AppCompatActivity {

    private TextView highscoreTimes;

    private interface OnScoreLoadedListener {
        void onScoresLoaded(List<Score> users);
    }
    private final OnScoreLoadedListener onScoresLoadedListener = this::updateScoresTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        highscoreTimes = findViewById(R.id.highscoreTimes);

        // Load scores
        Concurrency.executeAsync(() -> {
            List<Score> users = loadScores();
            runOnUiThread(() -> onScoresLoadedListener.onScoresLoaded(users));
        });
    }

    private List<Score> loadScores() {
        return ScoreRoomDatabase.getInstance(this).scoreDao().selectAllScores();
    }

    private void updateScoresTable(List<Score> scores) {
        StringBuilder text = new StringBuilder();
        int ranking = 1;
        for (Score score : scores) {
            text.append(ranking).append(":").append(score.time).append("\n");
            ranking++;
        }
        highscoreTimes.setText(text.toString());
    }
}