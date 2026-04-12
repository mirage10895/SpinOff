package fr.eseo.dis.amiaudluc.spinoffapp.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.SerieDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.TmdbApiRepository;

public class SerieRepository {
    private static SerieRepository INSTANCE = null;

    private final SerieDAO serieDAO;
    private final TmdbApiRepository apiRepository;

    private final Executor executor = Executors.newSingleThreadExecutor();

    public static SerieRepository getRepository(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SerieRepository(context);
        }
        return INSTANCE;
    }

    private SerieRepository(Context context) {
        AppDatabase db = AppDatabase.getAppDatabase(context);
        serieDAO = db.serieDAO();
        this.apiRepository = TmdbApiRepository.getInstance();
    }

    public LiveData<List<SerieDatabase>> fetchAll() {
        return this.serieDAO.getAll();
    }

    public void insert(int serieId) {
        executor.execute(() -> {
            SerieDatabase result = computeSerieDatabaseSync(serieId, false);
            if (result != null) {
                this.insert(result);
            }
        });
    }

    /**
     * Synchronous version for background tasks.
     */
    public void updateAllSeriesSync(Duration notSyncSince) {
        List<SerieDatabase> series = this.serieDAO.getAllSync();
        for (SerieDatabase serieDatabase : series) {
            // Only update if last sync was more than 10 days ago
            if (serieDatabase.getLastSynchronisationTime() == null ||
                    serieDatabase.getLastSynchronisationTime().isBefore(Instant.now().minus(notSyncSince))) {

                SerieDatabase updated = computeSerieDatabaseSync(serieDatabase.getId(), serieDatabase.isWatched());
                if (updated != null) {
                    this.serieDAO.updateSerie(updated);
                }
            }
        }
    }

    /**
     * Synchronous version containing the source of truth for the logic.
     */
    private SerieDatabase computeSerieDatabaseSync(int serieId, boolean isWatched) {
        Optional<Serie> apiSerie = this.apiRepository.getSerieByIdSync(serieId);
        if (apiSerie.isEmpty()) {
            return null;
        }

        SerieDatabase serieDatabase = new SerieDatabase();
        serieDatabase.setId(apiSerie.get().getId());
        serieDatabase.setName(apiSerie.get().getName());
        serieDatabase.setPosterPath(apiSerie.get().getPosterPath());
        serieDatabase.setWatched(isWatched);
        serieDatabase.setGenres(TmdbApiRepository.formatGenres(apiSerie.get().getGenres()));
        serieDatabase.setEpisodeCount(apiSerie.get().getNumberOfEpisodes());
        serieDatabase.setSeasonCount(apiSerie.get().getNumberOfSeasons());
        serieDatabase.setFirstAirDate(apiSerie.get().getFirstAirDate());
        serieDatabase.setLastSynchronisationTime(Instant.now());

        if (apiSerie.get().getNumberOfSeasons() == null || apiSerie.get().getNumberOfSeasons() == 0) {
            return serieDatabase;
        }

        if (apiSerie.get().getEpisodeRunTime() == null || apiSerie.get().getEpisodeRunTime().isEmpty()) {
            Optional<Season> season = this.apiRepository.getSeasonBySerieIdSync(apiSerie.get().getId(), 1);
            season.ifPresent(value -> serieDatabase.setRuntime(
                    Season.computeEpisodesAverageRuntime(value) * apiSerie.get().getNumberOfEpisodes()
            ));
        } else {
            serieDatabase.setRuntime(Serie.computeTotalRuntime(apiSerie.get()));
        }
        return serieDatabase;
    }

    public void deleteById(int id) {
        executor.execute(() -> this.serieDAO.deleteSerieById(id));
    }

    public void toggleSerieIsWatched(int id) {
        executor.execute(() -> this.serieDAO.toggleSerieIsWatched(id));
    }

    private void insert(SerieDatabase serieDatabase) {
        executor.execute(() -> this.serieDAO.insertSerie(serieDatabase));
    }
}
