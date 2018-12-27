package fr.eseo.dis.amiaudluc.spinoffapp.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Episode;

/**
 * Created by lucasamiaud on 22/03/2018.
 */

public class EpisodeParser {

    /**
     * get episodes from string data
     * @param data
     * @return
     */
    public static ArrayList<Episode> multiEpisodesParser(String data){
        ArrayList<Episode> episodes = new ArrayList<>();

        try{
            JSONArray object = new JSONArray(data);
            for (int i = 0; i < object.length(); i++) {
                JSONObject c = object.getJSONObject(i);

                Episode episode = EpisodeParser.singleEpisodeParser(c.toString());
                episodes.add(episode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Json Parsing Error in episodes: ",e.getMessage());
        }

        return episodes;
    }

    public static Episode singleEpisodeParser(String data){
        Episode episode = new Episode();
        try{
            JSONObject c = new JSONObject(data);

            if (c.has("id")){
                episode.setId(c.getInt("id"));
            }
            if(c.has("episode_number")) {
                episode.setEpisodeNumber(c.getInt("episode_number"));
            }
            if(c.has("still_path")){
                episode.setStillPath(c.getString("still_path"));
            }
            if(c.has("name")){
                episode.setName(c.getString("name"));
            }
            if(c.has("overview")){
                episode.setOverview(c.getString("overview"));
            }
            if(c.has("vote_average")){
                episode.setVoteAvg(c.getDouble("vote_average"));
            }
            if(c.has("vote_count")){
                episode.setVoteCount(c.getInt("vote_count"));
            }
            if (c.has("guest_stars")){
                episode.setGuestStars(WebServiceParser.multiArtistsParser(c.getString("guest_stars")));
            }
            episode.setSeasonNumber(c.getInt("season_number"));
            if (c.has("air_date")) {
                episode.setAirDate(DateUtils.getDateFromString(c.getString("air_date"),DateUtils.CLASSIC_DATE));
            }
        } catch (JSONException e) {
            e.printStackTrace();Log.e("Json Parsing Error in episode ",e.getMessage());
        }

        return episode;
    }
}
