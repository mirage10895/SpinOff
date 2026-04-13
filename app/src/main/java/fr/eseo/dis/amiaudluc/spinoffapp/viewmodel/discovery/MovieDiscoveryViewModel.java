package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery;

import android.app.Application;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.TmdbApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.MovieRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryMedia;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.MovieType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.pagesearch.PageSearchViewModel;

public class MovieDiscoveryViewModel extends PageSearchViewModel<Movie, DiscoveryType> {
    private final TmdbApiRepository apiRepository;
    private final MovieRepository movieRepository;

    private final MediatorLiveData<List<DiscoveryMedia<Movie>>> combinedMovies = new MediatorLiveData<>();

    public MovieDiscoveryViewModel(@NonNull Application application) {
        super(application);
        this.apiRepository = TmdbApiRepository.getInstance();
        this.movieRepository = MovieRepository.getRepository(application);

        LiveData<List<MovieDatabase>> storedMovies = this.movieRepository.fetchAll();
        this.combinedMovies.addSource(getResults(), movies -> combine(movies, storedMovies.getValue()));
        this.combinedMovies.addSource(storedMovies, dbMovies -> combine(getResults().getValue(), dbMovies));
    }

    public LiveData<List<DiscoveryMedia<Movie>>> getCombinedMovies() {
        return combinedMovies;
    }

    @Override
    protected LiveData<List<Movie>> searchApi(DiscoveryType filter, int pageNumber) {
        return this.apiRepository.discoverMovie(
                MovieType.valueOf(filter.name()).getDiscoverFilters().apply(pageNumber)
        );
    }

    private void combine(List<Movie> movies, List<MovieDatabase> storedMovies) {
        if (movies == null) return;
        List<MovieDatabase> finalStoredMovies = storedMovies != null ? storedMovies : Collections.emptyList();

        List<DiscoveryMedia<Movie>> displayModels = movies.stream()
                .map(m -> {
                    Optional<MovieDatabase> movieDatabase = finalStoredMovies.stream()
                            .filter(p -> p.getId().equals(m.getId()))
                            .findFirst();

                    return new DiscoveryMedia<>(
                            m,
                            movieDatabase.isPresent(),
                            movieDatabase.map(MovieDatabase::isWatched).orElse(false)
                    );
                })
                .collect(Collectors.toList());

        combinedMovies.setValue(displayModels);
    }
}
