package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.EpisodeDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SeasonDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;

/**
 * Unit tests of the watched list aggregation, especially the episode progress rule: a watched
 * season carries its whole episode count because the repository deletes its episode rows.
 */
public class WatchedListBuilderTest {

    // --- Movies ---------------------------------------------------------------------------

    @Test
    public void buildMovies_keepsOnlyWatchedMovies() {
        MovieDatabase watched = movie(1, "Alien", true);
        MovieDatabase notWatched = movie(2, "Aliens", false);

        List<WatchedMediaItem> items = WatchedListBuilder.buildMovies(List.of(watched, notWatched));

        assertEquals(1, items.size());
        assertEquals(1, items.get(0).id());
        assertEquals(FragmentType.MOVIE, items.get(0).fragmentType());
        assertTrue(items.get(0).fullyWatched());
        assertFalse(items.get(0).hasEpisodeProgress());
    }

    @Test
    public void buildMovies_mapsTheDisplayedFields() {
        MovieDatabase movie = movie(1, "Apocalypse Now", true);
        movie.setRuntime(166);
        movie.setVoteAverage(8.4);
        movie.setReleaseDate(LocalDate.of(1979, 8, 15));
        movie.setDirectors("Francis Ford Coppola,Someone Else");
        movie.setGenres("Drama,War");

        WatchedMediaItem item = WatchedListBuilder.buildMovies(List.of(movie)).get(0);

        assertEquals(Integer.valueOf(166), item.runtimeMinutes());
        assertEquals(Double.valueOf(8.4), item.voteAverage());
        assertEquals(Integer.valueOf(1979), item.releaseYear());
        assertEquals("Francis Ford Coppola", item.creators());
        assertEquals("Drama,War", item.genres());
        assertNull(item.seasonCount());
    }

    @Test
    public void buildMovies_handlesMissingOptionalFields() {
        WatchedMediaItem item = WatchedListBuilder.buildMovies(List.of(movie(1, "Alien", true))).get(0);

        assertNull(item.releaseYear());
        assertNull(item.creators());
        assertNull(item.posterPath());
        assertNull(item.voteAverage());
    }

    @Test
    public void buildMovies_ordersTitlesAlphabeticallyWithTheOtherBucketLast() {
        List<WatchedMediaItem> items = WatchedListBuilder.buildMovies(List.of(
                movie(1, "Zodiac", true),
                movie(2, "Les Misérables", true),
                movie(3, "12 Angry Men", true),
                movie(4, "Alien", true)));

        assertEquals(List.of("Alien", "Les Misérables", "Zodiac", "12 Angry Men"),
                items.stream().map(WatchedMediaItem::title).toList());
    }

    // --- Series ---------------------------------------------------------------------------

    @Test
    public void buildSeries_fullyWatchedSeasonsHaveNoEpisodeRow() {
        // A serie marked as watched has its episode rows deleted: only the season flags remain.
        SerieDatabase serie = serie(1, "Arcane", true, 2);
        List<SeasonDatabase> seasons = List.of(season(1, 1, 10, true), season(1, 2, 8, true));

        WatchedMediaItem item = WatchedListBuilder.buildSeries(List.of(serie), seasons, List.of()).get(0);

        assertEquals(18, item.watchedEpisodes());
        assertEquals(18, item.totalEpisodes());
        assertEquals(100, item.progressPercent());
        assertTrue(item.fullyWatched());
        assertTrue(item.hasEpisodeProgress());
        assertEquals(FragmentType.SERIE, item.fragmentType());
        assertNull(item.runtimeMinutes());
    }

    @Test
    public void buildSeries_addsTheWatchedEpisodeRowsOfPartialSeasons() {
        SerieDatabase serie = serie(1, "Arcane", false, 2);
        List<SeasonDatabase> seasons = List.of(season(1, 1, 10, true), season(1, 2, 8, false));
        List<EpisodeDatabase> episodes = List.of(episode(1, 2, 1), episode(1, 2, 2), episode(1, 2, 3));

        WatchedMediaItem item = WatchedListBuilder.buildSeries(List.of(serie), seasons, episodes).get(0);

        assertEquals(13, item.watchedEpisodes());
        assertEquals(18, item.totalEpisodes());
        assertEquals(72, item.progressPercent());
        assertFalse(item.fullyWatched());
    }

    @Test
    public void buildSeries_ignoresSpecials() {
        SerieDatabase serie = serie(1, "Arcane", false, 2);
        List<SeasonDatabase> seasons = List.of(
                season(1, 0, 5, true),
                season(1, 1, 10, true),
                season(1, 2, 8, false));

        WatchedMediaItem item = WatchedListBuilder.buildSeries(List.of(serie), seasons, List.of()).get(0);

        assertEquals(10, item.watchedEpisodes());
        assertEquals(18, item.totalEpisodes());
    }

    @Test
    public void buildSeries_skipsSeriesThatWereNeverStarted() {
        SerieDatabase serie = serie(1, "Arcane", false, 1);
        List<SeasonDatabase> seasons = List.of(season(1, 1, 10, false));

        List<WatchedMediaItem> items = WatchedListBuilder.buildSeries(List.of(serie), seasons, List.of());

        assertTrue(items.isEmpty());
    }

    @Test
    public void buildSeries_capsTheProgressOnTheTotal() {
        SerieDatabase serie = serie(1, "Arcane", false, 1);
        List<SeasonDatabase> seasons = List.of(season(1, 1, 2, false));
        List<EpisodeDatabase> episodes = List.of(
                episode(1, 1, 1), episode(1, 1, 2), episode(1, 1, 3));

        WatchedMediaItem item = WatchedListBuilder.buildSeries(List.of(serie), seasons, episodes).get(0);

        assertEquals(2, item.watchedEpisodes());
        assertEquals(100, item.progressPercent());
    }

    @Test
    public void buildSeries_ignoresTheSeasonsOfOtherSeries() {
        SerieDatabase serie = serie(1, "Arcane", false, 1);
        List<SeasonDatabase> seasons = List.of(season(1, 1, 10, false), season(2, 1, 10, true));
        List<EpisodeDatabase> episodes = List.of(episode(1, 1, 1), episode(2, 1, 1));

        WatchedMediaItem item = WatchedListBuilder.buildSeries(List.of(serie), seasons, episodes).get(0);

        assertEquals(1, item.watchedEpisodes());
        assertEquals(10, item.totalEpisodes());
    }

    @Test
    public void buildSeries_toleratesNullSources() {
        assertTrue(WatchedListBuilder.buildSeries(null, null, null).isEmpty());
        assertTrue(WatchedListBuilder.buildMovies(null).isEmpty());
    }

    // --- Fixtures -------------------------------------------------------------------------

    private static MovieDatabase movie(int id, String title, boolean watched) {
        MovieDatabase movie = new MovieDatabase();
        movie.setId(id);
        movie.setTitle(title);
        movie.setWatched(watched);
        return movie;
    }

    private static SerieDatabase serie(int id, String name, boolean watched, int seasonCount) {
        SerieDatabase serie = new SerieDatabase();
        serie.setId(id);
        serie.setName(name);
        serie.setWatched(watched);
        serie.setSeasonCount(seasonCount);
        return serie;
    }

    private static SeasonDatabase season(int serieId, int seasonNumber, int episodeCount, boolean watched) {
        SeasonDatabase season = new SeasonDatabase();
        season.setSerieId(serieId);
        season.setSeasonNumber(seasonNumber);
        season.setEpisodeCount(episodeCount);
        season.setWatched(watched);
        return season;
    }

    private static EpisodeDatabase episode(int serieId, int seasonNumber, int episodeNumber) {
        EpisodeDatabase episode = new EpisodeDatabase();
        episode.setSerieId(serieId);
        episode.setSeasonNumber(seasonNumber);
        episode.setEpisodeNumber(episodeNumber);
        return episode;
    }
}
