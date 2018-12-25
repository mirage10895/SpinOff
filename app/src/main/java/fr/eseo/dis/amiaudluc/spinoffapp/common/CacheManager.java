package fr.eseo.dis.amiaudluc.spinoffapp.common;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by lucasamiaud on 01/03/2018.
 */

public class CacheManager {

    public static final String CORE_POP_MOV = "spinoffapp_popular_movie.json";
    public static final String CORE_POP_SER = "spinoffapp_popular_serie.json";
    public static final String CORE_TOP_MOV = "spinoffapp_top_rated_movie.json";
    public static final String CORE_TOP_SER = "spinoffapp_top_rated_serie.json";
    public static final String CORE_OA_MOV = "spinoffapp_latest_movie.json";
    public static final String CORE_OA_SER = "spinoffapp_latest_serie.json";

    // The list of entities registered in the core data
    public static final String[] CORE_ENTITIES = {CORE_POP_MOV,CORE_POP_SER,CORE_TOP_MOV,CORE_TOP_SER, CORE_OA_MOV, CORE_OA_SER};

    private static CacheManager instance;

    public static synchronized CacheManager getInstance() {
        if (instance == null) {
            instance = new CacheManager();
        }
        return instance;
    }

    /**
     * This function remove all the content of the cache files.
     *
     * @param cxt : The current context.
     */
    public void removeAll(Context cxt) {
        for (String entity : CacheManager.CORE_ENTITIES) {
            write(cxt, entity, "");
        }
    }

    /**
     * This function extract the string contained in the cache file.
     *
     * @param cxt        : The current context.
     * @param coreEntity : The name of the cache file where the data are going to be extracted.
     * @return
     */
    public String read(Context cxt, String coreEntity) {
        String result = "";
        File cacheFile = new File(cxt.getCacheDir() + "/" + coreEntity);
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(cacheFile);
            result = convertStreamToString(fileInputStream);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * This function will write the data passed in arguments in the cache file.
     *
     * @param cxt        : The current context.
     * @param coreEntity : The name of the file where the data are going to be written.
     * @param data       : The json string received.
     */
    public void write(Context cxt, String coreEntity, String data) {
        File cacheFile = new File(cxt.getCacheDir() + "/" + coreEntity);
        try {
            try {
                try (FileOutputStream stream = new FileOutputStream(cacheFile)) {
                    try {
                        stream.write(data.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function will write the data passed in arguments in the cache file.
     *
     * @param cxt        : The current context.
     * @param coreEntity : The name of the file where the data are going to be written.
     * @param data       : The json string received.
     */
    public void writeInputStream(Context cxt, String coreEntity, InputStream data) {
        File cacheFile = new File(cxt.getCacheDir() + "/" + coreEntity);
        try {
            try {
                try (FileOutputStream stream = new FileOutputStream(cacheFile)) {
                    try {
                        byte[] buffer = new byte[data.available()];
                        stream.write(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
