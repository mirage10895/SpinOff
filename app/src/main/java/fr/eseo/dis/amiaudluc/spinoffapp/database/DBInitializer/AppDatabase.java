package fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import android.content.Context;

import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.EpisodeDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.MovieDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.SeasonDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.SerieDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.SerieNotificationDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.EpisodeDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SeasonDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieNotificationDatabase;

/**
 * Created by lucasamiaud on 29/12/2017.
 */

@Database(entities = {MovieDatabase.class, SerieDatabase.class, EpisodeDatabase.class, SeasonDatabase.class,
        SerieNotificationDatabase.class}, version = 5)
@TypeConverters({RoomTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract MovieDAO moviesDAO();

    public abstract SerieDAO serieDAO();

    public abstract EpisodeDAO episodeDAO();

    public abstract SeasonDAO seasonDAO();

    public abstract SerieNotificationDAO serieNotificationDAO();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE movie ADD COLUMN genres TEXT");
            database.execSQL("ALTER TABLE movie ADD COLUMN release_date INTEGER");
            database.execSQL("ALTER TABLE serie ADD COLUMN genres TEXT");
            database.execSQL("ALTER TABLE serie ADD COLUMN season_count INTEGER");
            database.execSQL("ALTER TABLE serie ADD COLUMN episode_count INTEGER");
            database.execSQL("ALTER TABLE serie ADD COLUMN first_air_date INTEGER");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE movie ADD COLUMN vote_average REAL");
            database.execSQL("ALTER TABLE movie ADD COLUMN original_language TEXT");
            database.execSQL("ALTER TABLE movie ADD COLUMN popularity REAL");
            database.execSQL("ALTER TABLE movie ADD COLUMN production_countries TEXT");
            database.execSQL("ALTER TABLE movie ADD COLUMN actors TEXT");
            database.execSQL("ALTER TABLE movie ADD COLUMN directors TEXT");

            database.execSQL("ALTER TABLE serie ADD COLUMN vote_average REAL");
            database.execSQL("ALTER TABLE serie ADD COLUMN original_language TEXT");
            database.execSQL("ALTER TABLE serie ADD COLUMN popularity REAL");
            database.execSQL("ALTER TABLE serie ADD COLUMN production_countries TEXT");
            database.execSQL("ALTER TABLE serie ADD COLUMN actors TEXT");
            database.execSQL("ALTER TABLE serie ADD COLUMN directors TEXT");
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // serie_episode becomes a lean link table holding only watched episodes
            database.execSQL("DROP TABLE IF EXISTS serie_episode");
            database.execSQL("CREATE TABLE IF NOT EXISTS serie_episode ("
                    + "serie_id INTEGER NOT NULL, "
                    + "season_number INTEGER NOT NULL, "
                    + "episode_number INTEGER NOT NULL, "
                    + "PRIMARY KEY(serie_id, season_number, episode_number), "
                    + "FOREIGN KEY(serie_id) REFERENCES serie(id) ON UPDATE NO ACTION ON DELETE CASCADE)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_serie_episode_serie_id ON serie_episode(serie_id)");

            // serie_season stores the season metadata (episode count) and its watched flag
            database.execSQL("CREATE TABLE IF NOT EXISTS serie_season ("
                    + "serie_id INTEGER NOT NULL, "
                    + "season_number INTEGER NOT NULL, "
                    + "episode_count INTEGER, "
                    + "watched INTEGER NOT NULL, "
                    + "PRIMARY KEY(serie_id, season_number), "
                    + "FOREIGN KEY(serie_id) REFERENCES serie(id) ON UPDATE NO ACTION ON DELETE CASCADE)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_serie_season_serie_id ON serie_season(serie_id)");
        }
    };

    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Dedicated release-tracking table: the library tables (serie, serie_season,
            // serie_episode) stay untouched and keep their ownership of the watched state.
            database.execSQL("CREATE TABLE IF NOT EXISTS serie_notification ("
                    + "serie_id INTEGER NOT NULL, "
                    + "enabled INTEGER NOT NULL DEFAULT 1, "
                    + "last_known_status TEXT, "
                    + "last_known_season_count INTEGER, "
                    + "last_seen_season INTEGER, "
                    + "last_seen_episode INTEGER, "
                    + "last_seen_air_date INTEGER, "
                    + "notified_at INTEGER, "
                    + "next_episode_season INTEGER, "
                    + "next_episode_number INTEGER, "
                    + "next_episode_air_date INTEGER, "
                    + "next_episode_name TEXT, "
                    + "PRIMARY KEY(serie_id), "
                    + "FOREIGN KEY(serie_id) REFERENCES serie(id) ON UPDATE NO ACTION ON DELETE CASCADE)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_serie_notification_serie_id "
                    + "ON serie_notification(serie_id)");
        }
    };

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "spinoff-database"
                    )
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
