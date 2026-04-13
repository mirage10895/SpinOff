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
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.SerieDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.EpisodeDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;

/**
 * Created by lucasamiaud on 29/12/2017.
 */

@Database(entities = {MovieDatabase.class, SerieDatabase.class, EpisodeDatabase.class}, version = 3)
@TypeConverters({RoomTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract MovieDAO moviesDAO();

    public abstract SerieDAO serieDAO();

    public abstract EpisodeDAO episodeDAO();

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

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "spinoff-database"
                    )
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
