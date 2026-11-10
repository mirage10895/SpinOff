package fr.eseo.dis.amiaudluc.spinoffapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * User preferences that are not part of the library content: currently whether the app may post
 * serie release notifications.
 * <p>
 * Kept in {@link SharedPreferences} rather than in Room on purpose: these are device settings,
 * not library data, so they do not belong in a backup and must not touch the library tables.
 */
public class NotificationPreferences {

    private static final String PREFERENCES_NAME = "spinoff_notifications";
    private static final String KEY_RELEASES_ENABLED = "releases_enabled";

    private final SharedPreferences preferences;

    public NotificationPreferences(Context context) {
        this.preferences = context.getApplicationContext()
                .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Releases are notified by default, unless the user turned them off.
     */
    public boolean areReleasesEnabled() {
        return preferences.getBoolean(KEY_RELEASES_ENABLED, true);
    }

    public void setReleasesEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_RELEASES_ENABLED, enabled).apply();
    }
}
