package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.strategy;

import androidx.lifecycle.LiveData;
import java.util.List;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.TmdbApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.DiscoverFilters;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Genre;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.MovieType;

public class MovieDiscoveryStrategy implements DiscoveryStrategy {
    @Override
    public DiscoverFilters.DiscoverFiltersBuilder getBaseFilters(DiscoveryType type) {
        return MovieType.valueOf(type.name()).getDiscoverFilters().get();
    }

    @Override
    public void applyYear(DiscoverFilters.DiscoverFiltersBuilder builder, Integer year) {
        builder.primaryReleaseYear(year);
    }

    @Override
    public Integer getYear(DiscoverFilters filters) {
        return filters.primaryReleaseYear();
    }

    @Override
    public LiveData<List<Genre>> getGenres(TmdbApiRepository repository) {
        return repository.getMovieGenres();
    }
}
