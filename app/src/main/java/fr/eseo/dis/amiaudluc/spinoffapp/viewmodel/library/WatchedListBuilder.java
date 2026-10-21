package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.EpisodeDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SeasonDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.AlphabetUtils;

/**
 * Turns the local library into the tiles of the watched list screen.
 * <p>
 * Pure aggregation: stateless and side-effect free, so it can run on any thread and be unit
 * tested in isolation. It is deliberately separate from the statistics calculator that feeds
 * the library dashboards, even though both walk the same tables.
 * <p>
 * The episode progress of a serie is the number of watched episodes over the total number of
 * episodes. Watched episodes are read from the season flag when the season is fully watched —
 * the repository deletes the individual episode rows in that case — and from the episode rows
 * otherwise. Specials are left out, matching the rest of the app.
 */
public final class WatchedListBuilder {

    private static final int SPECIALS_SEASON_NUMBER = 0;

    private WatchedListBuilder() {
    }

    /**
     * Builds the tiles of every movie marked as watched.
     *
     * @param movies all the movies of the library, may be {@code null}
     */
    public static List<WatchedMediaItem> buildMovies(List<MovieDatabase> movies) {
        List<WatchedMediaItem> items = new ArrayList<>();
        if (movies == null) {
            return items;
        }

        for (MovieDatabase movie : movies) {
            if (!movie.isWatched()) {
                continue;
            }
            items.add(new WatchedMediaItem(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getPosterPath(),
                    FragmentType.MOVIE,
                    movie.getRuntime(),
                    movie.getVoteAverage(),
                    yearOf(movie.getReleaseDate()),
                    firstValue(movie.getDirectors()),
                    movie.getGenres(),
                    null,
                    0,
                    0,
                    true));
        }
        return sortedByTitle(items);
    }

    /**
     * Builds the tiles of every started serie, with its global episode progress.
     *
     * @param series   all the series of the library, may be {@code null}
     * @param seasons  all the known seasons, may be {@code null}
     * @param episodes the watched episodes, may be {@code null}
     */
    public static List<WatchedMediaItem> buildSeries(
            List<SerieDatabase> series,
            List<SeasonDatabase> seasons,
            List<EpisodeDatabase> episodes
    ) {
        List<WatchedMediaItem> items = new ArrayList<>();
        if (series == null) {
            return items;
        }

        Map<Long, Integer> watchedBySeason = watchedEpisodesBySeason(episodes);
        Map<Integer, List<SeasonDatabase>> seasonsBySerie = seasonsBySerie(seasons);

        for (SerieDatabase serie : series) {
            int watchedEpisodes = 0;
            int totalEpisodes = 0;

            for (SeasonDatabase season : seasonsBySerie.getOrDefault(serie.getId(), List.of())) {
                Integer seasonNumber = season.getSeasonNumber();
                if (seasonNumber == SPECIALS_SEASON_NUMBER) {
                    continue;
                }

                int episodeCount = season.getEpisodeCount() != null ? season.getEpisodeCount() : 0;
                totalEpisodes += episodeCount;

                if (season.isWatched()) {
                    // A fully watched season has no episode row left: its flag carries the count.
                    watchedEpisodes += episodeCount;
                } else {
                    watchedEpisodes += watchedBySeason.getOrDefault(
                            seasonKey(serie.getId(), seasonNumber), 0);
                }
            }

            boolean fullyWatched = serie.isWatched();
            if (!fullyWatched && watchedEpisodes == 0) {
                continue; // never started: not part of the watched list
            }
            if (fullyWatched || watchedEpisodes > totalEpisodes) {
                // The aggregated flag is authoritative, and it also covers missing season counts.
                watchedEpisodes = totalEpisodes;
            }

            items.add(new WatchedMediaItem(
                    serie.getId(),
                    serie.getName(),
                    serie.getPosterPath(),
                    FragmentType.SERIE,
                    null,
                    serie.getVoteAverage(),
                    yearOf(serie.getFirstAirDate()),
                    firstValue(serie.getDirectors()),
                    serie.getGenres(),
                    serie.getSeasonCount(),
                    watchedEpisodes,
                    totalEpisodes,
                    fullyWatched));
        }
        return sortedByTitle(items);
    }

    /** Watched episode count per (serie, season). Every stored row means a watched episode. */
    private static Map<Long, Integer> watchedEpisodesBySeason(List<EpisodeDatabase> episodes) {
        Map<Long, Integer> counts = new HashMap<>();
        if (episodes == null) {
            return counts;
        }
        for (EpisodeDatabase episode : episodes) {
            counts.merge(seasonKey(episode.getSerieId(), episode.getSeasonNumber()), 1, Integer::sum);
        }
        return counts;
    }

    private static Map<Integer, List<SeasonDatabase>> seasonsBySerie(List<SeasonDatabase> seasons) {
        Map<Integer, List<SeasonDatabase>> grouped = new HashMap<>();
        if (seasons == null) {
            return grouped;
        }
        for (SeasonDatabase season : seasons) {
            grouped.computeIfAbsent(season.getSerieId(), key -> new ArrayList<>()).add(season);
        }
        return grouped;
    }

    private static List<WatchedMediaItem> sortedByTitle(List<WatchedMediaItem> items) {
        items.sort((left, right) -> AlphabetUtils.compareTitles(left.title(), right.title()));
        return items;
    }

    /** Compact composite key for a (serieId, seasonNumber) pair. */
    private static long seasonKey(Integer serieId, Integer seasonNumber) {
        return (((long) serieId) << 32) ^ (seasonNumber & 0xFFFFFFFFL);
    }

    private static Integer yearOf(LocalDate date) {
        return date != null ? date.getYear() : null;
    }

    /** The stored list fields are comma separated without spaces; only the first entry is shown. */
    private static String firstValue(String list) {
        if (list == null || list.isBlank()) {
            return null;
        }
        int comma = list.indexOf(',');
        String first = (comma >= 0 ? list.substring(0, comma) : list).trim();
        return first.isEmpty() ? null : first;
    }
}
