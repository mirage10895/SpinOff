package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.enums.SerieType;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.ApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.SerieRepository;
import lombok.Getter;

public class SerieViewModel extends AndroidViewModel {
    @Getter
    private LiveData<List<Serie>> series;
    @Getter
    private LiveData<Serie> serie;
    @Getter
    private LiveData<Season> season;
    @Getter
    private LiveData<Episode> episode;
    @Getter
    private LiveData<List<SerieDatabase>> databaseSeries;

    private final ApiRepository apiRepository;
    private final SerieRepository serieRepository;

    public SerieViewModel(@NonNull Application application) {
        super(application);
        this.series = new MutableLiveData<>();
        this.serie = new MutableLiveData<>();
        this.databaseSeries = new MutableLiveData<>();
        this.apiRepository = ApiRepository.getInstance();
        this.serieRepository = new SerieRepository(application);
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

    public void initGetEpisodeBySeasonNumberBySerieId(Integer id, Integer seasonNumber, Integer episodeNumber) {
        this.episode = this.apiRepository.getEpisodeBySeasonNumberBySerieId(id, seasonNumber, episodeNumber);
    }

    public void initDatabaseSeries() {
        this.databaseSeries = this.serieRepository.fetchAll();
    }

    public void updateAllSeries() {
        this.serieRepository.updateAllSeries();
    }

    public void insert(int serieId) {
        this.serieRepository.insert(serieId);
    }

    public void deleteById(int id) {
        this.serieRepository.deleteById(id);
    }

    public void toggleSerieIsWatched(int id) {
        this.serieRepository.toggleSerieIsWatched(id);
    }
}
