package fr.eseo.dis.amiaudluc.spinoffapp.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.eseo.dis.amiaudluc.spinoffapp.model.Video;

/**
 * Created by lucasamiaud on 22/03/2018.
 */

public class VideoParser {

    /**
     * extractVideos
     */
    public static ArrayList<Video> multiVideosParser(String data){
        ArrayList<Video> videos = new ArrayList<>();

        try {
            JSONArray object = new JSONArray(data);
            for (int i = 0; i < object.length(); i++) {
                JSONObject c = object.getJSONObject(i);
                videos.add(singleVideoParser(c.toString()));
            }
        }catch (JSONException e){
            Log.e("Json Parsing Error in videos",e.getMessage());
        }

        return videos;
    }

    /**
     * getVideo
     * @param data
     * @return
     */
    public static Video singleVideoParser(String data){
        Video video = new Video();
        try{
            JSONObject c = new JSONObject(data);

            if (c.has("id")){
                video.setId(c.getString("id"));
            }
            if(c.has("key")){
                video.setKey(c.getString("key"));
            }
            if(c.has("type")){
                video.setType(c.getString("type"));
            }
            if(c.has("site")){
                video.setSite(c.getString("site"));
            }
        } catch (JSONException e) {
            Log.e("Json Parsing Error in video",e.getMessage());
        }

        return video;
    }
}
