package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.TmdbApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.EpisodeDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SeasonDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.SerieRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter.WatchProviderAdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.SerieType;
import lombok.Getter;

public class SerieViewModel extends AndroidViewModel {

    private static final String SERIE_ID_KEY = "serieId";
    private static final String SEASON_REQ_KEY = "seasonReq";
    private static final String EPISODE_REQ_KEY = "episodeReq";

    private final SavedStateHandle savedStateHandle;

    @Getter
    private final MutableLiveData<SerieType> serieType = new MutableLiveData<>(SerieType.POPULAR);

    // Stable LiveData for UI observation
    @Getter
    private final LiveData<Serie> serie;
    @Getter
    private final LiveData<List<WatchProviderAdapterData>> serieWatchProviders;
    @Getter
    private final LiveData<Season> season;
    @Getter
    private final LiveData<Episode> episode;
    @Getter
    private final LiveData<List<SerieDatabase>> databaseSeries;
    @Getter
    private final LiveData<SerieStats> serieStats;

    private final TmdbApiRepository apiRepository;
    private final SerieRepository serieRepository;
    private final SerieStatsProvider statsProvider;

    public SerieViewModel(@NonNull Application application, @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        this.savedStateHandle = savedStateHandle;
        // Input triggers
        LiveData<Integer> serieIdTrigger = savedStateHandle.getLiveData(SERIE_ID_KEY);
        LiveData<SeasonRequest> seasonTrigger = savedStateHandle.getLiveData(SEASON_REQ_KEY);
        LiveData<EpisodeRequest> episodeTrigger = savedStateHandle.getLiveData(EPISODE_REQ_KEY);

        this.apiRepository = TmdbApiRepository.getInstance();
        this.serieRepository = SerieRepository.getRepository(application);

        // Wiring triggers to repository calls using switchMap
        this.serie = Transformations.switchMap(
                serieIdTrigger,
                apiRepository::getSerieById
        );

        this.serieWatchProviders = Transformations.switchMap(
                serie,
                fetchedSerie -> apiRepository.fetchTvWatchProvider(fetchedSerie.getId(), fetchedSerie.getExternalIds().imdbId())
        );

        this.season = Transformations.switchMap(seasonTrigger, req ->
                apiRepository.getSeasonBySerieId(req.id, req.seasonNumber)
        );

        this.episode = Transformations.switchMap(episodeTrigger, req ->
                apiRepository.getEpisodeBySeasonNumberBySerieId(req.id, req.seasonNumber, req.episodeNumber)
        );

        this.databaseSeries = serieRepository.fetchAll();
        LiveData<List<SeasonDatabase>> allSeasons = serieRepository.fetchAllSeasons();
        LiveData<List<EpisodeDatabase>> allEpisodes = serieRepository.fetchAllEpisodes();

        // Statistics are delegated to a dedicated provider: it owns the aggregation LiveData,
        // the background computation and the coalescing of database invalidations.
        this.statsProvider = new SerieStatsProvider(databaseSeries, allSeasons, allEpisodes);
        this.serieStats = statsProvider.getStats();
    }

    @Override
    protected void onCleared() {
        statsProvider.clear();
    }

    // Public API to trigger data fetch
    public void initGetSerieById(Integer id) {
        if (!id.equals(savedStateHandle.get(SERIE_ID_KEY))) {
            savedStateHandle.set(SERIE_ID_KEY, id);
        }
    }

    public void initGetSeasonBySerieId(Integer id, Integer seasonNumber) {
        SeasonRequest newReq = new SeasonRequest(id, seasonNumber);
        if (!newReq.equals(savedStateHandle.get(SEASON_REQ_KEY))) {
            savedStateHandle.set(SEASON_REQ_KEY, newReq);
        }
    }

    public void initGetEpisodeBySeasonNumberBySerieId(Integer id, Integer seasonNumber, Integer episodeNumber) {
        EpisodeRequest newReq = new EpisodeRequest(id, seasonNumber, episodeNumber);
        if (!newReq.equals(savedStateHandle.get(EPISODE_REQ_KEY))) {
            savedStateHandle.set(EPISODE_REQ_KEY, newReq);
        }
    }

    // Database methods (these usually return stable LiveData from Room)
    public void insert(int serieId) {
        serieRepository.insert(serieId);
    }

    public void deleteById(int id) {
        serieRepository.deleteById(id);
    }

    public void setSerieWatched(int id, boolean watched) {
        serieRepository.setSerieWatched(id, watched);
    }

    public void setSeasonWatched(int serieId, int seasonNumber, boolean watched) {
        serieRepository.setSeasonWatched(serieId, seasonNumber, watched);
    }

    public void setEpisodeWatched(int serieId, int seasonNumber, int episodeNumber, boolean watched) {
        serieRepository.setEpisodeWatched(serieId, seasonNumber, episodeNumber, watched);
    }

    public void ensureSeasonMetadata(int serieId, int seasonNumber, int episodeCount) {
        serieRepository.ensureSeasonMetadata(serieId, seasonNumber, episodeCount);
    }

    public LiveData<List<SeasonDatabase>> getSeasonsBySerie(int serieId) {
        return serieRepository.fetchSeasonsBySerie(serieId);
    }

    public LiveData<SeasonDatabase> getSeasonState(int serieId, int seasonNumber) {
        return serieRepository.fetchSeason(serieId, seasonNumber);
    }

    public LiveData<List<EpisodeDatabase>> getEpisodesBySerieAndSeason(int serieId, int seasonNumber) {
        return serieRepository.fetchEpisodesBySerieAndSeason(serieId, seasonNumber);
    }

    public LiveData<List<EpisodeDatabase>> getEpisodesBySerie(int serieId) {
        return serieRepository.fetchEpisodesBySerie(serieId);
    }

    // Helper classes for complex triggers

    private static class SeasonRequest implements java.io.Serializable {
        Integer id;
        Integer seasonNumber;

        SeasonRequest(Integer i, Integer s) {
            id = i;
            seasonNumber = s;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SeasonRequest that = (SeasonRequest) o;
            return java.util.Objects.equals(id, that.id) &&
                    java.util.Objects.equals(seasonNumber, that.seasonNumber);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(id, seasonNumber);
        }
    }

    private static class EpisodeRequest implements java.io.Serializable {
        Integer id;
        Integer seasonNumber;
        Integer episodeNumber;

        EpisodeRequest(Integer i, Integer s, Integer e) {
            id = i;
            seasonNumber = s;
            episodeNumber = e;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EpisodeRequest that = (EpisodeRequest) o;
            return java.util.Objects.equals(id, that.id) &&
                    java.util.Objects.equals(seasonNumber, that.seasonNumber) &&
                    java.util.Objects.equals(episodeNumber, that.episodeNumber);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(id, seasonNumber, episodeNumber);
        }
    }
}
