package fr.eseo.dis.amiaudluc.spinoffapp.database.dao;

import android.database.Cursor;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SeasonDatabase;

@Dao
public interface SeasonDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertSeason(SeasonDatabase season);

    @Query("update serie_season set episode_count = :episodeCount where serie_id = :serieId and season_number = :seasonNumber and :episodeCount > 0")
    void updateSeasonEpisodeCount(int serieId, int seasonNumber, int episodeCount);

    /**
     * Insert the season if absent, otherwise refresh its episode count while keeping its watched state.
     * A non-positive episode count is ignored so a season detail response (which may omit the count)
     * never overwrites a valid value coming from the serie sync.
     */
    default void upsertSeason(int serieId, int seasonNumber, int episodeCount) {
        SeasonDatabase season = new SeasonDatabase();
        season.setSerieId(serieId);
        season.setSeasonNumber(seasonNumber);
        season.setEpisodeCount(episodeCount);
        season.setWatched(false);
        insertSeason(season);
        if (episodeCount > 0) {
            updateSeasonEpisodeCount(serieId, seasonNumber, episodeCount);
        }
    }

    /**
     * Apply {@link #upsertSeason(int, int, int)} to every season of a serie payload, ignoring the
     * entries without a season number.
     */
    @Transaction
    default void upsertSeasons(int serieId, List<Season> seasons) {
        for (Season season : seasons) {
            if (season.getSeasonNumber() != null) {
                upsertSeason(serieId, season.getSeasonNumber(), season.getEpisodeCount());
            }
        }
    }

    @Query("SELECT * FROM serie_season")
    Cursor getAllCursor();

    @Query("DELETE FROM serie_season")
    void deleteAll();

    @Query("select * from serie_season")
    LiveData<List<SeasonDatabase>> getAll();

    @Query("select * from serie_season where serie_id = :serieId order by season_number")
    LiveData<List<SeasonDatabase>> getSeasonsBySerie(int serieId);

    @Query("select * from serie_season where serie_id = :serieId order by season_number")
    List<SeasonDatabase> getSeasonsBySerieSync(int serieId);

    @Query("select * from serie_season where serie_id = :serieId and season_number = :seasonNumber")
    LiveData<SeasonDatabase> getSeason(int serieId, int seasonNumber);

    @Query("select * from serie_season where serie_id = :serieId and season_number = :seasonNumber")
    SeasonDatabase getSeasonSync(int serieId, int seasonNumber);

    @Query("update serie_season set watched = :watched where serie_id = :serieId and season_number = :seasonNumber")
    void setSeasonWatched(int serieId, int seasonNumber, boolean watched);

    @Query("update serie_season set watched = :watched where serie_id = :serieId and season_number != 0")
    void setAllSeasonsWatched(int serieId, boolean watched);

    @Query("delete from serie_season where serie_id = :serieId")
    void deleteBySerie(int serieId);
}
