package fr.eseo.dis.amiaudluc.spinoffapp.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;

/**
 * Created by lucasamiaud on 22/03/2018.
 */

public class MovieParser {

    private static final String RESULTS = "results";
    private static final String TAG = MovieParser.class.getSimpleName();

    /**
     * Extract the movies from the JSON as String.
     *
     * @param data : The JSON String.
     * @return List<Movie> a list of projects.
     */
    public static ArrayList<Movie> multiMoviesParser(String data) {
        ArrayList<Movie> movieList = new ArrayList<>();

        try {
            JSONObject object = new JSONObject(data);

            JSONArray results;
            if(object.has(RESULTS)) {
                results = object.getJSONArray(RESULTS);
            }else if(object.has("cast")){
                results = object.getJSONArray("cast");
            }else{
                results = object.getJSONArray("");
            }
            for (int i = 0; i < results.length(); i++) {
                JSONObject c = results.getJSONObject(i);
                // adding movie to the list
                movieList.add(singleMovieParser(c.toString()));
            }
        } catch (JSONException e) {
            Log.e(TAG,"Json parsing error: " + e.getMessage());
            movieList = new ArrayList<>();
        }
        return movieList;
    }

    /**
     * extract a single movie from json data
     * @param data
     * @return Movie
     */
    public static Movie singleMovieParser(String data){
        Movie movie = new Movie();
        try {
            JSONObject c = new JSONObject(data);

            movie.setVoteCount(c.getInt("vote_count"));
            movie.setId(c.getInt("id"));
            movie.setVideo(c.getBoolean("video"));
            movie.setVoteAverage(c.getDouble("vote_average"));
            movie.setTitle(c.getString("title"));
            movie.setPopularity(c.getDouble("popularity"));
            movie.setPosterPath(c.getString("poster_path"));
            movie.setOriginalLanguage(WebServiceParser.getLanguage(c.getString("original_language")));
            movie.setOriginalTitle(c.getString("original_title"));
            movie.setBackdropPath(c.getString("backdrop_path"));
            movie.setAdult(c.getBoolean("adult"));
            movie.setOverview(c.getString("overview"));

            if(c.has("runtime")){
                movie.setRuntime(c.getInt("runtime"));
            }
            if(c.has("genres")){
                movie.setGenres(WebServiceParser.multiGenresParser(c.getString("genres")));
            }
            if(c.has("budget")){
                //movie.setBudget(c.getInt("budget"));
            }
            if(c.has("credits")){
                JSONObject castos = new JSONObject(c.getString("credits"));
                //movie.setCast(WebServiceParser.multiArtistsParser(castos.getString("cast")));
                //movie.setCrew(WebServiceParser.multiArtistsParser(castos.getString("crew")));
            }
            if (c.has("videos")){
                JSONObject videos = new JSONObject(c.getString("videos"));
                //movie.setVideos(WebServiceParser.multiVideosParser(videos.getString(RESULTS)));
            }
            if (c.has("release_date")){
                movie.setReleaseDate(DateUtils.getDateFromString(c.getString("release_date"),DateUtils.CLASSIC_DATE));
            }

        } catch (JSONException e) {
            Log.e("JSON Parsing Error in movie:",e.getMessage());
        }

        return movie;
    }
}
