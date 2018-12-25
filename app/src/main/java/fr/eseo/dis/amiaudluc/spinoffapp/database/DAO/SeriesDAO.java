package fr.eseo.dis.amiaudluc.spinoffapp.database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;

/**
 * Created by lucasamiaud on 08/03/2018.
 */

@Dao
public interface SeriesDAO {

    @Insert
    public void insertSerie(Serie serie);

    @Query("SELECT * FROM series")
    List<Serie> getAll();

    @Query("SELECT id FROM series")
    List<Integer> getAllIds();

    @Query("SELECT * FROM series where name LIKE  :firstName")
    Serie findByTitle(String firstName);

    @Query("SELECT COUNT(*) from series")
    int countSeries();

    @Query("SELECT SUM(vote_count) from SERIES")
    int countVotesSeries();

    @Insert
    void insertAll(Serie... series);

    @Delete
    void deleteSerie(Serie serie);

    @Query("DELETE FROM series")
    void deleteAllSeries();

    @Query("SELECT * FROM SERIES where id = :id")
    Serie getSerieById(int id);
}
