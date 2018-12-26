package fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.https.HttpsHandler;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.parser.WebServiceParser;

/**
 * Created by lucasamiaud on 30/12/2017.
 */

public class DatabaseTransactionManager {

    private static final String TAG = DatabaseTransactionManager.class.getName();

    public static void addSerieWithSeasons(final AppDatabase db, Serie serie) {
        SerieDbAsync taskDB = new SerieDbAsync(db,serie);
        taskDB.execute();
    }

    public static void deleteSerie(final AppDatabase db, Serie serie) {
        new Thread(() -> db.seriesDAO().deleteSerie(serie)).start();
        Log.i(TAG, "Serie deleted -> "+ serie.getName());
    }

    public static List<Integer> getAllMovieIds(final AppDatabase db) {
        GetAllMovieIds getAllMovieIds = new GetAllMovieIds(db);
        try {
            AsyncTask<String, Boolean, List<Integer>> kk = getAllMovieIds.execute();
            return kk.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static List<Movie> getAllMovies(final AppDatabase db) {
        GetAllMovies getAllMovies = new GetAllMovies(db);
        try {
            AsyncTask<String, Boolean, List<Movie>> kk = getAllMovies.execute();
            return kk.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static List<Integer> getAllSerieIds(final AppDatabase db) {
        GetAllSerieIds getAllSerieIds = new GetAllSerieIds(db);
        try {
            AsyncTask<String, Boolean, List<Integer>> kk = getAllSerieIds.execute();
            return kk.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static List<Serie> getAllSeries(final AppDatabase db) {
        GetAllSeries getAllSeries = new GetAllSeries(db);
        try {
            AsyncTask<String, Boolean, List<Serie>> kk = getAllSeries.execute();
            return kk.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static Serie getSerieById(final AppDatabase db, int id) {
        GetSerieById getSerieById = new GetSerieById(db, id);
        try {
            AsyncTask<String, Boolean, Serie> kk = getSerieById.execute();
            return kk.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Season> getAllSeasons(final AppDatabase db) {
        GetAllSeasons getAllSeasons = new GetAllSeasons(db);
        try {
            AsyncTask<String, Boolean, List<Season>> kk = getAllSeasons.execute();
            return kk.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static Season getSeasonById(final AppDatabase db, int id) {
        GetSeasonById getSeasonById = new GetSeasonById(db, id);
        try {
            AsyncTask<String, Boolean, Season> kk = getSeasonById.execute();
            return kk.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addMovie(final AppDatabase db, Movie movie){
        new Thread(() -> db.moviesDAO().insertMovie(movie)).start();
    }

    public static void addSeason(final AppDatabase db, Season season){
        new Thread(() -> db.seasonDAO().insertSeason(season)).start();
        new Thread(() -> db.episodesDAO().insertAll(season.getEpisodes()));
    }

    public static void deleteMovie(final AppDatabase db, Movie movie) {
        new Thread(() -> db.moviesDAO().deleteMovie(movie)).start();
        Log.i(TAG, "Movie deleted -> "+ movie.getTitle());
    }

    private static class GetAllMovieIds extends AsyncTask<String, Boolean, List<Integer>> {

        private final AppDatabase mDb;

        GetAllMovieIds(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected List<Integer> doInBackground(String... strings) {
            return mDb.moviesDAO().getAllIds();
        }
    }

    private static class GetAllMovies extends AsyncTask<String, Boolean, List<Movie>> {

        private final AppDatabase mDb;

        GetAllMovies(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected List<Movie> doInBackground(String... strings) {
            return mDb.moviesDAO().getAll();
        }
    }

    private static class GetAllSerieIds extends AsyncTask<String, Boolean, List<Integer>> {

        private final AppDatabase mDb;

        GetAllSerieIds(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected List<Integer> doInBackground(String... strings) {
            return mDb.seriesDAO().getAllIds();
        }
    }

    private static class GetAllSeries extends AsyncTask<String, Boolean, List<Serie>> {

        private final AppDatabase mDb;

        GetAllSeries(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected List<Serie> doInBackground(String... strings) {
            return mDb.seriesDAO().getAll();
        }
    }

    private static class GetSerieById extends AsyncTask<String, Boolean, Serie> {

        private final AppDatabase mDb;
        private final int id;

        GetSerieById(AppDatabase db, int id) {
            mDb = db;
            this.id = id;
        }

        @Override
        protected Serie doInBackground(String... strings) {
            return mDb.seriesDAO().getSerieById(id);
        }
    }

    private static class GetAllSeasons extends AsyncTask<String, Boolean, List<Season>> {

        private final AppDatabase mDb;

        GetAllSeasons(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected List<Season> doInBackground(String... strings) {
            return mDb.seasonDAO().getAll();
        }
    }

    private static class GetSeasonById extends AsyncTask<String, Boolean, Season> {

        private final AppDatabase mDb;
        private final int id;

        GetSeasonById(AppDatabase db, int id) {
            mDb = db;
            this.id = id;
        }

        @Override
        protected Season doInBackground(String... strings) {
            return mDb.seasonDAO().getSeasonById(id);
        }
    }

    private static class SerieDbAsync extends AsyncTask<String, Boolean, String> {

        private final AppDatabase mDb;

        private Serie serie;

        SerieDbAsync(AppDatabase db, Serie serie) {
            mDb = db;
            this.serie = serie;
        }

        @Override
        protected String doInBackground(final String... params) {
            HttpsHandler sh = new HttpsHandler();

            mDb.seriesDAO().insertSerie(serie);

            String args = "&language=en-US&append_to_response="
                    + "season/"
                    + this.serie.getSeasonsNumbers().stream().collect(Collectors.joining(",season/"));

            // Making a request to url and getting response

            return sh.makeServiceCall("tv",String.valueOf(this.serie.getId()),args);
        }

        @Override
        protected void onPostExecute(String aVoid) {
            if(!aVoid.isEmpty()) {
                HashMap<Integer,Season> seasons = WebServiceParser.fullSingleSeasonParser(aVoid, this.serie.getSeasonsNumbers());
                for (Season seasonFromSerie : this.serie.getSeasons()){
                    Season tmp = seasons.get(seasonFromSerie.getSeasonNumber());
                    for (Episode episode : tmp.getEpisodes()){
                        episode.setIdSeason(seasonFromSerie.getId());
                    }
                    seasonFromSerie.setEpisodes(tmp.getEpisodes());
                    DatabaseTransactionManager.addSeason(mDb, seasonFromSerie);
                }
            }
        }
    }
}
