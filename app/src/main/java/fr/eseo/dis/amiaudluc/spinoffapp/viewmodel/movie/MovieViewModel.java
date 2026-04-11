package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.movie;

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
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.WatchProvider;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.MovieRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.tmdb.ApiRepository;
import lombok.Getter;

public class MovieViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> movieIdTrigger = new MutableLiveData<>();

    @Getter
    private final LiveData<Movie> movie;
    @Getter
    private final LiveData<List<WatchProvider>> movieWatchProviders;
    @Getter
    private final LiveData<List<MovieDatabase>> databaseMovies;
    @Getter
    private final LiveData<MovieStats> movieStats;

    private final MovieRepository movieRepository;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        ApiRepository apiRepository = ApiRepository.getInstance();
        this.movieRepository = MovieRepository.getRepository(application);

        this.movie = Transformations.switchMap(movieIdTrigger, apiRepository::getMovieById);
        this.movieWatchProviders = Transformations.switchMap(movieIdTrigger, apiRepository::fetchMovieWatchProvider);

        this.databaseMovies = movieRepository.fetchAll();
        this.movieStats = Transformations.map(databaseMovies, this::calculateStats);
    }

    private MovieStats calculateStats(List<MovieDatabase> movies) {
        if (movies == null) return null;

        MovieStats stats = new MovieStats();
        int totalRuntimeMinutes = 0;
        int watchlistCount = 0;
        Map<String, Integer> genreCounts = new HashMap<>();
        Map<String, Integer> combinationCounts = new HashMap<>();
        Map<Integer, Integer> yearCounts = new HashMap<>();

        for (MovieDatabase movie : movies) {
            if (movie.isWatched()) {
                int runtime = movie.getRuntime() != null ? movie.getRuntime() : 0;
                totalRuntimeMinutes += runtime;
                stats.setTotalMovies(stats.getTotalMovies() + 1);

                // Genres and Combinations
                if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
                    String genresStr = movie.getGenres();
                    combinationCounts.put(genresStr, combinationCounts.getOrDefault(genresStr, 0) + 1);

                    String[] genres = genresStr.split(",");
                    for (String genre : genres) {
                        String trimmedGenre = genre.trim();
                        genreCounts.put(trimmedGenre, genreCounts.getOrDefault(trimmedGenre, 0) + 1);
                    }
                }

                // Years
                if (movie.getReleaseDate() != null) {
                    int year = movie.getReleaseDate().getYear();
                    yearCounts.put(year, yearCounts.getOrDefault(year, 0) + 1);
                }
            } else {
                watchlistCount++;
            }
        }

        stats.setTotalRuntime(totalRuntimeMinutes);
        stats.setWatchlistCount(watchlistCount);

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

    // single page

    public void initGetMovieById(Integer id) {
        movieIdTrigger.setValue(id);
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
}
