package fr.eseo.dis.amiaudluc.spinoffapp.repositories;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.TmdbApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.SeasonDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.SerieDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.SerieNotificationDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.release.SerieRelease;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.release.SerieReleaseDetector;

/**
 * Runs the two synchronisations of the library series, kept apart so the expensive one does not
 * have to run every day:
 * <ul>
 *   <li>{@link #checkReleases()} uses the lightweight TMDB payload and only refreshes the columns
 *       the release detection needs — cheap enough to run daily;</li>
 *   <li>{@link #refreshMetadata(Duration)} fetches credits, videos and runtime, one large request
 *       per serie, throttled by the given age.</li>
 * </ul>
 * Every method runs on the caller's background thread.
 */
class SerieSynchroniser {

    /**
     * How long an aired release stays available to the future release calendar.
     */
    private static final int RELEASE_RETENTION_DAYS = 7;

    private final SerieDAO serieDAO;
    private final SeasonDAO seasonDAO;
    private final SerieNotificationDAO serieNotificationDAO;
    private final TmdbApiRepository apiRepository;
    private final SerieWatchTracker watchTracker;
    private final SerieReleaseTracker releaseTracker;

    SerieSynchroniser(
            SerieDAO serieDAO,
            SeasonDAO seasonDAO,
            SerieNotificationDAO serieNotificationDAO,
            TmdbApiRepository apiRepository,
            SerieWatchTracker watchTracker,
            SerieReleaseTracker releaseTracker
    ) {
        this.serieDAO = serieDAO;
        this.seasonDAO = seasonDAO;
        this.serieNotificationDAO = serieNotificationDAO;
        this.apiRepository = apiRepository;
        this.watchTracker = watchTracker;
        this.releaseTracker = releaseTracker;
    }

    List<SerieRelease> checkReleases() {
        List<SerieRelease> releases = new ArrayList<>();
        SerieReleaseDetector detector = new SerieReleaseDetector(LocalDate.now());
        for (SerieDatabase serieDatabase : serieDAO.getAllSync()) {
            Optional<Serie> apiSerie = apiRepository.getSerieStatusSync(serieDatabase.getId());
            if (apiSerie.isEmpty()) {
                continue;
            }
            applyReleaseSnapshot(serieDatabase.getId(), apiSerie.get());
            releaseTracker.track(serieDatabase.getId(), apiSerie.get(), detector).ifPresent(releases::add);
        }
        serieNotificationDAO.clearReleasesOlderThan(LocalDate.now().minusDays(RELEASE_RETENTION_DAYS));
        return releases;
    }

    void refreshMetadata(Duration notSyncSince) {
        for (SerieDatabase serieDatabase : serieDAO.getAllSync()) {
            if (isDueForSync(serieDatabase, notSyncSince)) {
                refreshSerieMetadata(serieDatabase);
            }
        }
    }

    private void refreshSerieMetadata(SerieDatabase serieDatabase) {
        Optional<Serie> apiSerie = apiRepository.getSerieByIdSync(serieDatabase.getId());
        if (apiSerie.isEmpty()) {
            return;
        }
        boolean legacyWatched = serieDatabase.isWatched();
        serieDAO.updateSerie(SerieMapper.toDatabase(apiSerie.get(), legacyWatched, apiRepository));
        seasonDAO.upsertSeasons(apiSerie.get().getId(), seasonsOf(apiSerie.get()));

        // Data migration: propagate a legacy "watched" flag to every non-special season
        if (legacyWatched) {
            seasonDAO.setAllSeasonsWatched(serieDatabase.getId(), true);
        }
        watchTracker.recomputeSerieWatched(serieDatabase.getId());
    }

    /**
     * Store the lightweight payload in the library columns, leaving genres, actors, directors and
     * runtime untouched so the daily check never erases the full synchronisation's work.
     */
    private void applyReleaseSnapshot(int serieId, Serie apiSerie) {
        serieDAO.updateSerieReleaseSnapshot(serieId, apiSerie.getName(), apiSerie.getPosterPath(),
                apiSerie.getNumberOfSeasons(), apiSerie.getNumberOfEpisodes(), Instant.now());
        seasonDAO.upsertSeasons(serieId, seasonsOf(apiSerie));
    }

    private boolean isDueForSync(SerieDatabase serieDatabase, Duration notSyncSince) {
        return serieDatabase.getLastSynchronisationTime() == null
                || serieDatabase.getLastSynchronisationTime().isBefore(Instant.now().minus(notSyncSince));
    }

    /**
     * Empty list when TMDB omitted the seasons, so the DAO call stays a single statement. Also
     * used by {@link SerieRepository} when the serie is added to the library.
     */
    static List<Season> seasonsOf(Serie apiSerie) {
        return apiSerie.getSeasons() == null ? List.of() : apiSerie.getSeasons();
    }
}
