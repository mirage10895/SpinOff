package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery;

import android.app.Application;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.TmdbApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.SerieRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryMedia;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.SerieType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.pagesearch.PageSearchViewModel;

public class SerieDiscoveryViewModel extends PageSearchViewModel<Serie, DiscoveryType> {
    private final TmdbApiRepository apiRepository;
    private final SerieRepository serieRepository;

    private final MediatorLiveData<List<DiscoveryMedia<Serie>>> combinedSeries = new MediatorLiveData<>();

    public SerieDiscoveryViewModel(@NonNull Application application) {
        super(application);
        this.apiRepository = TmdbApiRepository.getInstance();
        this.serieRepository = SerieRepository.getRepository(application);

        LiveData<List<SerieDatabase>> storedSeries = this.serieRepository.fetchAll();
        this.combinedSeries.addSource(getResults(), series -> combine(series, storedSeries.getValue()));
        this.combinedSeries.addSource(storedSeries, dbSeries -> combine(getResults().getValue(), dbSeries));
    }

    public LiveData<List<DiscoveryMedia<Serie>>> getCombinedSeries() {
        return combinedSeries;
    }

    @Override
    protected LiveData<List<Serie>> searchApi(DiscoveryType filter, int pageNumber) {
        return this.apiRepository.discoverSerie(
                SerieType.valueOf(filter.name()).getDiscoverFilters().apply(pageNumber)
        );
    }

    private void combine(List<Serie> series, List<SerieDatabase> storedSeries) {
        if (series == null) return;
        List<SerieDatabase> finalStoredSeries = storedSeries != null ? storedSeries : Collections.emptyList();

        List<DiscoveryMedia<Serie>> displayModels = series.stream()
                .map(s -> {
                    Optional<SerieDatabase> serieDatabase = finalStoredSeries.stream()
                            .filter(p -> p.getId().equals(s.getId()))
                            .findFirst();

                    return new DiscoveryMedia<>(
                            s,
                            serieDatabase.isPresent(),
                            serieDatabase.map(SerieDatabase::isWatched).orElse(false)
                    );
                })
                .collect(Collectors.toList());

        combinedSeries.setValue(displayModels);
    }
}
