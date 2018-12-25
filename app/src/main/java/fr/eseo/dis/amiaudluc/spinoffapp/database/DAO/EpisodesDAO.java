package fr.eseo.dis.amiaudluc.spinoffapp.database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.model.Episode;

/**
 * Created by lucasamiaud on 05/04/2018.
 */

@Dao
public interface EpisodesDAO {

    @Insert
    void insertEpisode(Episode episode);

    @Insert
    void insertAll(List<Episode> episodes);

    @Query("SELECT * FROM episodes")
    List<Episode> getAll();

    @Query("SELECT * FROM EPISODES where id = :id")
    Episode getEpisodeById(int id);

    @Query("Select * from EPISODES where season_id = :id")
    List<Episode> getEpisodesBySeasonId(int id);

    @Query("DELETE FROM EPISODES")
    void deleteAllEpisodes();

}
