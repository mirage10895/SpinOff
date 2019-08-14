package fr.eseo.dis.amiaudluc.spinoffapp.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;

/**
 * Created by lucasamiaud on 22/03/2018.
 */

class SerieParser {

    private static final String RESULTS = "results";
    private static final String TAG = SerieParser.class.getSimpleName();

    /**
     * Extract series from the JSON as String.
     *
     * @param data : The JSON String.
     * @return List<Serie> a list of projects.
     */
    public static ArrayList<Serie> multiSeriesParser(String data) {
        ArrayList<Serie> series = new ArrayList<>();

        try {
            //Log.e("OccurenceExSeries",data);
            JSONObject object = new JSONObject(data);

            JSONArray seriesJson;
            if(object.has(RESULTS)) {
                seriesJson = object.getJSONArray(RESULTS);
            }else if(object.has("cast")){
                seriesJson = object.getJSONArray("cast");
            }else{
                seriesJson = new JSONArray(data);
            }

            for (int i = 0; i < seriesJson.length(); i++) {
                JSONObject c = seriesJson.getJSONObject(i);

                // adding serie to the list
                series.add(SerieParser.singleSerieParser(c.toString()));
            }
        } catch (JSONException e) {
            Log.e(TAG,"Json parsing error: " + e.getMessage());
            series = new ArrayList<>();
        }

        return series;
    }

    /**
     * extract a single serie from json data
     * @param data
     * @return Serie
     */
    public static Serie singleSerieParser(String data){
        //Log.e("OccurenceMovie",data);
        Serie serie = new Serie();

        try {
            JSONObject c = new JSONObject(data);

            serie.setOriginalName(c.getString("original_name"));
            serie.setName(c.getString("name"));
            //serie.setOriginCountry(WebServiceParser.getLanguage(c.getString("origin_country")));
            //serie.setNoteAvg(c.getDouble("vote_average"));
            serie.setVoteCount(c.getInt("vote_count"));
            serie.setId(c.getInt("id"));
            serie.setPopularity(c.getDouble("popularity"));
            serie.setPosterPath(c.getString("poster_path"));
            serie.setOriginalLanguage(WebServiceParser.getLanguage(c.getString("original_language")));
            serie.setBackdropPath(c.getString("backdrop_path"));
            serie.setOverview(c.getString("overview"));
            if (c.has("status")){
                serie.setStatus(c.getString("status"));
            }
            if(c.has("number_of_seasons")){
                serie.setNumberOfSeasons(c.getInt("number_of_seasons"));
            }
            if(c.has("seasons")){
                serie.setSeasons(WebServiceParser.multiSeasonsParser(c.getString("seasons"),serie.getId()));
            }
            if(c.has("networks")) {
                serie.setNetworks(WebServiceParser.getNetworks(c.getString("networks")));
            }
            if (c.has("production_companies")){
                serie.setProductionCompanies(WebServiceParser.getProductionCompanies(c.getString("production_companies")));
            }
            if(c.has("genres")){
                serie.setGenres(WebServiceParser.multiGenresParser(c.getString("genres")));
            }
            if(c.has("created_by")){
                //serie.setCreators(WebServiceParser.multiArtistsParser(c.getString("created_by")));
            }
            if(c.has("episode_run_time")){
                String kk = c.getString("episode_run_time");
            }
            if (c.has("first_air_date")) {
                serie.setFirstAirDate(DateUtils.getDateFromString(c.getString("first_air_date"),DateUtils.CLASSIC_DATE));
            }
            if (c.has("last_air_date")) {
                serie.setLastAirDate(DateUtils.getDateFromString(c.getString("last_air_date"),DateUtils.CLASSIC_DATE));
            }
        }catch (JSONException e){
            Log.e("JSON Parsing Error in serie",e.getMessage());
        }

        return serie;
    }
}
