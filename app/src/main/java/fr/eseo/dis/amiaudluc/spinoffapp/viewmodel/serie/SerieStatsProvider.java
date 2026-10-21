package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.EpisodeDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SeasonDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import lombok.Getter;

/**
 * Turns the series / seasons / episodes database sources into a single {@link SerieStats} LiveData.
 * <p>
 * The aggregation is delegated to {@link SerieStatsCalculator} and runs off the main thread.
 * The three sources are usually invalidated together, so a single computation is scheduled per
 * batch; if a source updates while a computation is running, one extra pass is run with the
 * freshest data.
 */
public class SerieStatsProvider {

    @Getter
    private final LiveData<SerieStats> stats;

    private volatile List<SerieDatabase> series;
    private volatile List<SeasonDatabase> seasons;
    private volatile List<EpisodeDatabase> episodes;

    private final MediatorLiveData<SerieStats> statsLiveData = new MediatorLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicBoolean recomputeRequested = new AtomicBoolean(false);

    public SerieStatsProvider(
            LiveData<List<SerieDatabase>> seriesSource,
            LiveData<List<SeasonDatabase>> seasonsSource,
            LiveData<List<EpisodeDatabase>> episodesSource
    ) {
        statsLiveData.addSource(seriesSource, list -> {
            this.series = list;
            scheduleRecompute();
        });
        statsLiveData.addSource(seasonsSource, list -> {
            this.seasons = list;
            scheduleRecompute();
        });
        statsLiveData.addSource(episodesSource, list -> {
            this.episodes = list;
            scheduleRecompute();
        });
        this.stats = statsLiveData;
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
            statsLiveData.postValue(
                    SerieStatsCalculator.calculate(series, seasons, episodes));
        } while (recomputeRequested.get());
    }

    /** Releases the background executor. Call it when the owning ViewModel is cleared. */
    public void clear() {
        executor.shutdownNow();
    }
}
