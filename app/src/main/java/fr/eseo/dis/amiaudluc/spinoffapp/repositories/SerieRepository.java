package fr.eseo.dis.amiaudluc.spinoffapp.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.TmdbApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Genre;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.ProductionCountry;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.EpisodeDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.SeasonDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.SerieDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.EpisodeDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SeasonCount;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SeasonDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;

public class SerieRepository {
    private static SerieRepository INSTANCE = null;

    private final SerieDAO serieDAO;
    private final SeasonDAO seasonDAO;
    private final EpisodeDAO episodeDAO;
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
        seasonDAO = db.seasonDAO();
        episodeDAO = db.episodeDAO();
        this.apiRepository = TmdbApiRepository.getInstance();
    }

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

    public void insert(int serieId) {
        executor.execute(() -> {
            Optional<Serie> apiSerie = this.apiRepository.getSerieByIdSync(serieId);
            if (apiSerie.isEmpty()) {
                return;
            }
            this.serieDAO.insertSerie(computeSerieDatabase(apiSerie.get(), false));
            upsertSeasons(apiSerie.get());
            recomputeSerieWatched(serieId);
        });
    }

    /**
     * Synchronous version for background tasks.
     */
    public void updateAllSeriesSync(Duration notSyncSince) {
        List<SerieDatabase> series = this.serieDAO.getAllSync();
        for (SerieDatabase serieDatabase : series) {
            // Only update if last sync was more than the given duration ago
            if (serieDatabase.getLastSynchronisationTime() == null ||
                    serieDatabase.getLastSynchronisationTime().isBefore(Instant.now().minus(notSyncSince))) {

                Optional<Serie> apiSerie = this.apiRepository.getSerieByIdSync(serieDatabase.getId());
                if (apiSerie.isEmpty()) {
                    continue;
                }

                boolean legacyWatched = serieDatabase.isWatched();
                this.serieDAO.updateSerie(computeSerieDatabase(apiSerie.get(), legacyWatched));
                upsertSeasons(apiSerie.get());

                // Data migration: propagate a legacy "watched" flag to every non-special season
                if (legacyWatched) {
                    this.seasonDAO.setAllSeasonsWatched(serieDatabase.getId(), true);
                }

                recomputeSerieWatched(serieDatabase.getId());
            }
        }
    }

    private SerieDatabase computeSerieDatabase(Serie apiSerie, boolean isWatched) {
        SerieDatabase serieDatabase = new SerieDatabase();
        serieDatabase.setId(apiSerie.getId());
        serieDatabase.setName(apiSerie.getName());
        serieDatabase.setPosterPath(apiSerie.getPosterPath());
        serieDatabase.setWatched(isWatched);
        serieDatabase.setGenres(TmdbApiRepository.formatList(apiSerie.getGenres(), Genre::getName));
        serieDatabase.setEpisodeCount(apiSerie.getNumberOfEpisodes());
        serieDatabase.setSeasonCount(apiSerie.getNumberOfSeasons());
        serieDatabase.setFirstAirDate(apiSerie.getFirstAirDate());
        serieDatabase.setVoteAverage(apiSerie.getVoteAverage());
        serieDatabase.setOriginalLanguage(apiSerie.getOriginalLanguage());
        serieDatabase.setPopularity(apiSerie.getPopularity());
        serieDatabase.setProductionCountries(TmdbApiRepository.formatList(apiSerie.getProductionCountries(), ProductionCountry::getName));
        serieDatabase.setActors(TmdbApiRepository.formatList(apiSerie.getCredits().getCast(), Artist::getName));
        serieDatabase.setDirectors(TmdbApiRepository.formatList(apiSerie.getCreatedBy(), Artist::getName));
        serieDatabase.setLastSynchronisationTime(Instant.now());

        if (apiSerie.getNumberOfSeasons() == null || apiSerie.getNumberOfSeasons() == 0) {
            return serieDatabase;
        }

        if (apiSerie.getEpisodeRunTime() == null || apiSerie.getEpisodeRunTime().isEmpty()) {
            Optional<Season> season = this.apiRepository.getSeasonBySerieIdSync(apiSerie.getId(), 1);
            season.ifPresent(value -> serieDatabase.setRuntime(
                    Season.computeEpisodesAverageRuntime(value) * apiSerie.getNumberOfEpisodes()
            ));
        } else {
            serieDatabase.setRuntime(Serie.computeTotalRuntime(apiSerie));
        }
        return serieDatabase;
    }

    private void upsertSeasons(Serie apiSerie) {
        if (apiSerie.getSeasons() == null) {
            return;
        }
        for (Season season : apiSerie.getSeasons()) {
            if (season.getSeasonNumber() == null) {
                continue;
            }
            this.seasonDAO.upsertSeason(apiSerie.getId(), season.getSeasonNumber(), season.getEpisodeCount());
        }
    }

    public void deleteById(int id) {
        executor.execute(() -> {
            this.episodeDAO.deleteBySerie(id);
            this.seasonDAO.deleteBySerie(id);
            this.serieDAO.deleteSerieById(id);
        });
    }

    public void setSerieWatched(int serieId, boolean watched) {
        executor.execute(() -> applySerieWatched(serieId, watched));
    }

    private void applySerieWatched(int serieId, boolean watched) {
        // Season flags carry the "watched" state; individual episode rows become redundant
        this.seasonDAO.setAllSeasonsWatched(serieId, watched);
        this.episodeDAO.deleteBySerie(serieId);
        recomputeSerieWatched(serieId);
    }

    public void setSeasonWatched(int serieId, int seasonNumber, boolean watched) {
        executor.execute(() -> {
            this.seasonDAO.setSeasonWatched(serieId, seasonNumber, watched);
            this.episodeDAO.deleteBySeason(serieId, seasonNumber);
            recomputeSerieWatched(serieId);
        });
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

    public void setEpisodeWatched(int serieId, int seasonNumber, int episodeNumber, boolean watched) {
        executor.execute(() -> {
            SeasonDatabase season = this.seasonDAO.getSeasonSync(serieId, seasonNumber);

            if (watched) {
                insertWatchedEpisode(serieId, seasonNumber, episodeNumber);
                if (season != null && season.getEpisodeCount() != null && season.getEpisodeCount() > 0
                        && this.episodeDAO.countBySeason(serieId, seasonNumber) >= season.getEpisodeCount()) {
                    this.seasonDAO.setSeasonWatched(serieId, seasonNumber, true);
                }
            } else {
                if (season != null && season.isWatched()) {
                    // The whole season was watched: unflag it and keep the other episodes watched
                    this.seasonDAO.setSeasonWatched(serieId, seasonNumber, false);
                    materializeAllEpisodesSync(serieId, seasonNumber);
                }
                this.episodeDAO.deleteEpisode(serieId, seasonNumber, episodeNumber);
            }
            recomputeSerieWatched(serieId);
        });
    }

    private void insertWatchedEpisode(int serieId, int seasonNumber, int episodeNumber) {
        EpisodeDatabase row = new EpisodeDatabase();
        row.setSerieId(serieId);
        row.setSeasonNumber(seasonNumber);
        row.setEpisodeNumber(episodeNumber);
        this.episodeDAO.insertEpisode(row);
    }

    /**
     * Insert every episode of a season as watched. Only used when a wholesale-watched season
     * is un-watched on a single episode, so the remaining episodes stay watched.
     */
    private void materializeAllEpisodesSync(int serieId, int seasonNumber) {
        SeasonDatabase season = this.seasonDAO.getSeasonSync(serieId, seasonNumber);
        if (season != null && season.getEpisodeCount() != null && season.getEpisodeCount() > 0) {
            List<EpisodeDatabase> rows = new ArrayList<>();
            for (int i = 1; i <= season.getEpisodeCount(); i++) {
                EpisodeDatabase row = new EpisodeDatabase();
                row.setSerieId(serieId);
                row.setSeasonNumber(seasonNumber);
                row.setEpisodeNumber(i);
                rows.add(row);
            }
            this.episodeDAO.insertEpisodes(rows);
            return;
        }

        // Fallback using API if local metadata is missing
        Optional<Season> apiSeason = this.apiRepository.getSeasonBySerieIdSync(serieId, seasonNumber);
        if (apiSeason.isEmpty() || apiSeason.get().getEpisodes() == null) {
            return;
        }
        List<EpisodeDatabase> rows = new ArrayList<>();
        for (Episode episode : apiSeason.get().getEpisodes()) {
            if (episode.getEpisodeNumber() == null) {
                continue;
            }
            EpisodeDatabase row = new EpisodeDatabase();
            row.setSerieId(serieId);
            row.setSeasonNumber(seasonNumber);
            row.setEpisodeNumber(episode.getEpisodeNumber());
            rows.add(row);
        }
        this.episodeDAO.insertEpisodes(rows);
    }

    /**
     * Recompute the aggregated "watched" flag of a serie from its non-special seasons.
     */
    private void recomputeSerieWatched(int serieId) {
        List<SeasonDatabase> seasons = this.seasonDAO.getSeasonsBySerieSync(serieId);
        List<SeasonCount> watchedCounts = this.episodeDAO.getWatchedCountsBySeasonSync(serieId);
        Map<Integer, Integer> countsMap = new HashMap<>();
        for (SeasonCount sc : watchedCounts) {
            countsMap.put(sc.seasonNumber, sc.watchedCount);
        }

        boolean hasCountableSeason = false;
        boolean allWatched = true;

        for (SeasonDatabase season : seasons) {
            if (season.getSeasonNumber() == 0) {
                continue;
            }
            hasCountableSeason = true;

            boolean seasonWatched = season.isWatched();
            int totalCount = season.getEpisodeCount() != null ? season.getEpisodeCount() : 0;
            int watchedCount = countsMap.getOrDefault(season.getSeasonNumber(), 0);

            if (!seasonWatched) {
                if (totalCount > 0 && watchedCount >= totalCount) {
                    seasonWatched = true;
                    this.seasonDAO.setSeasonWatched(serieId, season.getSeasonNumber(), true);
                    this.episodeDAO.deleteBySeason(serieId, season.getSeasonNumber());
                }
            }
            if (!seasonWatched) {
                allWatched = false;
            }
        }

        this.serieDAO.updateSerieWatched(serieId, hasCountableSeason && allWatched);
    }
}
