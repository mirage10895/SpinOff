package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.WatchProvider;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.SerieRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.tmdb.ApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.tmdb.data.SerieType;
import lombok.Getter;

public class SerieViewModel extends AndroidViewModel {

    // Input triggers
    private final MutableLiveData<Integer> serieIdTrigger = new MutableLiveData<>();
    private final MutableLiveData<SerieRequest> seriesListTrigger = new MutableLiveData<>();
    private final MutableLiveData<SeasonRequest> seasonTrigger = new MutableLiveData<>();
    private final MutableLiveData<EpisodeRequest> episodeTrigger = new MutableLiveData<>();

    // Stable LiveData for UI observation
    @Getter
    private final LiveData<Serie> serie;
    @Getter
    private final LiveData<List<WatchProvider>> serieWatchProviders;
    @Getter
    private final LiveData<List<Serie>> series;
    @Getter
    private final LiveData<Season> season;
    @Getter
    private final LiveData<Episode> episode;
    @Getter
    private final LiveData<List<SerieDatabase>> databaseSeries;

    private final ApiRepository apiRepository;
    private final SerieRepository serieRepository;

    public SerieViewModel(@NonNull Application application) {
        super(application);
        this.apiRepository = ApiRepository.getInstance();
        this.serieRepository = SerieRepository.getRepository(application);

        // Wiring triggers to repository calls using switchMap
        this.serie = Transformations.switchMap(
                serieIdTrigger,
                apiRepository::getSerieById
        );

        this.serieWatchProviders = Transformations.switchMap(
                serieIdTrigger,
                apiRepository::fetchTvWatchProvider
        );

        this.series = Transformations.switchMap(seriesListTrigger, req ->
                apiRepository.getSeriesByType(req.type, req.page, req.previous)
        );

        this.season = Transformations.switchMap(seasonTrigger, req ->
                apiRepository.getSeasonBySerieId(req.id, req.seasonNumber)
        );

        this.episode = Transformations.switchMap(episodeTrigger, req ->
                apiRepository.getEpisodeBySeasonNumberBySerieId(req.id, req.seasonNumber, req.episodeNumber)
        );

        this.databaseSeries = serieRepository.fetchAll();
    }

    // Public API to trigger data fetch
    public void initGetSerieById(Integer id) {
        serieIdTrigger.setValue(id);
    }

    public void initPopularSeries(Integer page) {
        seriesListTrigger.setValue(new SerieRequest(SerieType.POPULAR, page, series.getValue()));
    }

    public void initTopRatedSeries(Integer page) {
        seriesListTrigger.setValue(new SerieRequest(SerieType.TOP_RATED, page, series.getValue()));
    }

    public void initOnAirSeries(Integer page) {
        seriesListTrigger.setValue(new SerieRequest(SerieType.ON_AIR, page, series.getValue()));
    }

    public void initGetSeasonBySerieId(Integer id, Integer seasonNumber) {
        seasonTrigger.setValue(new SeasonRequest(id, seasonNumber));
    }

    public void initGetEpisodeBySeasonNumberBySerieId(Integer id, Integer seasonNumber, Integer episodeNumber) {
        episodeTrigger.setValue(new EpisodeRequest(id, seasonNumber, episodeNumber));
    }

    // Database methods (these usually return stable LiveData from Room)
    public void insert(int serieId) {
        serieRepository.insert(serieId);
    }

    public void deleteById(int id) {
        serieRepository.deleteById(id);
    }

    public void toggleSerieIsWatched(int id) {
        serieRepository.toggleSerieIsWatched(id);
    }

    // Helper classes for complex triggers
    private static class SerieRequest {
        SerieType type;
        Integer page;
        List<Serie> previous;

        SerieRequest(SerieType t, Integer p, List<Serie> prev) {
            type = t;
            page = p;
            previous = prev;
        }
    }

    private static class SeasonRequest {
        Integer id;
        Integer seasonNumber;

        SeasonRequest(Integer i, Integer s) {
            id = i;
            seasonNumber = s;
        }
    }

    private static class EpisodeRequest {
        Integer id;
        Integer seasonNumber;
        Integer episodeNumber;

        EpisodeRequest(Integer i, Integer s, Integer e) {
            id = i;
            seasonNumber = s;
            episodeNumber = e;
        }
    }
}
