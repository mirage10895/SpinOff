package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.release;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieNotificationDatabase;

/**
 * Decides whether a fresh TMDB payload contains a release worth notifying and computes the state
 * to store for the next comparison. Completely free of input/output so it can be unit tested.
 * <p>
 * Rules, in order:
 * <ul>
 *   <li>unknown previous state (first sync after the upgrade, or a serie just added to the
 *       library): no notification, only a snapshot, to avoid a burst of false positives;</li>
 *   <li>a new last aired episode must be already aired and either different from the last one
 *       seen or aired after it, otherwise a moved or rescheduled episode would re-notify;</li>
 *   <li>a new season must have been declared, be already aired and not be a special (season
 *       0), because TMDB announces upcoming seasons long before their release;</li>
 *   <li>an ended or cancelled serie never notifies a release.</li>
 * </ul>
 */
public class SerieReleaseDetector {

    public static final String STATUS_ENDED = "Ended";
    public static final String STATUS_CANCELED = "Canceled";

    /**
     * Outcome of a detection: the state to persist and the optional release to notify.
     *
     * @param state   tracking state to store for the next synchronisation, {@code null} when the
     *                payload has no usable identifier
     * @param release release to notify, empty when nothing new is worth a notification
     */
    public record Detection(SerieNotificationDatabase state, Optional<SerieRelease> release) {
    }

    private final LocalDate today;

    public SerieReleaseDetector(LocalDate today) {
        this.today = today;
    }

    /**
     * Compare the stored state with the fresh API payload.
     *
     * @param previous stored state, {@code null} on the very first synchronisation
     * @param serie    fresh TMDB payload
     */
    public Detection detect(SerieNotificationDatabase previous, Serie serie) {
        if (serie == null || serie.getId() == null) {
            return new Detection(null, Optional.empty());
        }
        boolean snapshotOnly = previous == null || previous.getLastKnownStatus() == null;
        SerieNotificationDatabase state = computeState(previous, serie);
        if (snapshotOnly || isFinished(serie)) {
            return new Detection(state, Optional.empty());
        }
        Optional<SerieRelease> release = detectRelease(previous, serie);
        release.ifPresent(value -> state.setNotifiedAt(Instant.now()));
        return new Detection(state, release);
    }

    private boolean isFinished(Serie serie) {
        String status = serie.getStatus();
        return STATUS_ENDED.equalsIgnoreCase(status) || STATUS_CANCELED.equalsIgnoreCase(status);
    }

    /**
     * At most one release per serie and per synchronisation: a new season wins over its opening
     * episode because the season label already carries the information.
     */
    private Optional<SerieRelease> detectRelease(SerieNotificationDatabase previous, Serie serie) {
        Optional<SerieRelease> season = detectSeason(previous, serie);
        return season.isPresent() ? season : detectEpisode(previous, serie);
    }

    private Optional<SerieRelease> detectSeason(SerieNotificationDatabase previous, Serie serie) {
        if (valueOrZero(serie.getNumberOfSeasons()) <= valueOrZero(previous.getLastKnownSeasonCount())) {
            return Optional.empty();
        }
        return firstAiredSeasonAbove(serie.getSeasons(), valueOrZero(previous.getLastKnownSeasonCount()))
                .map(season -> SerieRelease.season(serie.getId(), serie.getName(),
                        serie.getPosterPath(), season.getSeasonNumber(), season.getAirDate()));
    }

    private Optional<SerieRelease> detectEpisode(SerieNotificationDatabase previous, Serie serie) {
        Episode lastEpisode = serie.getLastEpisodeToAir();
        if (!isAired(lastEpisode) || lastEpisode.getSeasonNumber() == null
                || lastEpisode.getEpisodeNumber() == null || alreadySeen(previous, lastEpisode)) {
            return Optional.empty();
        }
        return Optional.of(SerieRelease.episode(serie.getId(), serie.getName(), serie.getPosterPath(),
                lastEpisode.getSeasonNumber(), lastEpisode.getEpisodeNumber(),
                lastEpisode.getName(), lastEpisode.getAirDate()));
    }

    private boolean alreadySeen(SerieNotificationDatabase previous, Episode episode) {
        boolean sameEpisode = episode.getSeasonNumber().equals(previous.getLastSeenSeason())
                && episode.getEpisodeNumber().equals(previous.getLastSeenEpisode());
        // Guard against TMDB filling a re-scheduled episode with an older air date than the one
        // already notified.
        boolean olderThanLastSeen = previous.getLastSeenAirDate() != null
                && episode.getAirDate().isBefore(previous.getLastSeenAirDate());
        return sameEpisode || olderThanLastSeen;
    }

    private boolean isAired(Episode episode) {
        return episode != null && episode.getAirDate() != null
                && !episode.getAirDate().isAfter(today);
    }

    /**
     * Oldest regular season added since the last synchronisation that is already aired. Specials
     * (season 0) are ignored, like everywhere else in the project.
     */
    private Optional<Season> firstAiredSeasonAbove(List<Season> seasons, int previousSeasonCount) {
        if (seasons == null) {
            return Optional.empty();
        }
        return seasons.stream()
                .filter(season -> season.getSeasonNumber() != null && season.getSeasonNumber() > 0)
                .filter(season -> season.getSeasonNumber() > previousSeasonCount)
                .filter(season -> season.getAirDate() != null && !season.getAirDate().isAfter(today))
                .min(Comparator.comparingInt(Season::getSeasonNumber));
    }

    private SerieNotificationDatabase computeState(SerieNotificationDatabase previous, Serie serie) {
        SerieNotificationDatabase state = previous == null
                ? new SerieNotificationDatabase()
                : copy(previous);
        state.setSerieId(serie.getId());
        state.setLastKnownStatus(serie.getStatus());
        state.setLastKnownSeasonCount(valueOrZero(serie.getNumberOfSeasons()));

        Episode lastEpisode = serie.getLastEpisodeToAir();
        if (isAired(lastEpisode)) {
            state.setLastSeenSeason(lastEpisode.getSeasonNumber());
            state.setLastSeenEpisode(lastEpisode.getEpisodeNumber());
            state.setLastSeenAirDate(lastEpisode.getAirDate());
        }
        applyNextEpisode(state, serie.getNextEpisodeToAir());
        return state;
    }

    /**
     * Keep the announced episode until a newer one is published: it feeds the release calendar.
     */
    private void applyNextEpisode(SerieNotificationDatabase state, Episode nextEpisode) {
        if (nextEpisode == null || nextEpisode.getAirDate() == null) {
            return;
        }
        state.setNextEpisodeSeason(nextEpisode.getSeasonNumber());
        state.setNextEpisodeNumber(nextEpisode.getEpisodeNumber());
        state.setNextEpisodeAirDate(nextEpisode.getAirDate());
        state.setNextEpisodeName(nextEpisode.getName());
    }

    private SerieNotificationDatabase copy(SerieNotificationDatabase source) {
        SerieNotificationDatabase copy = new SerieNotificationDatabase();
        copy.setSerieId(source.getSerieId());
        copy.setEnabled(source.isEnabled());
        copy.setLastKnownStatus(source.getLastKnownStatus());
        copy.setLastKnownSeasonCount(source.getLastKnownSeasonCount());
        copy.setLastSeenSeason(source.getLastSeenSeason());
        copy.setLastSeenEpisode(source.getLastSeenEpisode());
        copy.setLastSeenAirDate(source.getLastSeenAirDate());
        copy.setNotifiedAt(source.getNotifiedAt());
        copy.setNextEpisodeSeason(source.getNextEpisodeSeason());
        copy.setNextEpisodeNumber(source.getNextEpisodeNumber());
        copy.setNextEpisodeAirDate(source.getNextEpisodeAirDate());
        copy.setNextEpisodeName(source.getNextEpisodeName());
        return copy;
    }

    private static int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }
}
