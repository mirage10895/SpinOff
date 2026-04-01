package fr.eseo.dis.amiaudluc.spinoffapp.services;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import fr.eseo.dis.amiaudluc.spinoffapp.repositories.SerieRepository;

public class UpdateSeriesWorker extends Worker {

    public UpdateSeriesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Since we are in a background thread provided by WorkManager,
        // we can and should use synchronous calls.
        SerieRepository serieRepository = SerieRepository.getRepository(getApplicationContext());
        serieRepository.updateAllSeriesSync(Duration.of(10, ChronoUnit.DAYS));
        return Result.success();
    }
}
