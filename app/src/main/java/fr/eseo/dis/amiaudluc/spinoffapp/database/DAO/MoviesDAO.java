package fr.eseo.dis.amiaudluc.spinoffapp.database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;


/**
 * Created by lucasamiaud on 29/12/2017.
 */

@Dao
public interface MoviesDAO {

    @Insert
    void insertMovie(Movie movie);

    @Query("SELECT * FROM movies")
    List<Movie> getAll();

    @Query("SELECT id FROM movies")
    List<Integer> getAllIds();

    @Query("SELECT * FROM movies where title LIKE  :firstName")
    Movie findByTitle(String firstName);

    @Query("SELECT COUNT(*) from movies")
    int countMovies();

    @Query("SELECT SUM(vote_count) from MOVIES")
    int countVotesMovies();

    @Insert
    void insertAll(Movie... movies);

    @Delete
    void deleteMovie(Movie movie);

    @Query("DELETE FROM movies")
    void deleteAllMovies();

}
