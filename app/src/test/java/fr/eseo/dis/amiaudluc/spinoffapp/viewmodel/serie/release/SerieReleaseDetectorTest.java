package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.release;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieNotificationDatabase;

/**
 * Unit tests of the release detection rules: unknown baseline, new episode, new season, future
 * dates, finished series and state retention.
 */
public class SerieReleaseDetectorTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 22);
    private static final int SERIE_ID = 1399;

    private final SerieReleaseDetector detector = new SerieReleaseDetector(TODAY);

    // --- Baseline ---------------------------------------------------------------------------

    @Test
    public void detect_withoutPreviousState_snapshotsButNeverNotifies() {
        Serie serie = serie("Returning Series", 3, episode(3, 1, TODAY));
        serie.setSeasons(List.of(season(1, TODAY.minusYears(2)), season(2, TODAY.minusYears(1)),
                season(3, TODAY)));

        SerieReleaseDetector.Detection detection = detector.detect(null, serie);

        assertNotNull(detection.state());
        assertFalse(detection.release().isPresent());
        assertEquals("Returning Series", detection.state().getLastKnownStatus());
        assertEquals(Integer.valueOf(3), detection.state().getLastKnownSeasonCount());
        assertEquals(Integer.valueOf(3), detection.state().getLastSeenSeason());
        assertEquals(TODAY, detection.state().getLastSeenAirDate());
    }

    @Test
    public void detect_withUnknownStatusOnlySnapshots() {
        SerieNotificationDatabase previous = state("Returning Series", 2, 2, 10, TODAY.minusDays(7));
        previous.setLastKnownStatus(null);

        SerieReleaseDetector.Detection detection =
                detector.detect(previous, serie("Returning Series", 2, episode(2, 11, TODAY)));

        assertNotNull(detection.state());
        assertFalse(detection.release().isPresent());
        assertEquals("Returning Series", detection.state().getLastKnownStatus());
        assertEquals(Integer.valueOf(11), detection.state().getLastSeenEpisode());
    }

    // --- Episodes ---------------------------------------------------------------------------

    @Test
    public void detect_newAiredEpisode_notifiesIt() {
        SerieNotificationDatabase previous = state("Returning Series", 2, 2, 10, TODAY.minusDays(7));

        Optional<SerieRelease> release =
                detector.detect(previous, serie("Returning Series", 2, episode(2, 11, TODAY))).release();

        assertTrue(release.isPresent());
        assertEquals(SerieRelease.Kind.EPISODE, release.get().kind());
        assertEquals(2, release.get().seasonNumber());
        assertEquals(Integer.valueOf(11), release.get().episodeNumber());
        assertEquals(TODAY, release.get().airDate());
    }

    @Test
    public void detect_sameEpisode_doesNotNotifyAgain() {
        SerieNotificationDatabase previous = state("Returning Series", 2, 2, 11, TODAY);

        assertFalse(detector.detect(previous, serie("Returning Series", 2, episode(2, 11, TODAY)))
                .release().isPresent());
    }

    @Test
    public void detect_futureEpisode_doesNotNotifyYet() {
        SerieNotificationDatabase previous = state("Returning Series", 2, 2, 10, TODAY.minusDays(7));

        assertFalse(detector.detect(previous,
                serie("Returning Series", 2, episode(2, 11, TODAY.plusDays(3)))).release().isPresent());
    }

    @Test
    public void detect_rescheduledEpisodeOlderThanTheLastOneSeen_doesNotNotify() {
        SerieNotificationDatabase previous = state("Returning Series", 2, 2, 10, TODAY);

        assertFalse(detector.detect(previous,
                serie("Returning Series", 2, episode(2, 9, TODAY.minusDays(14)))).release().isPresent());
    }

    // --- Seasons ----------------------------------------------------------------------------

    @Test
    public void detect_newAiredSeason_notifiesTheSeason() {
        SerieNotificationDatabase previous = state("Returning Series", 2, 2, 10, TODAY.minusDays(7));
        Serie serie = serie("Returning Series", 3, episode(2, 10, TODAY.minusDays(7)));
        serie.setSeasons(List.of(season(1, TODAY.minusYears(2)), season(2, TODAY.minusYears(1)),
                season(3, TODAY)));

        Optional<SerieRelease> release = detector.detect(previous, serie).release();

        assertTrue(release.isPresent());
        assertTrue(release.get().isSeason());
        assertEquals(3, release.get().seasonNumber());
    }

    @Test
    public void detect_announcedSeasonNotAiredYet_doesNotNotify() {
        SerieNotificationDatabase previous = state("Returning Series", 2, 2, 10, TODAY.minusDays(7));
        Serie serie = serie("Returning Series", 3, episode(2, 10, TODAY.minusDays(7)));
        serie.setSeasons(List.of(season(3, TODAY.plusMonths(6))));

        assertFalse(detector.detect(previous, serie).release().isPresent());
    }

    @Test
    public void detect_newSpecialSeason_doesNotNotify() {
        SerieNotificationDatabase previous = state("Returning Series", 2, 2, 10, TODAY.minusDays(7));
        Serie serie = serie("Returning Series", 3, episode(2, 10, TODAY.minusDays(7)));
        serie.setSeasons(List.of(season(0, TODAY), season(3, TODAY.plusMonths(6))));

        assertFalse(detector.detect(previous, serie).release().isPresent());
    }

    // --- Finished series --------------------------------------------------------------------

    @Test
    public void detect_serieJustEnded_doesNotNotifyAnEpisode() {
        SerieNotificationDatabase previous = state("Returning Series", 2, 2, 10, TODAY.minusDays(7));

        SerieReleaseDetector.Detection detection =
                detector.detect(previous, serie("Ended", 2, episode(2, 11, TODAY)));

        assertFalse(detection.release().isPresent());
        assertEquals("Ended", detection.state().getLastKnownStatus());
    }

    // --- Release calendar retention ----------------------------------------------------------

    @Test
    public void detect_keepsTheNextEpisodeAsTheCalendarSource() {
        Serie serie = serie("Returning Series", 2, episode(2, 11, TODAY.minusDays(7)));
        serie.setNextEpisodeToAir(episode(2, 12, TODAY.plusDays(7)));

        SerieNotificationDatabase state = detector.detect(null, serie).state();

        assertEquals(Integer.valueOf(2), state.getNextEpisodeSeason());
        assertEquals(Integer.valueOf(12), state.getNextEpisodeNumber());
        assertEquals(TODAY.plusDays(7), state.getNextEpisodeAirDate());
    }

    @Test
    public void detect_airedNextEpisodeIsKeptForRecentReleases() {
        SerieNotificationDatabase previous = state("Returning Series", 2, 2, 11, TODAY.minusDays(7));
        previous.setNextEpisodeAirDate(TODAY.minusDays(2));
        previous.setNextEpisodeNumber(12);
        Serie serie = serie("Returning Series", 2, episode(2, 12, TODAY.minusDays(2)));

        SerieNotificationDatabase state = detector.detect(previous, serie).state();

        assertEquals(TODAY.minusDays(2), state.getNextEpisodeAirDate());
        assertEquals(Integer.valueOf(12), state.getNextEpisodeNumber());
    }

    @Test
    public void detect_missingPayloadReturnsNoState() {
        SerieReleaseDetector.Detection detection = detector.detect(null, null);

        assertNull(detection.state());
        assertFalse(detection.release().isPresent());
    }

    @Test
    public void detect_stampsTheStateWhenAReleaseIsNotified() {
        SerieNotificationDatabase previous = state("Returning Series", 2, 2, 10, TODAY.minusDays(7));

        SerieNotificationDatabase state = detector.detect(previous, serie("Returning Series", 2,
                episode(2, 11, TODAY))).state();

        assertEquals(Integer.valueOf(2), state.getLastKnownSeasonCount());
        assertTrue(state.getNotifiedAt().isBefore(Instant.now().plusSeconds(1)));
    }

    // --- Fixtures -----------------------------------------------------------------------------

    private static SerieNotificationDatabase state(String status, int seasonCount, int season,
                                                   int episodeNumber, LocalDate airDate) {
        SerieNotificationDatabase state = new SerieNotificationDatabase();
        state.setSerieId(SERIE_ID);
        state.setLastKnownStatus(status);
        state.setLastKnownSeasonCount(seasonCount);
        state.setLastSeenSeason(season);
        state.setLastSeenEpisode(episodeNumber);
        state.setLastSeenAirDate(airDate);
        return state;
    }

    private static Serie serie(String status, int seasonCount, Episode lastEpisode) {
        Serie serie = new Serie();
        serie.setId(SERIE_ID);
        serie.setName("Game of Thrones");
        serie.setPosterPath("/poster.jpg");
        serie.setStatus(status);
        serie.setNumberOfSeasons(seasonCount);
        serie.setLastEpisodeToAir(lastEpisode);
        return serie;
    }

    private static Episode episode(int seasonNumber, int episodeNumber, LocalDate airDate) {
        Episode episode = new Episode();
        episode.setSeasonNumber(seasonNumber);
        episode.setEpisodeNumber(episodeNumber);
        episode.setAirDate(airDate);
        episode.setName("Episode " + episodeNumber);
        return episode;
    }

    private static Season season(int seasonNumber, LocalDate airDate) {
        Season season = new Season();
        season.setSeasonNumber(seasonNumber);
        season.setAirDate(airDate);
        return season;
    }
}
