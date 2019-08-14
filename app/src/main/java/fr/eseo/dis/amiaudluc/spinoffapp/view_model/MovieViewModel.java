package fr.eseo.dis.amiaudluc.spinoffapp.view_model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.api.enums.MovieType;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.repository.ApiRepository;

public class MovieViewModel extends ViewModel {
    @NonNull
    private LiveData<List<Movie>> movies;
    private LiveData<Movie> movie;

    private ApiRepository apiRepository;

    public MovieViewModel(ApiRepository apiRepository) {
        this.movies = new MutableLiveData<>();
        this.apiRepository = apiRepository;
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

    @NonNull
    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public LiveData<Movie> getMovie() {
        return movie;
    }
}
