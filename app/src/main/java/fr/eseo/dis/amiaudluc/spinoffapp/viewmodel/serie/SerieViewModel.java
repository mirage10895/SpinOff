package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.TmdbApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.SerieRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter.WatchProviderAdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.StatUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.SerieType;
import lombok.Getter;

public class SerieViewModel extends AndroidViewModel {

    private static final String SERIE_ID_KEY = "serieId";
    private static final String SEASON_REQ_KEY = "seasonReq";
    private static final String EPISODE_REQ_KEY = "episodeReq";

    private final SavedStateHandle savedStateHandle;

    @Getter
    private final MutableLiveData<SerieType> serieType = new MutableLiveData<>(SerieType.POPULAR);

    // Stable LiveData for UI observation
    @Getter
    private final LiveData<Serie> serie;
    @Getter
    private final LiveData<List<WatchProviderAdapterData>> serieWatchProviders;
    @Getter
    private final LiveData<Season> season;
    @Getter
    private final LiveData<Episode> episode;
    @Getter
    private final LiveData<List<SerieDatabase>> databaseSeries;
    @Getter
    private final LiveData<SerieStats> serieStats;

    private final TmdbApiRepository apiRepository;
    private final SerieRepository serieRepository;

    public SerieViewModel(@NonNull Application application, @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        this.savedStateHandle = savedStateHandle;
        // Input triggers
        LiveData<Integer> serieIdTrigger = savedStateHandle.getLiveData(SERIE_ID_KEY);
        LiveData<SeasonRequest> seasonTrigger = savedStateHandle.getLiveData(SEASON_REQ_KEY);
        LiveData<EpisodeRequest> episodeTrigger = savedStateHandle.getLiveData(EPISODE_REQ_KEY);

        this.apiRepository = TmdbApiRepository.getInstance();
        this.serieRepository = SerieRepository.getRepository(application);

        // Wiring triggers to repository calls using switchMap
        this.serie = Transformations.switchMap(
                serieIdTrigger,
                apiRepository::getSerieById
        );

        this.serieWatchProviders = Transformations.switchMap(
                serie,
                fetchedSerie -> apiRepository.fetchTvWatchProvider(fetchedSerie.getId(), fetchedSerie.getExternalIds().imdbId())
        );

        this.season = Transformations.switchMap(seasonTrigger, req ->
                apiRepository.getSeasonBySerieId(req.id, req.seasonNumber)
        );

        this.episode = Transformations.switchMap(episodeTrigger, req ->
                apiRepository.getEpisodeBySeasonNumberBySerieId(req.id, req.seasonNumber, req.episodeNumber)
        );

        this.databaseSeries = serieRepository.fetchAll();
        this.serieStats = Transformations.map(databaseSeries, SerieViewModel::calculateStats);
    }

    // Public API to trigger data fetch
    public void initGetSerieById(Integer id) {
        if (!id.equals(savedStateHandle.get(SERIE_ID_KEY))) {
            savedStateHandle.set(SERIE_ID_KEY, id);
        }
    }

    public void initGetSeasonBySerieId(Integer id, Integer seasonNumber) {
        SeasonRequest newReq = new SeasonRequest(id, seasonNumber);
        if (!newReq.equals(savedStateHandle.get(SEASON_REQ_KEY))) {
            savedStateHandle.set(SEASON_REQ_KEY, newReq);
        }
    }

    public void initGetEpisodeBySeasonNumberBySerieId(Integer id, Integer seasonNumber, Integer episodeNumber) {
        EpisodeRequest newReq = new EpisodeRequest(id, seasonNumber, episodeNumber);
        if (!newReq.equals(savedStateHandle.get(EPISODE_REQ_KEY))) {
            savedStateHandle.set(EPISODE_REQ_KEY, newReq);
        }
    }

    // Database methods (these usually return stable LiveData from Room)
    public void insert(int serieId) {
        serieRepository.insert(serieId);
    }

    public void deleteById(int id) {
        serieRepository.deleteById(id);
    }

    public void toggleSerieIsWatched(int id) {
        serieRepository.toggleSerieIsWatched(id);
    }

    // Helper classes for complex triggers

    private static class SeasonRequest implements java.io.Serializable {
        Integer id;
        Integer seasonNumber;

        SeasonRequest(Integer i, Integer s) {
            id = i;
            seasonNumber = s;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SeasonRequest that = (SeasonRequest) o;
            return java.util.Objects.equals(id, that.id) &&
                    java.util.Objects.equals(seasonNumber, that.seasonNumber);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(id, seasonNumber);
        }
    }

    private static class EpisodeRequest implements java.io.Serializable {
        Integer id;
        Integer seasonNumber;
        Integer episodeNumber;

        EpisodeRequest(Integer i, Integer s, Integer e) {
            id = i;
            seasonNumber = s;
            episodeNumber = e;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EpisodeRequest that = (EpisodeRequest) o;
            return java.util.Objects.equals(id, that.id) &&
                    java.util.Objects.equals(seasonNumber, that.seasonNumber) &&
                    java.util.Objects.equals(episodeNumber, that.episodeNumber);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(id, seasonNumber, episodeNumber);
        }
    }

    // Helper methods
    
    private static SerieStats calculateStats(List<SerieDatabase> series) {
        if (series == null) return null;

        SerieStats stats = new SerieStats();
        int totalRuntimeMinutes = 0;
        int watchlistCount = 0;
        Map<String, Integer> genreCounts = new HashMap<>();
        Map<String, Integer> combinationCounts = new HashMap<>();
        Map<String, Integer> decadeCounts = new HashMap<>();
        List<Double> ratings = new ArrayList<>();
        Map<String, Integer> actorCounts = new HashMap<>();
        Map<String, Integer> directorCounts = new HashMap<>();
        Map<String, Integer> actorNetworkCounts = new HashMap<>();
        Map<String, Integer> originCounts = new HashMap<>();
        Map<String, Integer> languageCounts = new HashMap<>();

        for (SerieDatabase serie : series) {
            if (serie.isWatched()) {
                int episodes = serie.getEpisodeCount() != null ? serie.getEpisodeCount() : 0;
                int seasons = serie.getSeasonCount() != null ? serie.getSeasonCount() : 0;
                totalRuntimeMinutes += serie.getRuntime();
                stats.setTotalEpisodes(stats.getTotalEpisodes() + episodes);
                stats.setTotalSeasons(stats.getTotalSeasons() + seasons);
                stats.setTotalSeries(stats.getTotalSeries() + 1);

                // Genres and Combinations
                if (serie.getGenres() != null && !serie.getGenres().isEmpty()) {
                    String genresStr = serie.getGenres();
                    StatUtils.fillMapCount(combinationCounts, Set.of(genresStr));

                    Set<String> genres = StatUtils.extractTrimmedValues(genresStr);
                    StatUtils.fillMapCount(genreCounts, genres);
                }

                // Decades
                if (serie.getFirstAirDate() != null) {
                    int year = serie.getFirstAirDate().getYear();
                    int decadeStart = (year / 10) * 10;
                    String decade = decadeStart + "s";
                    StatUtils.fillMapCount(decadeCounts, Set.of(decade));
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
                    Set<String> trimmedDirectors = StatUtils.extractTrimmedValues(serie.getDirectors());
                    StatUtils.fillMapCount(directorCounts, trimmedDirectors);
                }

                // Production countries
                if (serie.getProductionCountries() != null && !serie.getProductionCountries().isEmpty()) {
                    Set<String> countries = StatUtils.extractTrimmedValues(serie.getProductionCountries());
                    StatUtils.fillMapCount(originCounts, countries);
                }

                // Language
                if (serie.getOriginalLanguage() != null) {
                    StatUtils.fillMapCount(languageCounts, Set.of(serie.getOriginalLanguage().toUpperCase()));
                }
            } else {
                watchlistCount++;
            }
        }

        stats.setTotalMinutes(totalRuntimeMinutes);
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
}
