package fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.EpisodesDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.MoviesDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.SeasonDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBOptimizer.SerieWithSeasonsDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.SeriesDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;

/**
 * Created by lucasamiaud on 29/12/2017.
 */

@Database(entities = {Movie.class, Serie.class, Season.class, Episode.class}, version = 1)
@TypeConverters({RoomTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract MoviesDAO moviesDAO();
    public abstract SeriesDAO seriesDAO();
    public abstract SeasonDAO seasonDAO();
    public abstract EpisodesDAO episodesDAO();
    public abstract SerieWithSeasonsDAO serieWithSeasonsDAO();

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

    public void nukeDB(){
        moviesDAO().deleteAllMovies();
        episodesDAO().deleteAllEpisodes();
        seasonDAO().deleteAllSeasons();
        seriesDAO().deleteAllSeries();
        destroyInstance();
    }
}
