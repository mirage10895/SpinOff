package fr.eseo.dis.amiaudluc.spinoffapp.parser;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.model.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Genre;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Language;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Network;
import fr.eseo.dis.amiaudluc.spinoffapp.model.ProductionCompany;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Video;

/**
 * Created by lucasamiaud on 22/03/2018.
 */

public class WebServiceParser {

    static Gson gson = new Gson();

    public static Movie singleMovieParser(String data){
        return MovieParser.singleMovieParser(data);
    }

    public static ArrayList<Movie> multiMoviesParser(String data){
        return MovieParser.multiMoviesParser(data);
    }

    public static Artist singleArtistParser(String data){
        return ArtistParser.singleArtistParser(data);
    }

    public static ArrayList<Artist> multiArtistsParser(String data){
        return ArtistParser.multiArtistsParser(data);
    }

    public static Serie singleSerieParser(String data){
        return SerieParser.singleSerieParser(data);
    }

    public static ArrayList<Serie> multiSeriesParser(String data){
        return SerieParser.multiSeriesParser(data);
    }

    public static Season singleSeasonParser(String data){
        return SeasonParser.singleSeasonParser(data);
    }

    public static ArrayList<Season> multiSeasonsParser(String data,int ID){
        return SeasonParser.multiSeasonsParser(data,ID);
    }

    public static Episode singleEpisodeParser(String data){
        return EpisodeParser.singleEpisodeParser(data);
    }

    public static ArrayList<Episode> multiEpisodesParser(String data){
        return EpisodeParser.multiEpisodesParser(data);
    }

    public static Video singleVideoParser(String data){
        return VideoParser.singleVideoParser(data);
    }

    public static ArrayList<Video> multiVideosParser(String data){
        return VideoParser.multiVideosParser(data);
    }

    public static HashMap<Integer, Season> fullSingleSeasonParser(String data, List<String> number){
        return SeasonParser.fullSingleSeasonParser(data,number);
    }

    public static Genre singleGenreParser(String data){
        return GenreParser.singleGenreParser(data);
    }

    public static ArrayList<Genre> multiGenresParser(String data){
        return GenreParser.multiGenresParser(data);
    }

    public static ArrayList<Genre> multiGenresParserForFilter(String data){
        return GenreParser.multiGenresParserForFilter(data);
    }

    public static ArrayList<Media> multiMediasParser(String data){

        ArrayList<Media> medias = new ArrayList<>();

        //Log.e(TAG,data);

        try {
            JSONObject object = new JSONObject(data);
            JSONArray projects = object.getJSONArray("results");
            for (int i = 0; i < projects.length(); i++) {
                JSONObject c = projects.getJSONObject(i);

                Media media;
                if(c.getString("media_type").equals("movie")) {
                    media = singleMovieParser(c.toString());
                    media.setMediaType(Media.MOVIE);
                    medias.add(media);
                }else if(c.getString("media_type").equals("tv")){
                    media = singleSerieParser(c.toString());
                    media.setMediaType(Media.SERIE);
                    medias.add(media);
                }else if (c.getString("media_type").equals("person")){
                    media = singleArtistParser(c.toString());
                    media.setMediaType(Media.ARTIST);
                    medias.add(media);
                } else{
                    Log.e("Other type",c.toString());
                }
            }
        } catch (JSONException e) {
            Log.e("Error in parsing multi",e.getMessage());
        }

        return medias;
    }

    /**
     * Some arguments being 'Language' type this can parse to it
     * @param language
     * @return
     */
    static Language getLanguage(String language){
        switch (language){
            case "es": return Language.es;
            case "en": return Language.en;
            case "fr": return Language.fr;
            case "us": return Language.us;
            default:return Language.unknown;
        }
    }

    /**
     *
     * @param data
     * @return
     */
    static ArrayList<Network> getNetworks(String data){
        ArrayList<Network> networks = new ArrayList<>();

        try{
            JSONArray object = new JSONArray(data);
            for (int i = 0; i < object.length(); i++) {
                JSONObject c = object.getJSONObject(i);
                Network network = new Network();

                network.setId(c.getInt("id"));
                network.setLogoPath(c.getString("logo_path"));
                network.setName(c.getString("name"));
                network.setOriginCountry(getLanguage(c.getString("origin_country")));

                networks.add(network);
            }
        } catch (JSONException e) {
            e.printStackTrace();Log.e("Json Parsing Error in network",e.getMessage());
        }

        return networks;
    }

    /**
     *
     * @param data
     * @return
     */
    static ArrayList<ProductionCompany> getProductionCompanies(String data){
        ArrayList<ProductionCompany> productionCompanies = new ArrayList<>();

        try{
            JSONArray object = new JSONArray(data);
            for (int i = 0; i < object.length(); i++) {
                JSONObject c = object.getJSONObject(i);
                ProductionCompany productionCompany = new ProductionCompany();

                productionCompany.setId(c.getInt("id"));
                productionCompany.setName(c.getString("name"));

                productionCompanies.add(productionCompany);
            }
        } catch (JSONException e) {
            e.printStackTrace();Log.e("Json Parsing Error in production companies",e.getMessage());
        }

        return productionCompanies;
    }
}
