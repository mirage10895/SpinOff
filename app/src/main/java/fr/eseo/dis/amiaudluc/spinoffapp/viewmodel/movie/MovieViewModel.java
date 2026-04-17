package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.movie;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
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
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.MovieRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter.WatchProviderAdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.StatUtils;
import lombok.Getter;

public class MovieViewModel extends AndroidViewModel {

    private static final String MOVIE_ID_KEY = "movieId";
    private final SavedStateHandle savedStateHandle;

    @Getter
    private final LiveData<Movie> movie;
    @Getter
    private final LiveData<List<WatchProviderAdapterData>> movieWatchProviders;
    @Getter
    private final LiveData<List<MovieDatabase>> databaseMovies;
    @Getter
    private final LiveData<MovieStats> movieStats;

    private final MovieRepository movieRepository;

    public MovieViewModel(@NonNull Application application, @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        this.savedStateHandle = savedStateHandle;
        LiveData<Integer> movieIdTrigger = savedStateHandle.getLiveData(MOVIE_ID_KEY);

        TmdbApiRepository apiRepository = TmdbApiRepository.getInstance();
        this.movieRepository = MovieRepository.getRepository(application);

        this.movie = Transformations.switchMap(movieIdTrigger, apiRepository::getMovieById);
        this.movieWatchProviders = Transformations.switchMap(
                movie,
                fetched -> apiRepository.fetchMovieWatchProvider(fetched.getId(), fetched.getImdbId())
        );

        this.databaseMovies = movieRepository.fetchAll();
        this.movieStats = Transformations.map(databaseMovies, MovieViewModel::calculateStats);
    }

    // single page

    public void initGetMovieById(Integer id) {
        if (!id.equals(savedStateHandle.get(MOVIE_ID_KEY))) {
            savedStateHandle.set(MOVIE_ID_KEY, id);
        }
    }

    // database action

    public void insert(int movieId) {
        this.movieRepository.insert(movieId);
    }

    public void deleteMovieById(int id) {
        this.movieRepository.deleteMovieById(id);
    }

    public void toggleMovieIsWatched(int id) {
        this.movieRepository.toggleMovieIsWatched(id);
    }

    // helper methods
    private static MovieStats calculateStats(List<MovieDatabase> movies) {
        if (movies == null) return null;

        MovieStats stats = new MovieStats();
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

        for (MovieDatabase movie : movies) {
            if (movie.isWatched()) {
                int runtime = movie.getRuntime() != null ? movie.getRuntime() : 0;
                totalRuntimeMinutes += runtime;
                stats.setTotalMovies(stats.getTotalMovies() + 1);

                // Genres and Combinations
                if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
                    String genresStr = movie.getGenres();
                    StatUtils.fillMapCount(combinationCounts, Set.of(genresStr));

                    Set<String> genres = StatUtils.extractTrimmedValues(genresStr);
                    StatUtils.fillMapCount(genreCounts, genres);
                }

                // Decades
                if (movie.getReleaseDate() != null) {
                    int year = movie.getReleaseDate().getYear();
                    int decadeStart = (year / 10) * 10;
                    String decade = decadeStart + "s";
                    StatUtils.fillMapCount(decadeCounts, Set.of(decade));
                }

                // Ratings
                if (movie.getVoteAverage() != null && movie.getVoteAverage() > 0) {
                    ratings.add(movie.getVoteAverage());
                }

                // Actors & Network
                if (movie.getActors() != null && !movie.getActors().isEmpty()) {
                    Set<String> trimmedActors = StatUtils.extractTrimmedValues(movie.getActors());
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
                if (movie.getDirectors() != null && !movie.getDirectors().isEmpty()) {
                    Set<String> trimmedDirectors = StatUtils.extractTrimmedValues(movie.getDirectors());
                    StatUtils.fillMapCount(directorCounts, trimmedDirectors);
                }

                // Production countries
                if (movie.getProductionCountries() != null && !movie.getProductionCountries().isEmpty()) {
                    Set<String> countries = StatUtils.extractTrimmedValues(movie.getProductionCountries());
                    StatUtils.fillMapCount(originCounts, countries);
                }

                // Language
                if (movie.getOriginalLanguage() != null) {
                    StatUtils.fillMapCount(languageCounts, Set.of(movie.getOriginalLanguage().toUpperCase()));
                }
            } else {
                watchlistCount++;
            }
        }

        stats.setTotalRuntime(totalRuntimeMinutes);
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
            stats.setTopActorNetwork(
                    actorNetworkCounts.entrySet().stream()
                            .max(Map.Entry.comparingByValue())
                            .orElseGet(() -> Map.entry("", 0))
            );
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
