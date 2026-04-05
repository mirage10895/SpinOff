package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery;

import android.app.Application;
import android.support.annotation.NonNull;

import java.util.List;

import androidx.lifecycle.LiveData;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.tmdb.ApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.tmdb.data.SerieType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.pagesearch.PageSearchViewModel;

public class SerieDiscoveryViewModel extends PageSearchViewModel<Serie, DiscoveryType> {
    private final ApiRepository apiRepository;

    public SerieDiscoveryViewModel(@NonNull Application application) {
        super(application);
        this.apiRepository = ApiRepository.getInstance();
    }

    @Override
    protected LiveData<List<Serie>> searchApi(DiscoveryType filter, int pageNumber) {
        return this.apiRepository.discoverSerie(
                SerieType.valueOf(filter.name()).getDiscoverFilters().apply(pageNumber)
        );
    }
}
