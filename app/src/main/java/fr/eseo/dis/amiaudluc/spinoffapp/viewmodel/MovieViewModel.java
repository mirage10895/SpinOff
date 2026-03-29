package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel;

import android.app.Application;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.WatchProvider;
import fr.eseo.dis.amiaudluc.spinoffapp.api.enums.MovieType;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.ApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.MovieRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieAdapterData;
import lombok.Getter;

public class MovieViewModel extends AndroidViewModel {
    
    private final MutableLiveData<Integer> movieIdTrigger = new MutableLiveData<>();
    private final MutableLiveData<MovieRequest> moviesListTrigger = new MutableLiveData<>();

    @Getter private final LiveData<List<Movie>> movies;
    @Getter private final MediatorLiveData<List<MovieAdapterData>> movieAdapterData;
    @Getter private final LiveData<Movie> movie;
    @Getter private final LiveData<List<WatchProvider>> movieWatchProviders;
    @Getter private final LiveData<List<MovieDatabase>> databaseMovies;

    private final ApiRepository apiRepository;
    private final MovieRepository movieRepository;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        this.apiRepository = ApiRepository.getInstance();
        this.movieRepository = new MovieRepository(application);

        this.movie = Transformations.switchMap(movieIdTrigger, apiRepository::getMovieById);
        this.movieWatchProviders = Transformations.switchMap(movieIdTrigger, apiRepository::fetchMovieWatchProvider);
        
        this.movies = Transformations.switchMap(moviesListTrigger, req -> 
            apiRepository.getMoviesByType(req.type, req.page, req.previous)
        );

        this.databaseMovies = movieRepository.fetchAll();

        this.movieAdapterData = new MediatorLiveData<>();
        this.movieAdapterData.addSource(
                this.movies,
                movies -> {
                    List<MovieDatabase> databaseMovies = this.databaseMovies.getValue();
                    if (databaseMovies != null) {
                        this.movieAdapterData.setValue(
                                movies.stream()
                                        .map(serieToSerieAdapterData(databaseMovies))
                                        .collect(Collectors.toList())
                        );
                    }
                }
        );
        this.movieAdapterData.addSource(
                this.databaseMovies,
                databaseMovies -> {
                    List<Movie> movies = this.movies.getValue();
                    if (movies != null) {
                        this.movieAdapterData.setValue(
                                movies.stream()
                                        .map(serieToSerieAdapterData(databaseMovies))
                                        .collect(Collectors.toList())
                        );
                    }
                }
        );
    }

    public void initPopularMovies(Integer page) {
        moviesListTrigger.setValue(new MovieRequest(MovieType.POPULAR.getName(), page, movies.getValue()));
    }

    public void initTopRatedMovies(Integer page) {
        moviesListTrigger.setValue(new MovieRequest(MovieType.TOP_RATED.getName(), page, movies.getValue()));
    }

    public void initOnAirMovies(Integer page) {
        moviesListTrigger.setValue(new MovieRequest(MovieType.ON_AIR.getName(), page, movies.getValue()));
    }

    public void initGetMovieById(Integer id) {
        movieIdTrigger.setValue(id);
    }

    public void insert(int movieId) {
        this.movieRepository.insert(movieId);
    }

    public void deleteMovieById(int id) {
        this.movieRepository.deleteMovieById(id);
    }

    public void toggleMovieIsWatched(int id) {
        this.movieRepository.toggleMovieIsWatched(id);
    }

    private static class MovieRequest {
        String type; Integer page; List<Movie> previous;
        MovieRequest(String t, Integer p, List<Movie> prev) { type=t; page=p; previous=prev; }
    }

    private static Function<Movie, MovieAdapterData> serieToSerieAdapterData(List<MovieDatabase> databaseSeries) {
        return movie1 -> MovieAdapterData.of(
                movie1.getId(),
                movie1.getPosterPath(),
                databaseSeries.stream()
                        .anyMatch(serieDatabase -> serieDatabase.getId().equals(movie1.getId())),
                databaseSeries.stream()
                        .anyMatch(serieDatabase -> serieDatabase.getId().equals(movie1.getId()))
        );
    }
}
