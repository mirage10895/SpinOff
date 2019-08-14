package fr.eseo.dis.amiaudluc.spinoffapp.content;

import java.util.ArrayList;

import fr.eseo.dis.amiaudluc.spinoffapp.model.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Network;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

public class Content {

    public static String currentFragment;

    public static ArrayList<Movie> movies;

    public static Movie currentMovie;

    public  static ArrayList<Serie> series;

    public static Serie currentSerie;

    public static ArrayList<Media> searchedMedias = new ArrayList<>();

    public static ArrayList<Movie> searchedMovies = new ArrayList<>();

    public  static ArrayList<Serie> searchedSeries = new ArrayList<>();

    public  static ArrayList<Artist> searchedArtists = new ArrayList<>();

    public static Season currentSeason;

    public static ArrayList<Season> seasons = new ArrayList<>();

    public static Network currentNetwork;

    public static ArrayList<Episode> episodes;

    public static Episode currentEpisode;

    public static ArrayList<Artist> artists;

    public static Artist currentArtist;
}
