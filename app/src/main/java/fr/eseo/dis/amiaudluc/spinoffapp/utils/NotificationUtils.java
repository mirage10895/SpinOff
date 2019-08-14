package fr.eseo.dis.amiaudluc.spinoffapp.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import fr.eseo.dis.amiaudluc.spinoffapp.R;

/**
 * Created by lucasamiaud on 05/04/2018.
 */

public class NotificationUtils {
    private static NotificationUtils notificationHandler;
    private static NotificationManager notificationManager;

    public static NotificationUtils getInstance(Context context){
        if(notificationHandler == null){
            notificationHandler = new NotificationUtils();
            notificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationHandler;
    }

    public void createNotification(Context context, String notificationTitle, String notificationText){

    }

}
