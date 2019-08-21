package fr.eseo.dis.amiaudluc.spinoffapp.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.eseo.dis.amiaudluc.spinoffapp.model.Artist;

/**
 * Created by lucasamiaud on 22/03/2018.
 */

class ArtistParser {

    /**
     * get all artists
     * @param data
     * @return
     */
    public static ArrayList<Artist> multiArtistsParser(String data){
        ArrayList<Artist> artists = new ArrayList<>();
        try{
            JSONArray object = new JSONArray(data);
            for (int i = 0; i < object.length(); i++) {
                JSONObject c = object.getJSONObject(i);

                artists.add(singleArtistParser(c.toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Json Parsing Error in artists",e.getMessage());
        }

        return  artists;
    }

    /**
     * get one artist
     * @param data
     * @return
     */
    public static Artist singleArtistParser(String data){
        Artist artist = new Artist();
        try{
            JSONObject c = new JSONObject(data);

            if (c.has("id")){
                artist.setId(c.getInt("id"));
            }
            if(c.has("character")){
                artist.setCharacter(c.getString("character"));
            }
            if(c.has("name")){
                artist.setName(c.getString("name"));
            }
            if(c.has("profile_path")){
                artist.setProfilePath(c.getString("profile_path"));
            }
            if(c.has("tv_credits")){
                //artist.setSeries(WebServiceParser.multiSeriesParser(c.getString("tv_credits")));
            }
            if(c.has("movie_credits")){
                //artist.setMovies(MovieParser.multiMoviesParser((c.getString("movie_credits"))));
            }
            if(c.has("place_of_birth")){
                artist.setPlaceOfBirth(c.getString("place_of_birth"));
            }
            if(c.has("biography")){
                artist.setBiography(c.getString("biography"));
            }
            if(c.has("department")){
                artist.setDepartment(c.getString("department"));
            }
            if(c.has("job")){
                artist.setJob(c.getString("job"));
            }
        } catch (JSONException e) {
            e.printStackTrace();Log.e("Json Parsing Error in artist ",e.getMessage());
        }

        return artist;
    }
}
