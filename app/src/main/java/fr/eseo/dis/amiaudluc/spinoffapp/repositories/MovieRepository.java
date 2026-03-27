package fr.eseo.dis.amiaudluc.spinoffapp.repositories;

import android.app.Application;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.MovieDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;

public class MovieRepository {
    private final MovieDAO movieDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public MovieRepository(Application application) {
        AppDatabase db = AppDatabase.getAppDatabase(application);
        movieDao = db.moviesDAO();
    }

    public LiveData<List<MovieDatabase>> fetchAll() {
        return this.movieDao.getAll();
    }

    public void insert(MovieDatabase movie) {
        executor.execute(() -> {
            this.movieDao.insertMovie(movie);
        });
    }

    public void deleteMovieById(int id) {
        executor.execute(() -> {
            this.movieDao.deleteMovieById(id);
        });
    }

    public void toggleMovieIsWatched(int id) {
        executor.execute(() -> {
            this.movieDao.toggleMovieIsWatched(id);
        });
    }
}
