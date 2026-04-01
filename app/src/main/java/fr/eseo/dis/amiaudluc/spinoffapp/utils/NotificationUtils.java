package fr.eseo.dis.amiaudluc.spinoffapp.utils;

import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import fr.eseo.dis.amiaudluc.R;

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
