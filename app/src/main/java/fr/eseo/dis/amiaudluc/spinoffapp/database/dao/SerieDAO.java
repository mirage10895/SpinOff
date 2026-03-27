package fr.eseo.dis.amiaudluc.spinoffapp.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;

/**
 * Created by lucasamiaud on 08/03/2018.
 */

@Dao
public interface SerieDAO {
    @Insert
    void insertSerie(SerieDatabase serie);
    @Query("SELECT * FROM serie")
    LiveData<List<SerieDatabase>> getAll();
    @Query("SELECT * FROM serie")
    List<SerieDatabase> getAllSync();
    @Query("DELETE FROM serie where id = :id")
    void deleteSerieById(Integer id);
    @Query("update serie set watched = not watched where id = :id")
    void toggleSerieIsWatched(int id);
    @Update
    void updateSerie(SerieDatabase serie);
}
