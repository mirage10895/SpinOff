package fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer;

import android.os.AsyncTask;

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

    public static void addMovie(final AppDatabase db, Movie movie){
        new Thread(() -> db.moviesDAO().insertMovie(movie)).start();
    }

    public static void delete(final AppDatabase db, Media media) {
        Delete taskDelete = null;
        if (Objects.equals(media.getMediaType(), Media.MOVIE)){
            taskDelete = new Delete(db,(Movie) media);
        }else if (media.getMediaType().equals(Media.SERIE)){
            taskDelete = new Delete(db,(Serie) media);
        }
        if (taskDelete != null) {
            taskDelete.execute();
        }
    }

    private static class Get extends AsyncTask<String, Void, List<Movie>> {

        private final AppDatabase mDb;

        Get(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected List<Movie> doInBackground(String... strings) {
            return mDb.moviesDAO().getAll();
        }
    }

    private static class Delete extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;

        private Serie serie;

        private Movie movie;

        Delete(AppDatabase db, Serie serie) {
            mDb = db;
            this.serie = serie;
        }

        Delete(AppDatabase db, Movie movie) {
            mDb = db;
            this.movie = movie;
        }

        @Override
        protected Void doInBackground(Void... strings) {
            if (movie != null){
                mDb.moviesDAO().deleteMovie(movie);
            }
            if (serie != null){
                mDb.seriesDAO().deleteSerie(serie);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
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
                    mDb.seasonDAO().insertSeason(seasonFromSerie);
                    mDb.episodesDAO().insertAll(seasonFromSerie.getEpisodes());
                }
            }
        }
    }
}
