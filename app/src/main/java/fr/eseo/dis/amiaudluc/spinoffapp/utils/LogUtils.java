package fr.eseo.dis.amiaudluc.spinoffapp.utils;

import android.util.Log;

import fr.eseo.dis.amiaudluc.spinoffapp.BuildConfig;

/**
 * Created by lucasamiaud on 05/04/2018.
 */

public class LogUtils {

    public static final String DEBUG_TAG = "DEBUG_TAG";

    public static void d(String tag, String message){
        if(BuildConfig.DEBUG){
            Log.d(tag,message);
        }
    }

    public static void e(String tag, String message){
        if(BuildConfig.DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void e(String tag, String message, Throwable throwable){
        if(BuildConfig.DEBUG) {
            Log.e(tag, message, throwable);
        }
    }
}
