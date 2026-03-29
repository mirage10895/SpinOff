package fr.eseo.dis.amiaudluc.spinoffapp.services;

import android.content.Context;

import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Genre;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.ApiRepository;

/**
 * Use this to reinject items idatabase if columns were to evolve
 * the call DatabaseMigrationWorker.enqueue(this) in MainActivity.onCreate()
 */
public class DatabaseMigrationWorker extends Worker {

    public DatabaseMigrationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
        ApiRepository repository = ApiRepository.getInstance();

        // Migrate Movies
        List<MovieDatabase> movies = db.moviesDAO().getAllSync();
        for (MovieDatabase movieDb : movies) {
            repository.getMovieByIdSync(movieDb.getId()).ifPresent(movieApi -> {
                movieDb.setGenres(formatGenres(movieApi.getGenres()));
                movieDb.setReleaseDate(movieApi.getReleaseDate());
                db.moviesDAO().updateMovie(movieDb);
            });
        }

        // Migrate Series
        List<SerieDatabase> series = db.serieDAO().getAllSync();
        for (SerieDatabase serieDb : series) {
            repository.getSerieByIdSync(serieDb.getId()).ifPresent(serieApi -> {
                serieDb.setGenres(formatGenres(serieApi.getGenres()));
                serieDb.setSeasonCount(serieApi.getNumberOfSeasons());
                serieDb.setEpisodeCount(serieApi.getNumberOfEpisodes());
                serieDb.setFirstAirDate(serieApi.getFirstAirDate());
                db.serieDAO().updateSerie(serieDb);
            });
        }

        return Result.success();
    }

    private String formatGenres(List<Genre> genres) {
        if (genres == null) {
            return null;
        }
        return genres.stream().map(Genre::getName).collect(Collectors.joining(","));
    }

    public static void enqueue(Context context) {
        WorkManager.getInstance(context).enqueue(
                new OneTimeWorkRequest.Builder(DatabaseMigrationWorker.class).build()
        );
    }
}
