package fr.eseo.dis.amiaudluc.spinoffapp.database.DAO;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.SerieDatabase;

/**
 * Created by lucasamiaud on 08/03/2018.
 */

@Dao
public interface SerieDAO {

    @Insert
    public void insertSerie(SerieDatabase serie);

    @Query("SELECT * FROM series")
    LiveData<List<SerieDatabase>> getAll();

    @Query("SELECT id FROM series")
    LiveData<List<Integer>> getAllIds();

    @Query("SELECT * FROM series where name LIKE  :firstName")
    LiveData<SerieDatabase> findByTitle(String firstName);

    @Query("SELECT COUNT(*) from series")
    int countSeries();

    @Query("SELECT SUM(id) from SERIES")
    int countVotesSeries();

    @Insert
    void insertAll(SerieDatabase... series);

    @Delete
    void deleteSerie(SerieDatabase serie);

    @Query("DELETE FROM series")
    void deleteAllSeries();

    @Query("SELECT * FROM SERIES where id = :id")
    LiveData<SerieDatabase> getSerieById(int id);
}
