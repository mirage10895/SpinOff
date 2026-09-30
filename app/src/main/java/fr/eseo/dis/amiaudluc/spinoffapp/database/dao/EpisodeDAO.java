package fr.eseo.dis.amiaudluc.spinoffapp.database.dao;

import android.database.Cursor;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.EpisodeDatabase;

/**
 * Created by lucasamiaud on 05/04/2018.
 */

@Dao
public interface EpisodeDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertEpisode(EpisodeDatabase episode);

    @Transaction
    default void insertEpisodes(List<EpisodeDatabase> episodes) {
        for (EpisodeDatabase episode : episodes) {
            insertEpisode(episode);
        }
    }

    @Query("SELECT * FROM serie_episode")
    Cursor getAllCursor();

    @Query("DELETE FROM serie_episode")
    void deleteAll();

    @Query("select * from serie_episode")
    LiveData<List<EpisodeDatabase>> getAll();

    @Query("select * from serie_episode where serie_id = :serieId and season_number = :seasonNumber order by episode_number")
    LiveData<List<EpisodeDatabase>> getEpisodesBySerieAndSeason(int serieId, int seasonNumber);

    @Query("select * from serie_episode where serie_id = :serieId and season_number = :seasonNumber order by episode_number")
    List<EpisodeDatabase> getEpisodesBySerieAndSeasonSync(int serieId, int seasonNumber);

    @Query("select * from serie_episode where serie_id = :serieId")
    LiveData<List<EpisodeDatabase>> getEpisodesBySerie(int serieId);

    @Query("delete from serie_episode where serie_id = :serieId and season_number = :seasonNumber and episode_number = :episodeNumber")
    void deleteEpisode(int serieId, int seasonNumber, int episodeNumber);

    @Query("delete from serie_episode where serie_id = :serieId and season_number = :seasonNumber")
    void deleteBySeason(int serieId, int seasonNumber);

    @Query("delete from serie_episode where serie_id = :serieId")
    void deleteBySerie(int serieId);

    @Query("select count(*) from serie_episode where serie_id = :serieId and season_number = :seasonNumber")
    int countBySeason(int serieId, int seasonNumber);
}
