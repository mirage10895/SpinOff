package fr.eseo.dis.amiaudluc.spinoffapp.view_model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.api.enums.SerieType;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.repository.ApiRepository;

public class SerieViewModel extends ViewModel {
    @NonNull
    private LiveData<List<Serie>> series;
    private LiveData<Serie> serie;
    private LiveData<Season> season;

    private ApiRepository apiRepository;

    public SerieViewModel(ApiRepository apiRepository) {
        this.series = new MutableLiveData<>();
        this.apiRepository = apiRepository;
    }

    public void initPopularSeries(Integer page) {
        this.series = this.apiRepository.getSeriesByType(SerieType.POPULAR.getName(), page, this.series.getValue());
    }

    public void initTopRatedSeries(Integer page) {
        this.series = this.apiRepository.getSeriesByType(SerieType.TOP_RATED.getName(), page, this.series.getValue());
    }

    public void initOnAirSeries(Integer page) {
        this.series = this.apiRepository.getSeriesByType(SerieType.ON_AIR.getName(), page, this.series.getValue());
    }

    public void initGetSerieById(Integer id) {
        this.serie = this.apiRepository.getSerieById(id);
    }

    public void initGetSeasonBySerieId(Integer id, Integer seasonNumber) {
        this.season = this.apiRepository.getSeasonBySerieId(id, seasonNumber);
    }

    @NonNull
    public LiveData<List<Serie>> getSeries() {
        return series;
    }

    public LiveData<Serie> getSerie() {
        return serie;
    }

    public LiveData<Season> getSeason() {
        return season;
    }
}
