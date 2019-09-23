package fr.eseo.dis.amiaudluc.spinoffapp.database.DAO;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.EpisodeDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Episode;

/**
 * Created by lucasamiaud on 05/04/2018.
 */

@Dao
public interface EpisodeDAO {

    @Insert
    void insertEpisode(EpisodeDatabase EPISODES);

    @Insert
    void insertAll(List<EpisodeDatabase> EPISODES);

    @Query("SELECT * FROM EPISODES")
    LiveData<List<EpisodeDatabase>> getAll();

    @Query("SELECT * FROM EPISODES where id = :id")
    LiveData<EpisodeDatabase> getEpisodeById(int id);

    @Query("Select * from EPISODES where season_id = :id")
    LiveData<List<EpisodeDatabase>> getEpisodesBySeasonId(int id);

    @Query("Select EPISODES.name, EPISODES.air_date, SERIES.poster_path, episodes.watched from EPISODES " +
            "inner join SEASONS on SEASONS.id = EPISODES.season_id " +
            "inner join SERIES on SERIES.id = SEASONS.serie_id")
    LiveData<List<CalendarBean>> getAllEpisodesBySerie();

    @Query("DELETE FROM EPISODES")
    void deleteAllEpisodes();

}
