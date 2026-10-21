package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.EpisodeDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SeasonDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.MovieRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.SerieRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import lombok.Getter;

/**
 * Owns the data of the library list screen: it exposes the tiles of each media type for the
 * watched list and for the watchlist, and relays the "mark as seen / unseen" and "delete"
 * actions to the repositories.
 * <p>
 * It reads the repositories directly rather than the library statistics view model, so the
 * screen stays independent from the dashboards aggregation. The series tiles need three tables
 * joined together, so the aggregation runs on a dedicated executor and coalesces the
 * invalidations, exactly like the statistics provider does.
 */
public class LibraryListViewModel extends AndroidViewModel {

    @Getter
    private final LiveData<List<LibraryItem>> watchedMovies;
    @Getter
    private final LiveData<List<LibraryItem>> watchlistMovies;
    @Getter
    private final LiveData<List<LibraryItem>> watchedSeries;
    @Getter
    private final LiveData<List<LibraryItem>> watchlistSeries;

    private final MovieRepository movieRepository;
    private final SerieRepository serieRepository;

    private final MediatorLiveData<List<LibraryItem>> watchedMovieItems = new MediatorLiveData<>();
    private final MediatorLiveData<List<LibraryItem>> watchlistMovieItems = new MediatorLiveData<>();
    private final MediatorLiveData<List<LibraryItem>> watchedSerieItems = new MediatorLiveData<>();
    private final MediatorLiveData<List<LibraryItem>> watchlistSerieItems = new MediatorLiveData<>();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicBoolean recomputeRequested = new AtomicBoolean(false);

    private volatile List<MovieDatabase> movieSource;
    private volatile List<SerieDatabase> serieSource;
    private volatile List<SeasonDatabase> seasonSource;
    private volatile List<EpisodeDatabase> episodeSource;

    public LibraryListViewModel(@NonNull Application application) {
        super(application);
        this.movieRepository = MovieRepository.getRepository(application);
        this.serieRepository = SerieRepository.getRepository(application);

        // One query per table, observed by both lists of the matching media type.
        LiveData<List<MovieDatabase>> movies = movieRepository.fetchAll();
        watchedMovieItems.addSource(movies, list -> {
            this.movieSource = list;
            scheduleRecompute();
        });
        watchlistMovieItems.addSource(movies, list -> {
            this.movieSource = list;
            scheduleRecompute();
        });

        LiveData<List<SerieDatabase>> series = serieRepository.fetchAll();
        LiveData<List<SeasonDatabase>> seasons = serieRepository.fetchAllSeasons();
        LiveData<List<EpisodeDatabase>> episodes = serieRepository.fetchAllEpisodes();
        watchedSerieItems.addSource(series, list -> {
            this.serieSource = list;
            scheduleRecompute();
        });
        watchedSerieItems.addSource(seasons, list -> {
            this.seasonSource = list;
            scheduleRecompute();
        });
        watchedSerieItems.addSource(episodes, list -> {
            this.episodeSource = list;
            scheduleRecompute();
        });
        watchlistSerieItems.addSource(series, list -> {
            this.serieSource = list;
            scheduleRecompute();
        });
        watchlistSerieItems.addSource(seasons, list -> {
            this.seasonSource = list;
            scheduleRecompute();
        });
        watchlistSerieItems.addSource(episodes, list -> {
            this.episodeSource = list;
            scheduleRecompute();
        });

        this.watchedMovies = watchedMovieItems;
        this.watchlistMovies = watchlistMovieItems;
        this.watchedSeries = watchedSerieItems;
        this.watchlistSeries = watchlistSerieItems;
    }

    /** The tiles of one media type for one list. */
    public LiveData<List<LibraryItem>> items(FragmentType type, LibraryFilter filter) {
        if (type == FragmentType.MOVIE) {
            return filter == LibraryFilter.WATCHED ? watchedMovies : watchlistMovies;
        }
        return filter == LibraryFilter.WATCHED ? watchedSeries : watchlistSeries;
    }

    public void setWatched(int id, FragmentType type, boolean watched) {
        if (type == FragmentType.MOVIE) {
            this.movieRepository.setMovieWatched(id, watched);
        } else {
            this.serieRepository.setSerieWatched(id, watched);
        }
    }

    public void delete(int id, FragmentType type) {
        if (type == FragmentType.MOVIE) {
            this.movieRepository.deleteMovieById(id);
        } else {
            this.serieRepository.deleteById(id);
        }
    }

    private void scheduleRecompute() {
        if (recomputeRequested.getAndSet(true)) {
            return;
        }
        executor.execute(this::runRecompute);
    }

    private void runRecompute() {
        do {
            recomputeRequested.set(false);
            if (!hasActiveObservers()) {
                return;
            }
            postMovieLists();
            postSerieLists();
        } while (recomputeRequested.get());
    }

    private void postMovieLists() {
        if (watchedMovieItems.hasActiveObservers()) {
            watchedMovieItems.postValue(
                    LibraryBuilder.buildMovies(movieSource, LibraryFilter.WATCHED));
        }
        if (watchlistMovieItems.hasActiveObservers()) {
            watchlistMovieItems.postValue(
                    LibraryBuilder.buildMovies(movieSource, LibraryFilter.WATCHLIST));
        }
    }

    private void postSerieLists() {
        if (watchedSerieItems.hasActiveObservers()) {
            watchedSerieItems.postValue(LibraryBuilder.buildSeries(
                    serieSource, seasonSource, episodeSource, LibraryFilter.WATCHED));
        }
        if (watchlistSerieItems.hasActiveObservers()) {
            watchlistSerieItems.postValue(LibraryBuilder.buildSeries(
                    serieSource, seasonSource, episodeSource, LibraryFilter.WATCHLIST));
        }
    }

    private boolean hasActiveObservers() {
        return watchedMovieItems.hasActiveObservers()
                || watchlistMovieItems.hasActiveObservers()
                || watchedSerieItems.hasActiveObservers()
                || watchlistSerieItems.hasActiveObservers();
    }

    @Override
    protected void onCleared() {
        executor.shutdownNow();
    }
}
