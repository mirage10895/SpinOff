package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery;

import android.app.Application;
import android.support.annotation.NonNull;

import java.util.List;

import androidx.lifecycle.LiveData;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.TmdbApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryFilter;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.SerieType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.pagesearch.PageSearchViewModel;

public class SerieDiscoveryViewModel extends PageSearchViewModel<Serie, DiscoveryFilter> {
    private final TmdbApiRepository apiRepository;

    public SerieDiscoveryViewModel(@NonNull Application application) {
        super(application);
        this.apiRepository = TmdbApiRepository.getInstance();
    }

    @Override
    protected LiveData<List<Serie>> searchApi(DiscoveryFilter filter, int pageNumber) {
        return this.apiRepository.discoverSerie(
                SerieType.valueOf(filter.type().name())
                        .getDiscoverFilters()
                        .apply(pageNumber)
                        .merge(filter.extraFilters())
        );
    }
}
