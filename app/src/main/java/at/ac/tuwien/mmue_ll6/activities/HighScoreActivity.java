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
 * Activity to see high score (score is shortest time to complete level)
 * @author Renate Zhang
 */
public class HighScoreActivity extends AppCompatActivity {

    private TextView highscoreEasy;
    private TextView highscoreHard;

    private interface OnScoreLoadedListener {
        void onScoresLoaded(List<Score> scores, int level);
    }
    private final OnScoreLoadedListener onScoresLoadedListener = this::updateScoresTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        highscoreEasy = findViewById(R.id.highscoreTimesEasy);
        highscoreHard = findViewById(R.id.highscoreTimesHard);

        // load scores for each level
        Concurrency.executeAsync(() -> {
            List<Score> scores = loadScores(1);
            runOnUiThread(() -> onScoresLoadedListener.onScoresLoaded(scores, 1));
        });

        Concurrency.executeAsync(() -> {
            List<Score> scores = loadScores(2);
            runOnUiThread(() -> onScoresLoadedListener.onScoresLoaded(scores, 2));
        });
    }

    private List<Score> loadScores(int level) {
        return ScoreRoomDatabase.getInstance(this).scoreDao().selectAllScores(level);
    }

    private void updateScoresTable(List<Score> scores, int level) {
        StringBuilder text = new StringBuilder();
        int ranking = 1;
        for (Score score : scores) {
            text.append(ranking).append(" : ").append(score.time).append("\n");
            ranking++;
        }
        if (level == 1) {
            highscoreEasy.setText(text.toString());
        } else if (level == 2) {
            highscoreHard.setText(text.toString());
        }
    }

}