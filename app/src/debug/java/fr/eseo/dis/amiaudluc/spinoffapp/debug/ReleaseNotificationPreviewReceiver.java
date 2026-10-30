package fr.eseo.dis.amiaudluc.spinoffapp.debug;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.LogUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.NotificationUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.release.SerieRelease;

/**
 * Debug-only trigger that posts fake release notifications through the production
 * {@link NotificationUtils}, to check the look, the poster download and the summary bundling on an
 * emulator.
 * <p>
 * It lives in the {@code debug} source set ({@code app/src/debug}) and is therefore absent from
 * every release build. The series of the library are reused when there are any, so the poster and
 * the name are real; otherwise a fallback serie and poster are used.
 * <p>
 * The debug build carries the {@code .debug} application id suffix. Examples:
 * <pre>
 * adb shell am broadcast -n fr.eseo.dis.amiaudluc.spinoffapp.debug/fr.eseo.dis.amiaudluc.spinoffapp.debug.ReleaseNotificationPreviewReceiver
 * adb shell am broadcast -n ... --ei count 3          # one real serie + two fallbacks, shows the summary
 * adb shell am broadcast -n ... --es kind season      # season flavour instead of episode
 * adb shell am broadcast -n ... --es name "My serie" --es poster /1XS1oqL89opfnbLl8WnZY1O1uJx.jpg
 * </pre>
 * The notification permission must be granted (library settings → New releases) and the debug app
 * must be installed on the emulator.
 */
public class ReleaseNotificationPreviewReceiver extends BroadcastReceiver {

    public static final String EXTRA_COUNT = "count";
    public static final String EXTRA_KIND = "kind";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_POSTER = "poster";

    public static final String KIND_EPISODE = "episode";
    public static final String KIND_SEASON = "season";

    private static final String TAG = "ReleasePreview";
    private static final String FALLBACK_NAME = "Preview serie";
    private static final String FALLBACK_POSTER = "/1XS1oqL89opfnbLl8WnZY1O1uJx.jpg";

    @Override
    public void onReceive(Context context, Intent intent) {
        PendingResult pendingResult = goAsync();
        Context appContext = context.getApplicationContext();
        // notifyReleases() downloads the poster synchronously: it must stay off the main thread.
        new Thread(() -> {
            try {
                preview(appContext, intent);
            } finally {
                pendingResult.finish();
            }
        }, "release-notification-preview").start();
    }

    private void preview(Context context, Intent intent) {
        if (!NotificationUtils.canPostNotifications(context)) {
            LogUtils.d(TAG, "Notifications are disabled: enable the switch in the library settings.");
            return;
        }
        int count = Math.max(1, intent.getIntExtra(EXTRA_COUNT, 1));
        boolean season = KIND_SEASON.equals(intent.getStringExtra(EXTRA_KIND));
        List<SerieRelease> releases = buildReleases(context, intent, count, season);

        NotificationUtils.getInstance(context).ensureReleaseChannel();
        NotificationUtils.getInstance(context).notifyReleases(releases);
        LogUtils.d(TAG, releases.size() + " preview release(s) posted");
    }

    private List<SerieRelease> buildReleases(Context context, Intent intent, int count, boolean season) {
        List<SerieDatabase> library = AppDatabase.getAppDatabase(context).serieDAO().getAllSync();
        List<SerieRelease> releases = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            SerieDatabase serie = i < library.size() ? library.get(i) : null;
            int serieId = serie != null ? serie.getId() : 1000 + i;
            String name = nameOf(intent, serie);
            String poster = posterOf(intent, serie);
            releases.add(season
                    ? SerieRelease.season(serieId, name, poster, 3, LocalDate.now())
                    : SerieRelease.episode(serieId, name, poster, 3, 4, "The Long Night",
                            LocalDate.now()));
        }
        return releases;
    }

    private String nameOf(Intent intent, SerieDatabase serie) {
        if (intent.hasExtra(EXTRA_NAME)) {
            return intent.getStringExtra(EXTRA_NAME);
        }
        return serie != null ? serie.getName() : FALLBACK_NAME;
    }

    private String posterOf(Intent intent, SerieDatabase serie) {
        if (intent.hasExtra(EXTRA_POSTER)) {
            return intent.getStringExtra(EXTRA_POSTER);
        }
        return serie != null ? serie.getPosterPath() : FALLBACK_POSTER;
    }
}
