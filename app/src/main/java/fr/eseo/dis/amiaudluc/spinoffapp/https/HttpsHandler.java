package fr.eseo.dis.amiaudluc.spinoffapp.https;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import fr.eseo.dis.amiaudluc.spinoffapp.common.Tools;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.ConstUtils;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

public class HttpsHandler {

        private static final String TAG = HttpsHandler.class.getSimpleName();

        public HttpsHandler() {
        }

        /**
         * This function will verify if the user is connected to internet.
         *
         * @param ctx : The current context.
         * @return Boolean which describes whether the user is well connected.
         */
        public boolean isOnline(Context ctx) {
            ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = null;
            if (cm != null) {
                netInfo = cm.getActiveNetworkInfo();
            }
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }

        private InputStream makeServiceCallStream(String type,String id, String args) {
            InputStream in = null;
            String reqUrl = "https://api.themoviedb.org/3/"+type+"/"+id+"?api_key="+ ConstUtils.API_KEY+args;
            try {

                URL url = new URL(reqUrl);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setRequestProperty("Content-MovieType", "application/json");
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() > 400){
                    in = new BufferedInputStream(conn.getErrorStream());
                }else {
                    // read the response
                    in = new BufferedInputStream(conn.getInputStream());
                }
                //response = convertStreamToString(in);
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
            return in;
        }

        public String makeServiceCall(String type,String id, String args){
            InputStream result = makeServiceCallStream(type,id,args);
            if(result == null){
                return "";
            }else {
                return Tools.convertStreamToString(result);
            }
        }
}
