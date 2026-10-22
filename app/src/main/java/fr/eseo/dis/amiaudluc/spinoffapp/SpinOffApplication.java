package fr.eseo.dis.amiaudluc.spinoffapp;

import android.app.Application;

import fr.eseo.dis.amiaudluc.spinoffapp.utils.NotificationUtils;

/**
 * Application entry point. Registers the global setup that must exist before any screen or
 * background worker runs, currently the notification channel of the serie releases.
 */
public class SpinOffApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationUtils.getInstance(this).ensureReleaseChannel();
    }
}
