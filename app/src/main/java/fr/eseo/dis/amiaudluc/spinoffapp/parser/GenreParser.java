package fr.eseo.dis.amiaudluc.spinoffapp.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.eseo.dis.amiaudluc.spinoffapp.model.Genre;

/**
 * Created by lucasamiaud on 23/03/2018.
 */

public class GenreParser {

    /**
     * get episodes from string data
     * @param data
     * @return
     */
    public static Genre singleGenreParser(String data){
        Genre genre = new Genre();

        try {
            JSONObject c = new JSONObject(data);

            genre.setId(c.getInt("id"));
            genre.setName(c.getString("name"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return genre;
    }

    /**
     * get genres from a media
     * @param data
     * @return
     */
    static ArrayList<Genre> multiGenresParser(String data){
        ArrayList<Genre> genres = new ArrayList<>();
        try {

            JSONArray object = new JSONArray(data);


            for (int i = 0; i < object.length(); i++) {
                JSONObject c = object.getJSONObject(i);

                    genres.add(singleGenreParser(c.toString()));
            }

        } catch (JSONException e) {
            Log.e("JSON Parsing error in genre",e.getMessage());
        }

        return genres;
    }

    static ArrayList<Genre> multiGenresParserForFilter(String data){
        ArrayList<Genre> genres = new ArrayList<>();
        try {

            JSONObject object = new JSONObject(data);

            return multiGenresParser(object.getString("genres"));

        } catch (JSONException e) {
            Log.e("JSON Parsing error in genre for filter",e.getMessage());
        }

        return genres;
    }
}
