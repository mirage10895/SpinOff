package fr.eseo.dis.amiaudluc.spinoffapp.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;


/**
 * Created by lucasamiaud on 29/12/2017.
 */

@Dao
public interface MovieDAO {
    @Insert
    void insertMovie(MovieDatabase movie);
    @Query("select * from movie")
    LiveData<List<MovieDatabase>> getAll();
    @Query("select * from movie")
    List<MovieDatabase> getAllSync();
    @Query("DELETE FROM movie WHERE id = :id")
    void deleteMovieById(Integer id);
    @Query("select * from movie where id = :id")
    LiveData<MovieDatabase> fetchMovieById(int id);

    @Query("update movie set watched = not watched where id = :id")
    void toggleMovieIsWatched(int id);

    @Update
    void updateMovie(MovieDatabase movie);
}
