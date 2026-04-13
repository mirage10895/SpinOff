package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery;

import android.app.Application;
import android.support.annotation.NonNull;

import java.util.List;

import androidx.lifecycle.LiveData;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.TmdbApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.SerieType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.pagesearch.PageSearchViewModel;

public class SerieDiscoveryViewModel extends PageSearchViewModel<Serie, DiscoveryType> {
    private final TmdbApiRepository apiRepository;

    public SerieDiscoveryViewModel(@NonNull Application application) {
        super(application);
        this.apiRepository = TmdbApiRepository.getInstance();
    }

    @Override
    protected LiveData<List<Serie>> searchApi(DiscoveryType filter, int pageNumber) {
        return this.apiRepository.discoverSerie(
                SerieType.valueOf(filter.name()).getDiscoverFilters().apply(pageNumber)
        );
    }
}
