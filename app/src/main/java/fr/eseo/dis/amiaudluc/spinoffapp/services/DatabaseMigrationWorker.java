package fr.eseo.dis.amiaudluc.spinoffapp.services;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.Duration;

import fr.eseo.dis.amiaudluc.spinoffapp.repositories.MovieRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.SerieRepository;

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
        MovieRepository movieRepository = MovieRepository.getRepository(getApplicationContext());
        SerieRepository serieRepository = SerieRepository.getRepository(getApplicationContext());

        // Migrate Movies
        movieRepository.updateAllMoviesSync(Duration.ZERO);
        // Migrate Series
        serieRepository.updateAllSeriesSync(Duration.ZERO);
        return Result.success();
    }

    public static void enqueue(Context context) {
        WorkManager.getInstance(context).enqueue(
                new OneTimeWorkRequest.Builder(DatabaseMigrationWorker.class).build()
        );
    }
}
