package fr.eseo.dis.amiaudluc.spinoffapp.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;

/**
 * Created by lucasamiaud on 22/03/2018.
 */

class SeasonParser {

    private static final String RESULTS = "results";
    private static final String TAG = SeasonParser.class.getSimpleName();

    /**
     * get the seasons objects
     * @param seasonsString
     * @return
     */
    public static ArrayList<Season> multiSeasonsParser(String seasonsString, int ID){
        ArrayList<Season> seasons = new ArrayList<>();

        try{
            JSONArray object = new JSONArray(seasonsString);
            for (int i = 0; i < object.length(); i++) {
                JSONObject c = object.getJSONObject(i);

                Season season = SeasonParser.singleSeasonParser(c.toString());
                season.setSerieId(ID);
                seasons.add(season);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Json Parsing Error in extractSeasons ",e.getMessage());
        }

        return seasons;
    }

    /**
     * get Single season
     * @param data
     * @return
     */
    public static Season singleSeasonParser(String data){
        Season season = new Season();
        //Log.e("OccurenceSeason",data);
        try{
            JSONObject c = new JSONObject(data);

            if (c.has("id")){
                season.setId(c.getInt("id"));
            }
            if(c.has("episode_count")) {
                season.setEpisodeCount(c.getInt("episode_count"));
            }
            if(c.has("poster_path")) {
                season.setPosterPath(c.getString("poster_path"));
            }
            if (c.has("season_number")){
                season.setSeasonNumber(c.getInt("season_number"));
            }
            if(c.has("name")){
                season.setName(c.getString("name"));
            }
            if(c.has("overview")){
                season.setOverview(c.getString("overview"));
            }
            if(c.has("episodes")){
                season.setEpisodes(WebServiceParser.multiEpisodesParser(c.getString("episodes")));
            }
            if(c.has("credits")){
                JSONObject castos = new JSONObject(c.getString("credits"));
                //season.setCast(WebServiceParser.multiArtistsParser(castos.getString("cast")));
            }
            if (c.has("videos")){
                JSONObject videos = new JSONObject(c.getString("videos"));
                //season.setVideos(WebServiceParser.multiVideosParser(videos.getString(RESULTS)));
            }
            if (c.has("air_date")) {
                season.setAirDate(DateUtils.getDateFromString(c.getString("air_date"),DateUtils.CLASSIC_DATE));
            }
        } catch (JSONException e) {
            e.printStackTrace();Log.e("Json Parsing Error in getSeason ",e.getMessage());
        }
        return season;
    }

    /**
     * Maethod to get all the seasons when adding a serie to the DB
     * @param data
     * @param numbers
     * @return
     */
    public static HashMap<Integer,Season> fullSingleSeasonParser(String data, List<String> numbers){
        //ArrayList<Season> seasons = new ArrayList<>();
        HashMap<Integer,Season> seasons = new HashMap<>();

        try {
            JSONObject object = new JSONObject(data);
            for (String i : numbers){
                String season = object.getString("season/"+i);
                Season seasonObj = SeasonParser.singleSeasonParser(season);
                if (seasonObj != null){
                    seasons.put(Integer.parseInt(i),seasonObj);
                }
            }
        } catch (JSONException e) {
            Log.e("JSON parsing error in DB Seasons:",e.getMessage());
            e.printStackTrace();
        }

        return seasons;
    }

}
