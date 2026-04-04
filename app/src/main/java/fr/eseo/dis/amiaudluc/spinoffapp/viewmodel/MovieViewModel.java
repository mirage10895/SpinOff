package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel;

import android.app.Application;

import java.util.List;

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
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.tmdb.data.MovieType;
import lombok.Getter;

public class MovieViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> movieIdTrigger = new MutableLiveData<>();
    private final MutableLiveData<MovieRequest> moviesListTrigger = new MutableLiveData<>();

    @Getter
    private final LiveData<List<Movie>> movies;
    @Getter
    private final LiveData<Movie> movie;
    @Getter
    private final LiveData<List<WatchProvider>> movieWatchProviders;
    @Getter
    private final LiveData<List<MovieDatabase>> databaseMovies;

    private final ApiRepository apiRepository;
    private final MovieRepository movieRepository;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        this.apiRepository = ApiRepository.getInstance();
        this.movieRepository = MovieRepository.getRepository(application);

        this.movie = Transformations.switchMap(movieIdTrigger, apiRepository::getMovieById);
        this.movieWatchProviders = Transformations.switchMap(movieIdTrigger, apiRepository::fetchMovieWatchProvider);

        this.movies = Transformations.switchMap(moviesListTrigger, req ->
                apiRepository.getMoviesByType(req.type, req.page, req.previous)
        );

        this.databaseMovies = movieRepository.fetchAll();
    }

    public void initPopularMovies(Integer page) {
        moviesListTrigger.setValue(new MovieRequest(MovieType.POPULAR, page, movies.getValue()));
    }

    public void initTopRatedMovies(Integer page) {
        moviesListTrigger.setValue(new MovieRequest(MovieType.TOP_RATED, page, movies.getValue()));
    }

    public void initOnAirMovies(Integer page) {
        moviesListTrigger.setValue(new MovieRequest(MovieType.ON_AIR, page, movies.getValue()));
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
        MovieType type;
        Integer page;
        List<Movie> previous;

        MovieRequest(MovieType t, Integer p, List<Movie> prev) {
            type = t;
            page = p;
            previous = prev;
        }
    }
}
