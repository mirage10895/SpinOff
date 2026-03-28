package fr.eseo.dis.amiaudluc.spinoffapp.repositories;

import android.app.Application;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.MovieDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;

public class MovieRepository {
    private final MovieDAO movieDao;
    private final ApiRepository apiRepository;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public MovieRepository(Application application) {
        AppDatabase db = AppDatabase.getAppDatabase(application);
        movieDao = db.moviesDAO();
        this.apiRepository = ApiRepository.getInstance();
    }

    public LiveData<List<MovieDatabase>> fetchAll() {
        return this.movieDao.getAll();
    }

    public void insert(int movieId) {
        executor.execute(() -> {
            MovieDatabase movieDatabase = computeMovieDatabaseSync(movieId, false);
            this.movieDao.insertMovie(movieDatabase);
        });
    }

    public void deleteMovieById(int id) {
        executor.execute(() -> this.movieDao.deleteMovieById(id));
    }

    public void toggleMovieIsWatched(int id) {
        executor.execute(() -> this.movieDao.toggleMovieIsWatched(id));
    }

    private MovieDatabase computeMovieDatabaseSync(int movieId, boolean isWatched) {
        Optional<Movie> apiMovie = this.apiRepository.getMovieByIdSync(movieId);
        if (apiMovie.isEmpty()) {
            return null;
        }
        MovieDatabase movieDatabase = new MovieDatabase();
        movieDatabase.setId(apiMovie.get().getId());
        movieDatabase.setTitle(apiMovie.get().getTitle());
        movieDatabase.setPosterPath(apiMovie.get().getPosterPath());
        movieDatabase.setRuntime(apiMovie.get().getRuntime());
        movieDatabase.setWatched(isWatched);
        movieDatabase.setLastSynchronisationTime(Instant.now());
        return movieDatabase;
    }
}
