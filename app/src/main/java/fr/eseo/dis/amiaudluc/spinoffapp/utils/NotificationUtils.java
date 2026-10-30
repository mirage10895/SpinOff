package fr.eseo.dis.amiaudluc.spinoffapp.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.WorkerThread;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.MainActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.release.SerieRelease;

/**
 * Centralises the serie release notifications: channel registration and one notification per
 * serie, with its poster, deep linking to the serie screen.
 * <p>
 * Single responsibility: it only knows how to turn {@link SerieRelease} values into Android
 * notifications. Whether a release deserves a notification is decided by the release detector.
 * <p>
 * Releases of the same run are bundled behind a summary notification, so a night with several
 * episodes stays a single entry in the notification shade.
 */
public class NotificationUtils {

    public static final String RELEASE_CHANNEL_ID = "serie_releases";
    /**
     * Group shared by every release notification, required for the summary to bundle them.
     */
    public static final String RELEASE_GROUP = "serie_releases";
    /**
     * Notification id of the summary; serie ids are positive so they can never collide.
     */
    private static final int SUMMARY_ID = 0;
    /**
     * A summary only makes sense when it actually bundles several releases.
     */
    private static final int SUMMARY_MIN_RELEASES = 2;

    private static final String TAG = "NotificationUtils";

    private static NotificationUtils notificationHandler;

    private final Context context;
    private final NotificationManagerCompat notificationManager;

    private NotificationUtils(Context context) {
        this.context = context.getApplicationContext();
        this.notificationManager = NotificationManagerCompat.from(this.context);
    }

    public static synchronized NotificationUtils getInstance(Context context) {
        if (notificationHandler == null) {
            notificationHandler = new NotificationUtils(context);
        }
        return notificationHandler;
    }

    /**
     * Create the release channel. Safe to call repeatedly: an existing channel is left untouched
     * so the user preferences survive.
     */
    public void ensureReleaseChannel() {
        NotificationChannel channel = new NotificationChannel(
                RELEASE_CHANNEL_ID,
                context.getString(R.string.notification_channel_releases),
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription(context.getString(R.string.notification_channel_releases_description));
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    /**
     * Publish one notification per released serie, then a summary when several series released at
     * the same time. Guards the runtime permission itself so callers (background worker, screens)
     * cannot forget it.
     * <p>
     * The poster is downloaded before posting, so the notification is complete when it appears
     * instead of being upgraded a few seconds later. This is why the call must stay off the main
     * thread.
     *
     * @return {@code true} when at least one notification was posted
     */
    @WorkerThread
    public boolean notifyReleases(List<SerieRelease> releases) {
        if (releases == null || releases.isEmpty() || !canPostNotifications(context)) {
            return false;
        }
        for (SerieRelease release : releases) {
            post(release.serieId(), buildRelease(release, loadPoster(release)));
        }
        if (releases.size() >= SUMMARY_MIN_RELEASES) {
            post(SUMMARY_ID, buildSummary(releases));
        } else {
            // A summary from a previous run would otherwise linger with stale content.
            notificationManager.cancel(SUMMARY_ID);
        }
        return true;
    }

    /**
     * Whether this app may currently post notifications: the runtime permission (API 33+) and the
     * system-level switch.
     */
    public static boolean canPostNotifications(Context context) {
        return isPermissionGranted(context)
                && NotificationManagerCompat.from(context.getApplicationContext()).areNotificationsEnabled();
    }

    private static boolean isPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private NotificationCompat.Builder buildRelease(SerieRelease release, Bitmap poster) {
        String content = computeContentText(release);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, RELEASE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_release)
                .setContentTitle(release.serieName())
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setAutoCancel(true)
                .setGroup(RELEASE_GROUP)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setContentIntent(buildSerieIntent(release.serieId()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (poster != null) {
            builder.setLargeIcon(poster)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(poster)
                            .setBigContentTitle(release.serieName())
                            .setSummaryText(content));
        }
        return builder;
    }

    /**
     * Bundle of the notifications of the run: tapping it opens the library, where the user can
     * pick the serie. Only the children alert, so the summary never vibrates twice.
     */
    private NotificationCompat.Builder buildSummary(List<SerieRelease> releases) {
        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle()
                .setBigContentTitle(context.getString(R.string.notification_releases_summary_title));
        for (SerieRelease release : releases) {
            style.addLine(release.serieName() + " · " + computeContentText(release));
        }
        return new NotificationCompat.Builder(context, RELEASE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_release)
                .setContentTitle(context.getString(R.string.notification_releases_summary_title))
                .setContentText(context.getResources().getQuantityString(
                        R.plurals.notification_releases_summary, releases.size(), releases.size()))
                .setStyle(style)
                .setAutoCancel(true)
                .setGroup(RELEASE_GROUP)
                .setGroupSummary(true)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setContentIntent(buildLibraryIntent())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    /**
     * Download the serie poster before posting. A failure is not fatal: the notification is
     * posted without it.
     */
    private Bitmap loadPoster(SerieRelease release) {
        if (release.posterPath() == null) {
            return null;
        }
        String url = context.getString(R.string.base_url_poster_500) + release.posterPath();
        try {
            return Picasso.get().load(url).get();
        } catch (IOException e) {
            LogUtils.e(TAG, "Poster download failed for serie " + release.serieId(), e);
            return null;
        }
    }

    private void post(int notificationId, NotificationCompat.Builder builder) {
        // Permission checked inline so the static analysis can see the guard next to notify().
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (!notificationManager.areNotificationsEnabled()) {
            return;
        }
        notificationManager.notify(notificationId, builder.build());
    }

    private String computeContentText(SerieRelease release) {
        if (release.isSeason()) {
            return context.getString(R.string.notification_new_season, release.seasonNumber());
        }
        String label = context.getString(R.string.notification_episode_label,
                release.seasonNumber(), release.episodeNumber());
        return release.episodeName() == null
                ? label
                : context.getString(R.string.notification_new_episode, label, release.episodeName());
    }

    private PendingIntent buildSerieIntent(int serieId) {
        Intent intent = new Intent(context, SerieActivity.class)
                .putExtra(SerieActivity.EXTRA_SERIE_ID, serieId)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(
                context,
                serieId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private PendingIntent buildLibraryIntent() {
        Intent intent = new Intent(context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(
                context,
                SUMMARY_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }
}
