package fr.eseo.dis.amiaudluc.spinoffapp.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.TmdbApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.EpisodeDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.SeasonDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.SerieDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.EpisodeDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SeasonCount;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SeasonDatabase;

/**
 * Owns the watched state of a serie: it turns season and episode toggles into the matching season
 * flags and watched episode rows, then recomputes the aggregated serie flag.
 * <p>
 * Extracted from {@link SerieRepository} so the repository stays an orchestration entry point;
 * every method here runs on the caller's background thread.
 */
class SerieWatchTracker {

    private final SerieDAO serieDAO;
    private final SeasonDAO seasonDAO;
    private final EpisodeDAO episodeDAO;
    private final TmdbApiRepository apiRepository;

    SerieWatchTracker(SerieDAO serieDAO, SeasonDAO seasonDAO, EpisodeDAO episodeDAO,
                      TmdbApiRepository apiRepository) {
        this.serieDAO = serieDAO;
        this.seasonDAO = seasonDAO;
        this.episodeDAO = episodeDAO;
        this.apiRepository = apiRepository;
    }

    /**
     * Season flags carry the "watched" state; individual episode rows become redundant.
     */
    void applySerieWatched(int serieId, boolean watched) {
        seasonDAO.setAllSeasonsWatched(serieId, watched);
        episodeDAO.deleteBySerie(serieId);
        recomputeSerieWatched(serieId);
    }

    void applySeasonWatched(int serieId, int seasonNumber, boolean watched) {
        seasonDAO.setSeasonWatched(serieId, seasonNumber, watched);
        episodeDAO.deleteBySeason(serieId, seasonNumber);
        recomputeSerieWatched(serieId);
    }

    void applyEpisodeWatched(int serieId, int seasonNumber, int episodeNumber, boolean watched) {
        SeasonDatabase season = seasonDAO.getSeasonSync(serieId, seasonNumber);
        if (watched) {
            markEpisodeWatched(serieId, seasonNumber, episodeNumber, season);
        } else {
            unmarkEpisodeWatched(serieId, seasonNumber, episodeNumber, season);
        }
        recomputeSerieWatched(serieId);
    }

    private void markEpisodeWatched(int serieId, int seasonNumber, int episodeNumber,
                                    SeasonDatabase season) {
        insertWatchedEpisode(serieId, seasonNumber, episodeNumber);
        boolean seasonComplete = season != null && season.getEpisodeCount() != null
                && season.getEpisodeCount() > 0
                && episodeDAO.countBySeason(serieId, seasonNumber) >= season.getEpisodeCount();
        if (seasonComplete) {
            seasonDAO.setSeasonWatched(serieId, seasonNumber, true);
        }
    }

    private void unmarkEpisodeWatched(int serieId, int seasonNumber, int episodeNumber,
                                      SeasonDatabase season) {
        if (season != null && season.isWatched()) {
            // The whole season was watched: unflag it and keep the other episodes watched
            seasonDAO.setSeasonWatched(serieId, seasonNumber, false);
            materializeAllEpisodesSync(serieId, seasonNumber);
        }
        episodeDAO.deleteEpisode(serieId, seasonNumber, episodeNumber);
    }

    private void insertWatchedEpisode(int serieId, int seasonNumber, int episodeNumber) {
        EpisodeDatabase row = new EpisodeDatabase();
        row.setSerieId(serieId);
        row.setSeasonNumber(seasonNumber);
        row.setEpisodeNumber(episodeNumber);
        episodeDAO.insertEpisode(row);
    }

    /**
     * Insert every episode of a season as watched. Only used when a wholesale-watched season
     * is un-watched on a single episode, so the remaining episodes stay watched.
     */
    private void materializeAllEpisodesSync(int serieId, int seasonNumber) {
        SeasonDatabase season = seasonDAO.getSeasonSync(serieId, seasonNumber);
        if (season != null && season.getEpisodeCount() != null && season.getEpisodeCount() > 0) {
            List<EpisodeDatabase> rows = new ArrayList<>();
            for (int i = 1; i <= season.getEpisodeCount(); i++) {
                rows.add(episodeRow(serieId, seasonNumber, i));
            }
            episodeDAO.insertEpisodes(rows);
            return;
        }

        // Fallback using API if local metadata is missing
        Optional<Season> apiSeason = apiRepository.getSeasonBySerieIdSync(serieId, seasonNumber);
        if (apiSeason.isEmpty() || apiSeason.get().getEpisodes() == null) {
            return;
        }
        List<EpisodeDatabase> rows = new ArrayList<>();
        for (Episode episode : apiSeason.get().getEpisodes()) {
            if (episode.getEpisodeNumber() != null) {
                rows.add(episodeRow(serieId, seasonNumber, episode.getEpisodeNumber()));
            }
        }
        episodeDAO.insertEpisodes(rows);
    }

    private static EpisodeDatabase episodeRow(int serieId, int seasonNumber, int episodeNumber) {
        EpisodeDatabase row = new EpisodeDatabase();
        row.setSerieId(serieId);
        row.setSeasonNumber(seasonNumber);
        row.setEpisodeNumber(episodeNumber);
        return row;
    }

    /**
     * Recompute the aggregated "watched" flag of a serie from its non-special seasons.
     */
    void recomputeSerieWatched(int serieId) {
        Map<Integer, Integer> watchedCounts = new HashMap<>();
        for (SeasonCount count : episodeDAO.getWatchedCountsBySeasonSync(serieId)) {
            watchedCounts.put(count.seasonNumber, count.watchedCount);
        }

        boolean hasCountableSeason = false;
        boolean allWatched = true;
        for (SeasonDatabase season : seasonDAO.getSeasonsBySerieSync(serieId)) {
            if (season.getSeasonNumber() == 0) {
                continue;
            }
            hasCountableSeason = true;
            if (!isSeasonWatched(serieId, season, watchedCounts)) {
                allWatched = false;
            }
        }
        serieDAO.updateSerieWatched(serieId, hasCountableSeason && allWatched);
    }

    /**
     * A season counts as watched when flagged, or when its episode rows cover its episode count.
     */
    private boolean isSeasonWatched(int serieId, SeasonDatabase season,
                                    Map<Integer, Integer> watchedCounts) {
        if (season.isWatched()) {
            return true;
        }
        int totalCount = season.getEpisodeCount() != null ? season.getEpisodeCount() : 0;
        int watchedCount = watchedCounts.getOrDefault(season.getSeasonNumber(), 0);
        if (totalCount > 0 && watchedCount >= totalCount) {
            seasonDAO.setSeasonWatched(serieId, season.getSeasonNumber(), true);
            episodeDAO.deleteBySeason(serieId, season.getSeasonNumber());
            return true;
        }
        return false;
    }
}
