package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.enums.MovieType;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.ApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.MovieRepository;
import lombok.Getter;

public class MovieViewModel extends AndroidViewModel {
    @Getter
    private LiveData<List<Movie>> movies;
    @Getter
    private LiveData<Movie> movie;
    @Getter
    private LiveData<List<MovieDatabase>> databaseMovies;

    private final ApiRepository apiRepository;
    private final MovieRepository movieRepository;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        this.movies = new MutableLiveData<>();
        this.movie = new MutableLiveData<>();
        this.databaseMovies = new MutableLiveData<>();
        this.apiRepository = ApiRepository.getInstance();
        this.movieRepository = new MovieRepository(application);
    }

    public void initPopularMovies(Integer page) {
        this.movies = this.apiRepository.getMoviesByType(MovieType.POPULAR.getName(), page, this.movies.getValue());
    }

    public void initTopRatedMovies(Integer page) {
        this.movies = this.apiRepository.getMoviesByType(MovieType.TOP_RATED.getName(), page, this.movies.getValue());
    }

    public void initOnAirMovies(Integer page) {
        this.movies = this.apiRepository.getMoviesByType(MovieType.ON_AIR.getName(), page, this.movies.getValue());
    }

    public void initGetMovieById(Integer id) {
        this.movie = this.apiRepository.getMovieById(id);
    }

    public void initDatabaseMovies() {
        this.databaseMovies = this.movieRepository.fetchAll();
    }

    public void insert(MovieDatabase movie) {
        this.movieRepository.insert(movie);
    }

    public void deleteMovieById(int id) {
        this.movieRepository.deleteMovieById(id);
    }

    public void toggleMovieIsWatched(int id) {
        this.movieRepository.toggleMovieIsWatched(id);
    }
}
