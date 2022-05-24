package at.ac.tuwien.mmue_ll6.persistence;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity object for score
 * @author Michelle Lau
 */
@Entity(tableName = "score")
public class Score {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public double time;
    public int level;

    public Score(double time, int level) {
        this.time = time;
        this.level = level;
    }
}
