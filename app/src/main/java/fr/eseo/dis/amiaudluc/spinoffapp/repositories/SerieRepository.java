package fr.eseo.dis.amiaudluc.spinoffapp.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.TmdbApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.EpisodeDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.SeasonDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.SerieDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.SerieNotificationDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.EpisodeDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SeasonDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.release.SerieRelease;

/**
 * Single entry point to the series data: orchestrates Room (library, watched state) and the TMDB
 * API. The collaborators hold the actual logic — {@link SerieWatchTracker} for the watched state,
 * {@link SerieReleaseTracker} for the release tracking and {@link SerieSynchroniser} for the two
 * synchronisation frequencies — so this class only sequences them.
 */
public class SerieRepository {
    private static SerieRepository INSTANCE = null;

    private final SerieDAO serieDAO;
    private final SeasonDAO seasonDAO;
    private final EpisodeDAO episodeDAO;
    private final SerieNotificationDAO serieNotificationDAO;
    private final TmdbApiRepository apiRepository;

    private final SerieWatchTracker watchTracker;
    private final SerieReleaseTracker releaseTracker;
    private final SerieSynchroniser synchroniser;

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
        seasonDAO = db.seasonDAO();
        episodeDAO = db.episodeDAO();
        serieNotificationDAO = db.serieNotificationDAO();
        this.apiRepository = TmdbApiRepository.getInstance();
        this.watchTracker = new SerieWatchTracker(serieDAO, seasonDAO, episodeDAO, apiRepository);
        this.releaseTracker = new SerieReleaseTracker(serieNotificationDAO);
        this.synchroniser = new SerieSynchroniser(serieDAO, seasonDAO, serieNotificationDAO,
                apiRepository, watchTracker, releaseTracker);
    }

    // --- Reads -------------------------------------------------------------------------------

    public LiveData<List<SerieDatabase>> fetchAll() {
        return this.serieDAO.getAll();
    }
    public LiveData<List<SeasonDatabase>> fetchAllSeasons() {
        return this.seasonDAO.getAll();
    }
    public LiveData<List<EpisodeDatabase>> fetchAllEpisodes() {
        return this.episodeDAO.getAll();
    }
    public LiveData<List<SeasonDatabase>> fetchSeasonsBySerie(int serieId) {
        return this.seasonDAO.getSeasonsBySerie(serieId);
    }
    public LiveData<SeasonDatabase> fetchSeason(int serieId, int seasonNumber) {
        return this.seasonDAO.getSeason(serieId, seasonNumber);
    }
    public LiveData<List<EpisodeDatabase>> fetchEpisodesBySerieAndSeason(int serieId, int seasonNumber) {
        return this.episodeDAO.getEpisodesBySerieAndSeason(serieId, seasonNumber);
    }
    public LiveData<List<EpisodeDatabase>> fetchEpisodesBySerie(int serieId) {
        return this.episodeDAO.getEpisodesBySerie(serieId);
    }

    // --- Writes ------------------------------------------------------------------------------

    public void insert(int serieId) {
        executor.execute(() -> {
            Optional<Serie> apiSerie = this.apiRepository.getSerieByIdSync(serieId);
            if (apiSerie.isEmpty()) {
                return;
            }
            this.serieDAO.insertSerie(SerieMapper.toDatabase(apiSerie.get(), false, apiRepository));
            this.seasonDAO.upsertSeasons(serieId, SerieSynchroniser.seasonsOf(apiSerie.get()));
            watchTracker.recomputeSerieWatched(serieId);
        });
    }

    public void deleteById(int id) {
        executor.execute(() -> {
            this.serieNotificationDAO.deleteBySerie(id);
            this.episodeDAO.deleteBySerie(id);
            this.seasonDAO.deleteBySerie(id);
            this.serieDAO.deleteSerieById(id);
        });
    }

    public void setSerieWatched(int serieId, boolean watched) {
        executor.execute(() -> watchTracker.applySerieWatched(serieId, watched));
    }

    public void setSeasonWatched(int serieId, int seasonNumber, boolean watched) {
        executor.execute(() -> watchTracker.applySeasonWatched(serieId, seasonNumber, watched));
    }

    public void setEpisodeWatched(int serieId, int seasonNumber, int episodeNumber, boolean watched) {
        executor.execute(() -> watchTracker.applyEpisodeWatched(serieId, seasonNumber, episodeNumber, watched));
    }

    /**
     * Store the episode count of a season when the season screen is opened.
     * Never overwrites a valid count with a non-positive one.
     */
    public void ensureSeasonMetadata(int serieId, int seasonNumber, int episodeCount) {
        executor.execute(() -> {
            if (this.serieDAO.fetchSerieByIdSync(serieId) == null) {
                return;
            }
            this.seasonDAO.upsertSeason(serieId, seasonNumber, episodeCount);
        });
    }

    // --- Background synchronisation ----------------------------------------------------------

    /**
     * Daily release check on the lightweight TMDB payload: detects new episodes and seasons
     * without carrying the cost of a full metadata synchronisation.
     *
     * @return the releases discovered during the check, one entry per concerned serie
     */
    public List<SerieRelease> syncReleasesSync() {
        return synchroniser.checkReleases();
    }

    /**
     * Full metadata synchronisation (credits, videos, runtime…), throttled by the given duration
     * because it is one large request per serie.
     */
    public void updateAllSeriesSync(Duration notSyncSince) {
        synchroniser.refreshMetadata(notSyncSince);
    }
}
