package fr.eseo.dis.amiaudluc.spinoffapp.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.release.SerieRelease;

/**
 * Centralises the serie release notifications: channel registration and per-serie grouped
 * notifications with a deep link to the serie screen.
 * <p>
 * Single responsibility: it only knows how to turn {@link SerieRelease} values into Android
 * notifications. Whether a release deserves a notification is decided by the release detector.
 */
public class NotificationUtils {

    public static final String RELEASE_CHANNEL_ID = "serie_releases";
    public static final String GROUP_PREFIX = "serie_release_";

    /** Picasso only keeps a weak reference on its targets: keep them alive until they answer. */
    private static final Set<Target> PENDING_POSTERS = Collections.synchronizedSet(new HashSet<>());

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
     * Publish one notification per released serie. Guards the runtime permission itself so callers
     * (background worker, screens) cannot forget it.
     *
     * @return {@code true} when at least one notification was posted
     */
    public boolean notifyReleases(List<SerieRelease> releases) {
        if (releases == null || releases.isEmpty() || !canPostNotifications(context)) {
            return false;
        }
        for (SerieRelease release : releases) {
            postRelease(release, null);
            loadPoster(release);
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

    private void postRelease(SerieRelease release, Bitmap poster) {
        // Permission checked inline so the static analysis can see the guard next to notify().
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (!notificationManager.areNotificationsEnabled()) {
            return;
        }
        String content = computeContentText(release);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, RELEASE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_tv)
                .setContentTitle(release.serieName())
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setAutoCancel(true)
                .setGroup(GROUP_PREFIX + release.serieId())
                .setContentIntent(buildSerieIntent(release.serieId()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (poster != null) {
            builder.setLargeIcon(poster)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(poster)
                            .setBigContentTitle(release.serieName())
                            .setSummaryText(computeContentText(release)));
        }
        notificationManager.notify(release.serieId(), builder.build());
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

    /**
     * Upgrade the notification with the serie poster once the download completes.
     */
    private void loadPoster(SerieRelease release) {
        if (release.posterPath() == null) {
            return;
        }
        String url = context.getString(R.string.base_url_poster_185) + release.posterPath();
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                PENDING_POSTERS.remove(this);
                postRelease(release, bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                PENDING_POSTERS.remove(this);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                // Nothing to prepare.
            }
        };
        PENDING_POSTERS.add(target);
        Picasso.get().load(url).into(target);
    }
}
