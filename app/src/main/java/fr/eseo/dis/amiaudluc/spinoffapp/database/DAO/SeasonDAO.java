package fr.eseo.dis.amiaudluc.spinoffapp.database.DAO;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.SeasonDatabase;

/**
 * Created by lucasamiaud on 15/03/2018.
 */

@Dao
public interface SeasonDAO {

    @Insert
    void insertSeason(SeasonDatabase season);

    @Query("SELECT * FROM SEASONS")
    LiveData<List<SeasonDatabase>> getAll();

    @Query("SELECT id FROM SEASONS")
    LiveData<List<Integer>> getAllIds();

    @Query("SELECT COUNT(*) from SEASONS")
    int countSeasons();

    @Insert
    void insertAll(SeasonDatabase ... seasons);

    @Delete
    void deleteSeason(SeasonDatabase season);

    @Query("DELETE FROM seasons")
    void deleteAllSeasons();

    @Query("SELECT * FROM SEASONS where id = :id")
    LiveData<SeasonDatabase> getSeasonById(int id);

    @Query("Select * from SEASONS where serie_id = :id")
    LiveData<List<SeasonDatabase>> getSeasonsBySerieId(int id);
}
