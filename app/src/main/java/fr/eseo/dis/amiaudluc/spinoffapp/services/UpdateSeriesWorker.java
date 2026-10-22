package fr.eseo.dis.amiaudluc.spinoffapp.services;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.Duration;
import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.repositories.SerieRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.LogUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.NotificationPreferences;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.NotificationUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.release.SerieRelease;

/**
 * Periodic series synchronisation, split into two frequencies so the heavy metadata refresh does
 * not have to run every day:
 * <ul>
 *   <li>a daily <strong>release check</strong> using the lightweight TMDB payload, which detects
 *       new episodes and seasons and feeds the notifications;</li>
 *   <li>a <strong>full metadata refresh</strong> (credits, videos, runtime…) throttled by
 *       {@link #METADATA_MAX_AGE}, because it is one large request per serie.</li>
 * </ul>
 */
public class UpdateSeriesWorker extends Worker {

    /** The worker runs daily: the release check never has to look further back. */
    public static final Duration WORK_INTERVAL = Duration.ofDays(1);
    /** A serie is only fully refreshed when its metadata is older than this. */
    public static final Duration METADATA_MAX_AGE = Duration.ofDays(10);

    private static final String TAG = "UpdateSeriesWorker";

    public UpdateSeriesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Since we are in a background thread provided by WorkManager,
        // we can and should use synchronous calls.
        SerieRepository serieRepository = SerieRepository.getRepository(getApplicationContext());
        serieRepository.updateAllSeriesSync(METADATA_MAX_AGE);
        notifyReleases(serieRepository.syncReleasesSync());
        return Result.success();
    }

    private void notifyReleases(List<SerieRelease> releases) {
        NotificationPreferences preferences = new NotificationPreferences(getApplicationContext());
        if (!preferences.areReleasesEnabled()) {
            return;
        }
        NotificationUtils.getInstance(getApplicationContext()).notifyReleases(releases);
        LogUtils.d(TAG, releases.size() + " release(s) detected");
    }
}
