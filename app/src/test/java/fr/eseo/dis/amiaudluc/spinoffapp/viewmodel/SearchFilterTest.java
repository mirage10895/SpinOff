package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Media;

/**
 * Unit tests of the front-side category filter of the search screen: each filter must accept only
 * the media type it targets, and the "all" filter must accept everything.
 */
public class SearchFilterTest {

    @Test
    public void all_acceptsEveryMediaType() {
        assertTrue(SearchFilter.ALL.matches(media(Media.MOVIE)));
        assertTrue(SearchFilter.ALL.matches(media(Media.SERIE)));
        assertTrue(SearchFilter.ALL.matches(media(Media.ARTIST)));
    }

    @Test
    public void movies_acceptsOnlyMovies() {
        assertTrue(SearchFilter.MOVIES.matches(media(Media.MOVIE)));
        assertFalse(SearchFilter.MOVIES.matches(media(Media.SERIE)));
        assertFalse(SearchFilter.MOVIES.matches(media(Media.ARTIST)));
    }

    @Test
    public void series_acceptsOnlySeries() {
        assertTrue(SearchFilter.SERIES.matches(media(Media.SERIE)));
        assertFalse(SearchFilter.SERIES.matches(media(Media.MOVIE)));
        assertFalse(SearchFilter.SERIES.matches(media(Media.ARTIST)));
    }

    @Test
    public void artists_acceptsOnlyArtists() {
        assertTrue(SearchFilter.ARTISTS.matches(media(Media.ARTIST)));
        assertFalse(SearchFilter.ARTISTS.matches(media(Media.MOVIE)));
        assertFalse(SearchFilter.ARTISTS.matches(media(Media.SERIE)));
    }

    @Test
    public void nullMedia_neverMatches() {
        assertFalse(SearchFilter.ALL.matches(null));
        assertFalse(SearchFilter.MOVIES.matches(null));
        assertFalse(SearchFilter.SERIES.matches(null));
        assertFalse(SearchFilter.ARTISTS.matches(null));
    }

    private static Media media(String type) {
        return () -> type;
    }
}
