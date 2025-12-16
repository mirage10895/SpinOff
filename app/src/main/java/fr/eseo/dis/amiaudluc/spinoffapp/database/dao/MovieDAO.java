package fr.eseo.dis.amiaudluc.spinoffapp.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;


/**
 * Created by lucasamiaud on 29/12/2017.
 */

@Dao
public interface MovieDAO {

    @Insert
    void insertMovie(MovieDatabase movie);

    @Query("SELECT * FROM MOVIES")
    LiveData<List<MovieDatabase>> getAll();

    @Query("DELETE FROM MOVIES WHERE id = :id")
    void deleteMovieById(Integer id);

    @Query("SELECT * FROM movies where id = :id")
    LiveData<MovieDatabase> fetchMovieById(int id);
}
