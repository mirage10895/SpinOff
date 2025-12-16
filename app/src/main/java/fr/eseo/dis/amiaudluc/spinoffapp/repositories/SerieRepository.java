package fr.eseo.dis.amiaudluc.spinoffapp.repositories;

import android.app.Application;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.SerieDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;

public class SerieRepository {
    private final SerieDAO serieDAO;
    private final ApiRepository apiRepository;

    private final Executor executor = Executors.newSingleThreadExecutor();

    public SerieRepository(Application application) {
        AppDatabase db = AppDatabase.getAppDatabase(application);
        serieDAO = db.serieDAO();
        this.apiRepository = ApiRepository.getInstance();
    }

    public LiveData<List<SerieDatabase>> fetchAll() {
        return this.serieDAO.getAll();
    }

    public void insert(int serieId) {
        this.apiRepository.getSerieById(serieId)
                .observeForever(apiSerie -> {
                    SerieDatabase serieDatabase = new SerieDatabase();
                    serieDatabase.setId(apiSerie.getId());
                    serieDatabase.setName(apiSerie.getName());
                    serieDatabase.setPosterPath(apiSerie.getPosterPath());
                    serieDatabase.setWatched(false);
                    serieDatabase.setLastSynchronisationTime(Instant.now());

                    if (apiSerie.getNumberOfSeasons() == null || apiSerie.getNumberOfSeasons() == 0) {
                        insert(serieDatabase);
                        return;
                    }
                    if (apiSerie.getEpisodeRunTime().isEmpty()) {
                        // runtime of the first season episodes * the number of episodes
                        this.apiRepository.getSeasonBySerieId(apiSerie.getId(), 1)
                                .observeForever(season -> {
                                    serieDatabase.setRuntime(
                                            Season.computeEpisodesAverageRuntime(season) * apiSerie.getNumberOfEpisodes()
                                    );
                                    insert(serieDatabase);
                                });
                        return;
                    }
                    serieDatabase.setRuntime(Serie.computeTotalRuntime(apiSerie));
                    insert(serieDatabase);
                });
    }

    public void deleteById(int id) {
        executor.execute(() -> {
            this.serieDAO.deleteSerieById(id);
        });
    }

    private void insert(SerieDatabase serieDatabase) {
        executor.execute(() -> {
            this.serieDAO.insertSerie(serieDatabase);
        });
    }
}
