package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie;

import android.app.Application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.WatchProvider;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.SerieRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.tmdb.ApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.tmdb.data.SerieType;
import lombok.Getter;

public class SerieViewModel extends AndroidViewModel {

    // Input triggers
    private final MutableLiveData<Integer> serieIdTrigger = new MutableLiveData<>();
    private final MutableLiveData<SeasonRequest> seasonTrigger = new MutableLiveData<>();
    private final MutableLiveData<EpisodeRequest> episodeTrigger = new MutableLiveData<>();

    @Getter
    private final MutableLiveData<SerieType> serieType = new MutableLiveData<>(SerieType.POPULAR);

    // Stable LiveData for UI observation
    @Getter
    private final LiveData<Serie> serie;
    @Getter
    private final LiveData<List<WatchProvider>> serieWatchProviders;
    @Getter
    private final LiveData<Season> season;
    @Getter
    private final LiveData<Episode> episode;
    @Getter
    private final LiveData<List<SerieDatabase>> databaseSeries;
    @Getter
    private final LiveData<SerieStats> serieStats;

    private final ApiRepository apiRepository;
    private final SerieRepository serieRepository;

    public SerieViewModel(@NonNull Application application) {
        super(application);
        this.apiRepository = ApiRepository.getInstance();
        this.serieRepository = SerieRepository.getRepository(application);

        // Wiring triggers to repository calls using switchMap
        this.serie = Transformations.switchMap(
                serieIdTrigger,
                apiRepository::getSerieById
        );

        this.serieWatchProviders = Transformations.switchMap(
                serieIdTrigger,
                apiRepository::fetchTvWatchProvider
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
        serieIdTrigger.setValue(id);
    }

    public void initGetSeasonBySerieId(Integer id, Integer seasonNumber) {
        seasonTrigger.setValue(new SeasonRequest(id, seasonNumber));
    }

    public void initGetEpisodeBySeasonNumberBySerieId(Integer id, Integer seasonNumber, Integer episodeNumber) {
        episodeTrigger.setValue(new EpisodeRequest(id, seasonNumber, episodeNumber));
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

    private static class SeasonRequest {
        Integer id;
        Integer seasonNumber;

        SeasonRequest(Integer i, Integer s) {
            id = i;
            seasonNumber = s;
        }
    }

    private static class EpisodeRequest {
        Integer id;
        Integer seasonNumber;
        Integer episodeNumber;

        EpisodeRequest(Integer i, Integer s, Integer e) {
            id = i;
            seasonNumber = s;
            episodeNumber = e;
        }
    }

    private static SerieStats calculateStats(List<SerieDatabase> series) {
        if (series == null) return null;

        SerieStats stats = new SerieStats();
        int totalRuntimeMinutes = 0;
        Map<String, Integer> genreCounts = new HashMap<>();
        Map<String, Integer> combinationCounts = new HashMap<>();
        Map<Integer, Integer> yearCounts = new HashMap<>();

        for (SerieDatabase serie : series) {
            if (serie.isWatched()) {
                int episodes = serie.getEpisodeCount() != null ? serie.getEpisodeCount() : 0;
                totalRuntimeMinutes += serie.getRuntime();
                stats.setTotalEpisodes(stats.getTotalEpisodes() + episodes);
                stats.setTotalSeries(stats.getTotalSeries() + 1);

                // Genres and Combinations
                if (serie.getGenres() != null && !serie.getGenres().isEmpty()) {
                    String genresStr = serie.getGenres();
                    combinationCounts.put(genresStr, combinationCounts.getOrDefault(genresStr, 0) + 1);

                    String[] genres = genresStr.split(",");
                    for (String genre : genres) {
                        String trimmedGenre = genre.trim();
                        genreCounts.put(trimmedGenre, genreCounts.getOrDefault(trimmedGenre, 0) + 1);
                    }
                }

                // Years
                if (serie.getFirstAirDate() != null) {
                    int year = serie.getFirstAirDate().getYear();
                    yearCounts.put(year, yearCounts.getOrDefault(year, 0) + 1);
                }
            }
        }

        stats.setTotalMinutes(totalRuntimeMinutes);

        // Process Genres
        if (!genreCounts.isEmpty()) {
            stats.setTop3Genres(genreCounts.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .limit(3)
                    .collect(Collectors.toList()));
            stats.setTopGenre(stats.getTop3Genres().get(0).getKey());
        }

        // Process Combinations
        if (!combinationCounts.isEmpty()) {
            stats.setTopCombination(combinationCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(""));
        }

        // Process Years
        if (!yearCounts.isEmpty()) {
            stats.setTop3Years(yearCounts.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .limit(3)
                    .collect(Collectors.toList()));
            stats.setTopYear(stats.getTop3Years().get(0).getKey());
        }

        return stats;
    }
}
