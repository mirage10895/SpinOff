package fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.https.HttpsHandler;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.parser.WebServiceParser;

/**
 * Created by lucasamiaud on 30/12/2017.
 */

public class DatabaseTransactionManager {

    private static final String TAG = DatabaseTransactionManager.class.getName();

    public static void executeAsync(Runnable target) {
        new Thread(target).start();
    }

    public static void addSerieWithSeasons(final AppDatabase db, Serie serie) {
        SerieDbAsync taskDB = new SerieDbAsync(db, serie);
        taskDB.execute();
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

            mDb.serieDAO().insertSerie(serie);

            String args = "&language=en-US&append_to_response="
                    + "season/"
                    + this.serie.getSeasonsNumbers().stream().collect(Collectors.joining(",season/"));

            return sh.makeServiceCall("tv",String.valueOf(this.serie.getId()),args);
        }

        @Override
        protected void onPostExecute(String aVoid) {
            if(!aVoid.isEmpty()) {
                HashMap<Integer,Season> seasons = WebServiceParser.fullSingleSeasonParser(aVoid, this.serie.getSeasonsNumbers());
                for (Season seasonFromSerie : this.serie.getSeasons()){
                    Season tmp = seasons.get(seasonFromSerie.getSeasonNumber());
                    for (Episode episode : tmp.getEpisodes()){
                        episode.setSeasonId(seasonFromSerie.getId());
                    }
                    seasonFromSerie.setEpisodes(tmp.getEpisodes());
                    DatabaseTransactionManager.executeAsync(() -> mDb.seasonDAO().insertSeason(seasonFromSerie));
                }
            }
        }
    }
}
