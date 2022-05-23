package at.ac.tuwien.mmue_ll6.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.HashMap;

@Database(entities = {Score.class}, version = 1)
public abstract class ScoreRoomDatabase extends RoomDatabase {
    public abstract ScoreDao scoreDao();

    private static final HashMap<Context, ScoreRoomDatabase> INSTANCES = new HashMap<>();

    public static ScoreRoomDatabase getInstance(Context context) {
        ScoreRoomDatabase db = INSTANCES.get(context);
        if (db == null) {
            db = Room.databaseBuilder(context, ScoreRoomDatabase.class, "score_db").build();
            INSTANCES.put(context, db);
        }
        return db;
    }
}