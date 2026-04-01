package fr.eseo.dis.amiaudluc.spinoffapp.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.CalendarBean;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.EpisodeDatabase;

/**
 * Created by lucasamiaud on 05/04/2018.
 */

@Dao
public interface EpisodeDAO {

    @Insert
    void insertEpisode(EpisodeDatabase episode);

    @Query("select * FROM serie_episode where serie_id = :serieId")
    LiveData<List<EpisodeDatabase>> fetchWatchedEpisodesBySerieId(long serieId);

    @Query("select serie_episode.name, serie_episode.air_date, serie.poster_path, 'true' as watched from serie_episode join serie on serie_id = serie.id")
    LiveData<List<CalendarBean>> getAllEpisodesBySerie();
}
