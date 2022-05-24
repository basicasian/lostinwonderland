package at.ac.tuwien.mmue_ll6.persistence;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Dao object for score
 * @author Michelle Lau
 */
@Dao
public interface ScoreDao {
    @Insert
    void insert(Score score);

    @Query("SELECT * FROM score WHERE level = :level ORDER BY time ASC LIMIT 5")
    List<Score> selectAllScores(int level);

    @Query("DELETE FROM score")
    void deleteAllScores();
}
