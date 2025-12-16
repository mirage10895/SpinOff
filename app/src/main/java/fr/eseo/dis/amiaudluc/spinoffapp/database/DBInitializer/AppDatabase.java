package fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.EpisodeDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.MovieDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.SerieDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.EpisodeDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;

/**
 * Created by lucasamiaud on 29/12/2017.
 */

@Database(entities = {MovieDatabase.class, SerieDatabase.class, EpisodeDatabase.class}, version = 1)
@TypeConverters({RoomTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract MovieDAO moviesDAO();

    public abstract SerieDAO serieDAO();

    public abstract EpisodeDAO episodeDAO();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "spinoff-database"
                    )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static void destroyInstance() {
        INSTANCE = null;
    }
}
