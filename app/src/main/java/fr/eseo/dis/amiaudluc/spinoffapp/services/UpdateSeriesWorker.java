package fr.eseo.dis.amiaudluc.spinoffapp.services;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

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
        SerieRepository serieRepository = new SerieRepository((Application) getApplicationContext());
        serieRepository.updateAllSeriesSync();
        return Result.success();
    }
}
