package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery;

import android.app.Application;
import android.support.annotation.NonNull;

import java.util.List;

import androidx.lifecycle.LiveData;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.tmdb.ApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.tmdb.data.MovieType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.pagesearch.PageSearchViewModel;

public class MovieDiscoveryViewModel extends PageSearchViewModel<Movie, DiscoveryType> {
    private final ApiRepository apiRepository;

    public MovieDiscoveryViewModel(@NonNull Application application) {
        super(application);
        this.apiRepository = ApiRepository.getInstance();
    }

    @Override
    protected LiveData<List<Movie>> searchApi(DiscoveryType filter, int pageNumber) {
        return this.apiRepository.discoverMovie(
                MovieType.valueOf(filter.name()).getDiscoverFilters().apply(pageNumber)
        );
    }
}
