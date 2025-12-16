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

    @Query("SELECT * FROM EPISODES")
    LiveData<List<EpisodeDatabase>> getAll();

    @Query("SELECT * FROM EPISODES where id = :id")
    LiveData<EpisodeDatabase> getEpisodeById(int id);

    @Query("Select * from EPISODES where season_id = :id")
    LiveData<List<EpisodeDatabase>> getEpisodesBySeasonId(int id);

    @Query("Select name, air_date, watched from EPISODES ")
    LiveData<List<CalendarBean>> getAllEpisodesBySerie();
}
