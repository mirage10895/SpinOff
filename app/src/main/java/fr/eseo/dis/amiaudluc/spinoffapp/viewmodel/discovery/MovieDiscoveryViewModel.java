package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery;

import android.app.Application;
import android.support.annotation.NonNull;

import java.util.List;

import androidx.lifecycle.LiveData;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.TmdbApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.MovieType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.pagesearch.PageSearchViewModel;

public class MovieDiscoveryViewModel extends PageSearchViewModel<Movie, DiscoveryType> {
    private final TmdbApiRepository apiRepository;

    public MovieDiscoveryViewModel(@NonNull Application application) {
        super(application);
        this.apiRepository = TmdbApiRepository.getInstance();
    }

    @Override
    protected LiveData<List<Movie>> searchApi(DiscoveryType filter, int pageNumber) {
        return this.apiRepository.discoverMovie(
                MovieType.valueOf(filter.name()).getDiscoverFilters().apply(pageNumber)
        );
    }
}
