package fr.eseo.dis.amiaudluc.spinoffapp.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.MovieDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.tmdb.ApiRepository;

public class MovieRepository {
    private static MovieRepository INSTANCE = null;

    private final MovieDAO movieDao;
    private final ApiRepository apiRepository;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public static MovieRepository getRepository(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new MovieRepository(context);
        }
        return INSTANCE;
    }

    private MovieRepository(Context context) {
        AppDatabase db = AppDatabase.getAppDatabase(context);
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

    public void updateAllMoviesSync(Duration notSyncSince) {
        List<MovieDatabase> movies = this.movieDao.getAllSync();
        for (MovieDatabase movieDatabase : movies) {
            if (
                    movieDatabase.getLastSynchronisationTime() == null
                    || movieDatabase.getLastSynchronisationTime().isBefore(Instant.now().minus(notSyncSince))
            ) {
                MovieDatabase updated = computeMovieDatabaseSync(movieDatabase.getId(), movieDatabase.isWatched());
                if (updated != null) {
                    this.movieDao.updateMovie(updated);
                }
            }
        }
    }

    private MovieDatabase computeMovieDatabaseSync(int movieId, boolean isWatched) {
        Optional<Movie> apiMovie = this.apiRepository.getMovieByIdSync(movieId);
        if (apiMovie.isEmpty()) {
            return null;
        }
        MovieDatabase movieDatabase = new MovieDatabase();
        movieDatabase.setId(apiMovie.get().getId());
        movieDatabase.setTitle(apiMovie.get().getOriginalTitle());
        movieDatabase.setPosterPath(apiMovie.get().getPosterPath());
        movieDatabase.setRuntime(apiMovie.get().getRuntime());
        movieDatabase.setWatched(isWatched);
        movieDatabase.setGenres(ApiRepository.formatGenres(apiMovie.get().getGenres()));
        movieDatabase.setReleaseDate(apiMovie.get().getReleaseDate());
        movieDatabase.setLastSynchronisationTime(Instant.now());
        return movieDatabase;
    }
}
