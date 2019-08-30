package fr.eseo.dis.amiaudluc.spinoffapp.database.DAO;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;


/**
 * Created by lucasamiaud on 29/12/2017.
 */

@Dao
public interface MovieDAO {

    @Insert
    void insertMovie(MovieDatabase MOVIES);

    @Query("SELECT * FROM MOVIES")
    LiveData<List<MovieDatabase>> getAll();

    @Query("SELECT id FROM MOVIES")
    LiveData<List<Integer>> getAllIds();

    @Query("SELECT COUNT(*) from MOVIES")
    int countMovies();

    @Query("SELECT SUM(id) from MOVIES")
    int countVotesMovies();

    @Insert
    void insertAll(MovieDatabase... MOVIES);

    @Delete
    void deleteMovie(MovieDatabase MOVIES);

    @Query("DELETE FROM MOVIES WHERE id = :id")
    void deleteMovieById(Integer id);

    @Query("DELETE FROM MOVIES")
    void deleteAllMovies();

}
