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
import lombok.Getter;

public class MovieViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> movieIdTrigger = new MutableLiveData<>();

    @Getter
    private final LiveData<Movie> movie;
    @Getter
    private final LiveData<List<WatchProvider>> movieWatchProviders;
    @Getter
    private final LiveData<List<MovieDatabase>> databaseMovies;

    private final MovieRepository movieRepository;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        ApiRepository apiRepository = ApiRepository.getInstance();
        this.movieRepository = MovieRepository.getRepository(application);

        this.movie = Transformations.switchMap(movieIdTrigger, apiRepository::getMovieById);
        this.movieWatchProviders = Transformations.switchMap(movieIdTrigger, apiRepository::fetchMovieWatchProvider);

        this.databaseMovies = movieRepository.fetchAll();
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
