package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.strategy;

import androidx.lifecycle.LiveData;
import java.util.List;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.TmdbApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.DiscoverFilters;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Genre;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryType;

public interface DiscoveryStrategy {
    DiscoverFilters.DiscoverFiltersBuilder getBaseFilters(DiscoveryType type);
    void applyYear(DiscoverFilters.DiscoverFiltersBuilder builder, Integer year);
    Integer getYear(DiscoverFilters filters);
    LiveData<List<Genre>> getGenres(TmdbApiRepository repository);
    List<RuntimeFilter> getRuntimeFilters();

    static DiscoveryStrategy from(FragmentType type) {
        if (type == FragmentType.MOVIE) {
            return new MovieDiscoveryStrategy();
        } else if (type == FragmentType.SERIE) {
            return new SerieDiscoveryStrategy();
        }
        throw new IllegalArgumentException("Unsupported fragment type for discovery: " + type);
    }
}
