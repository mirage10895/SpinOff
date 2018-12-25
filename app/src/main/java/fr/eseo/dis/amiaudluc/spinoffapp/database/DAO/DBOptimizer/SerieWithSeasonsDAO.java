package fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBOptimizer;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

/**
 * Created by lucasamiaud on 11/04/2018.
 */

@Dao
public interface SerieWithSeasonsDAO {

    @Transaction
    @Query("SELECT * from SERIES")
    List<SerieWithSeasons> getSerieWithSeasons();
}
