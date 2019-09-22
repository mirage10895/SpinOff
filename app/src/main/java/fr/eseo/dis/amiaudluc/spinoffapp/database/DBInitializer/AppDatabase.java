package fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.EpisodeDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.MovieDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.SeasonDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.SerieDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.EpisodeDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.SeasonDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.SerieDatabase;

/**
 * Created by lucasamiaud on 29/12/2017.
 */

@Database(entities = {MovieDatabase.class, SerieDatabase.class, SeasonDatabase.class, EpisodeDatabase.class}, version = 1)
@TypeConverters({RoomTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract MovieDAO moviesDAO();
    public abstract SerieDAO serieDAO();
    public abstract SeasonDAO seasonDAO();
    public abstract EpisodeDAO episodeDAO();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "spinoff-database")
                            .build();
        }
        return INSTANCE;
    }

    private static void destroyInstance() {
        INSTANCE = null;
    }
}
