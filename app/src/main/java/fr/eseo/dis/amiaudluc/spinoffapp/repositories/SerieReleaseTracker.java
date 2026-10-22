package fr.eseo.dis.amiaudluc.spinoffapp.repositories;

import java.util.Optional;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.SerieNotificationDAO;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieNotificationDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.release.SerieRelease;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.release.SerieReleaseDetector;

/**
 * Keeps the release-tracking state of one serie in sync with the API and returns what is worth a
 * notification. Runs on the caller's background thread.
 */
class SerieReleaseTracker {

    private final SerieNotificationDAO serieNotificationDAO;

    SerieReleaseTracker(SerieNotificationDAO serieNotificationDAO) {
        this.serieNotificationDAO = serieNotificationDAO;
    }

    /**
     * Compare the serie with its tracking state, store the new state and return the release to
     * notify. The state is written before the notification so a crash never replays it.
     */
    Optional<SerieRelease> track(int serieId, Serie apiSerie, SerieReleaseDetector detector) {
        SerieNotificationDatabase previous = serieNotificationDAO.getSync(serieId);
        SerieReleaseDetector.Detection detection = detector.detect(previous, apiSerie);
        if (detection.state() == null) {
            return Optional.empty();
        }
        serieNotificationDAO.upsert(detection.state());
        return detection.release();
    }
}
