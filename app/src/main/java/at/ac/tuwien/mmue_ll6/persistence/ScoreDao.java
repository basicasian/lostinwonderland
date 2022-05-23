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

    @Query("SELECT * FROM score")
    List<Score> selectAllScores();

    @Query("DELETE FROM score")
    void deleteAllScores();
}
