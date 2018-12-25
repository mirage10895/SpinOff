package fr.eseo.dis.amiaudluc.spinoffapp.database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;

/**
 * Created by lucasamiaud on 15/03/2018.
 */

@Dao
public interface SeasonDAO {

    @Insert
    void insertSeason(Season season);

    @Query("SELECT * FROM seasons")
    List<Season> getAll();

    @Query("SELECT id FROM seasons")
    List<Integer> getAllIds();

    @Query("SELECT * FROM seasons where name LIKE  :firstName")
    Season findByTitle(String firstName);

    @Query("SELECT COUNT(*) from seasons")
    int countSeasons();

    @Insert
    void insertAll(Season ... seasons);

    @Delete
    void deleteSeason(Season season);

    @Query("DELETE FROM seasons")
    void deleteAllSeasons();

    @Query("SELECT * FROM SEASONS where id = :id")
    Season getSeasonById(int id);

    @Query("Select * from seasons where serie_id = :id")
    List<Season> getSeasonsBySerieId(int id);
}
