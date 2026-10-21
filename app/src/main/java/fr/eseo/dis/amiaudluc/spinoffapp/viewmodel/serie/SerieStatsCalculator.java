package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.EpisodeDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SeasonDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.StatUtils;

/**
 * Pure aggregation of the series / seasons / episodes tables into a {@link SerieStats}.
 * Stateless and side-effect free: it can run on any thread and be unit tested in isolation.
 */
public final class SerieStatsCalculator {

    private SerieStatsCalculator() {
        // utility class
    }

    public static SerieStats calculate(
            List<SerieDatabase> series,
            List<SeasonDatabase> seasons,
            List<EpisodeDatabase> episodes
    ) {
        if (series == null) return null;

        SerieStats stats = new SerieStats();

        // Watched episode count per (serie, season), keyed by a compact long to avoid String allocation.
        Map<Long, Integer> watchedBySeason = new HashMap<>();
        if (episodes != null) {
            for (EpisodeDatabase episode : episodes) {
                // Every stored episode row means the episode has been watched
                watchedBySeason.merge(
                        seasonKey(episode.getSerieId(), episode.getSeasonNumber()), 1, Integer::sum);
            }
        }

        // Seasons grouped by serie, so the main loop stays a single pass.
        Map<Integer, List<SeasonDatabase>> seasonsBySerie = new HashMap<>();
        if (seasons != null) {
            for (SeasonDatabase season : seasons) {
                seasonsBySerie.computeIfAbsent(season.getSerieId(), k -> new ArrayList<>()).add(season);
            }
        }

        Map<String, Integer> genreCounts = new HashMap<>();
        Map<String, Integer> combinationCounts = new HashMap<>();
        Map<String, Integer> decadeCounts = new HashMap<>();
        List<Double> ratings = new ArrayList<>();
        Map<String, Integer> actorCounts = new HashMap<>();
        Map<String, Integer> directorCounts = new HashMap<>();
        Map<String, Integer> actorNetworkCounts = new HashMap<>();
        Map<String, Integer> originCounts = new HashMap<>();
        Map<String, Integer> languageCounts = new HashMap<>();

        long totalRuntimeMinutes = 0;
        int totalEpisodes = 0;
        int totalSeasons = 0;
        int totalSeries = 0;
        int watchlistCount = 0;

        for (SerieDatabase serie : series) {
            int watchedEpisodesInSerie = 0;
            int watchedSeasonsInSerie = 0;

            List<SeasonDatabase> serieSeasons = seasonsBySerie.get(serie.getId());
            if (serieSeasons != null) {
                for (SeasonDatabase season : serieSeasons) {
                    int episodeCount = season.getEpisodeCount() != null ? season.getEpisodeCount() : 0;
                    int watchedInSeason = watchedBySeason.getOrDefault(
                            seasonKey(serie.getId(), season.getSeasonNumber()), 0);
                    if (season.isWatched() || (episodeCount > 0 && watchedInSeason >= episodeCount)) {
                        watchedEpisodesInSerie += episodeCount;
                        watchedSeasonsInSerie++;
                    } else {
                        watchedEpisodesInSerie += watchedInSeason;
                    }
                }
            }

            totalEpisodes += watchedEpisodesInSerie;
            totalSeasons += watchedSeasonsInSerie;

            boolean hasStartedWatching = serie.isWatched() || watchedEpisodesInSerie > 0;
            if (!hasStartedWatching) {
                watchlistCount++;
                continue;
            }

            totalSeries++;
            stats.getStartedSerieIds().add(serie.getId());

            if (serie.isWatched()) {
                totalRuntimeMinutes += serie.getRuntime();
            } else if (serie.getEpisodeCount() != null && serie.getEpisodeCount() > 0) {
                double averageRuntime = (double) serie.getRuntime() / serie.getEpisodeCount();
                totalRuntimeMinutes += (int) (averageRuntime * watchedEpisodesInSerie);
            }

            // Genres and Combinations
            if (serie.getGenres() != null && !serie.getGenres().isEmpty()) {
                String genresStr = serie.getGenres();
                StatUtils.fillMapCount(combinationCounts, Set.of(genresStr));
                StatUtils.fillMapCount(genreCounts, StatUtils.extractTrimmedValues(genresStr));
            }

            // Decades
            if (serie.getFirstAirDate() != null) {
                int decadeStart = (serie.getFirstAirDate().getYear() / 10) * 10;
                StatUtils.fillMapCount(decadeCounts, Set.of(decadeStart + "s"));
            }

            // Ratings
            if (serie.getVoteAverage() != null && serie.getVoteAverage() > 0) {
                ratings.add(serie.getVoteAverage());
            }

            // Actors & Network
            if (serie.getActors() != null && !serie.getActors().isEmpty()) {
                Set<String> trimmedActors = StatUtils.extractTrimmedValues(serie.getActors());
                StatUtils.fillMapCount(actorCounts, trimmedActors);
                // Network (pairs)
                StatUtils.fillMapCount(
                        actorNetworkCounts,
                        StatUtils.extractPairs(trimmedActors).stream()
                                .map(pair -> pair.first + " & " + pair.second)
                                .collect(Collectors.toSet())
                );
            }

            // Directors
            if (serie.getDirectors() != null && !serie.getDirectors().isEmpty()) {
                StatUtils.fillMapCount(directorCounts, StatUtils.extractTrimmedValues(serie.getDirectors()));
            }

            // Production countries
            if (serie.getProductionCountries() != null && !serie.getProductionCountries().isEmpty()) {
                StatUtils.fillMapCount(originCounts, StatUtils.extractTrimmedValues(serie.getProductionCountries()));
            }

            // Language
            if (serie.getOriginalLanguage() != null) {
                StatUtils.fillMapCount(languageCounts, Set.of(serie.getOriginalLanguage().toUpperCase()));
            }
        }

        stats.setTotalMinutes(totalRuntimeMinutes);
        stats.setTotalEpisodes(totalEpisodes);
        stats.setTotalSeasons(totalSeasons);
        stats.setTotalSeries(totalSeries);
        stats.setWatchlistCount(watchlistCount);

        // Process Genres
        if (!genreCounts.isEmpty()) {
            stats.setTopGenres(StatUtils.topNWithOther(genreCounts, 8));
            stats.setTopGenre(stats.getTopGenres().get(0).getKey());
        }

        // Process Combinations
        if (!combinationCounts.isEmpty()) {
            stats.setTopCombination(combinationCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(""));
        }

        // Process Decades
        if (!decadeCounts.isEmpty()) {
            stats.setTopDecades(StatUtils.topNWithOther(decadeCounts, 8));
            stats.setTopDecade(stats.getTopDecades().get(0).getKey());
        }

        // Process Ratings
        if (!ratings.isEmpty()) {
            Collections.sort(ratings);
            stats.setMeanRating(ratings.stream().mapToDouble(d -> d).average().orElse(0.0));
            stats.setMedianRating(StatUtils.computeMedian(ratings));
        }

        // Process Actors & Directors
        if (!actorCounts.isEmpty()) {
            stats.setTopActors(StatUtils.topNWithOther(actorCounts, 5));
        }
        if (!directorCounts.isEmpty()) {
            stats.setTopDirectors(StatUtils.topNWithOther(directorCounts, 5));
        }

        // Process Network
        if (!actorNetworkCounts.isEmpty()) {
            stats.setTopActorNetwork(actorNetworkCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElseGet(() -> Map.entry("", 0)));
        }

        // Process Origins & Languages
        if (!originCounts.isEmpty()) {
            stats.setTopOrigins(originCounts.entrySet().stream()
                    .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                    .collect(Collectors.toList()));
            stats.setTopOrigin(stats.getTopOrigins().get(0).getKey());
        }
        if (!languageCounts.isEmpty()) {
            stats.setTopLanguages(StatUtils.topNWithOther(languageCounts, 5));
            stats.setTopLanguage(stats.getTopLanguages().get(0).getKey());
        }

        return stats;
    }

    /**
     * Compact composite key for a (serieId, seasonNumber) pair.
     * Both values are non-null (NOT NULL columns), so the unboxing is safe.
     */
    private static long seasonKey(Integer serieId, Integer seasonNumber) {
        return (((long) serieId) << 32) ^ (seasonNumber & 0xFFFFFFFFL);
    }
}
