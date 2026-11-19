package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Media;

/**
 * Category filter applied to the results of the search screen.
 * <p>
 * The TMDB search endpoint only accepts a free-text query, so this filter is applied by the app on
 * the results it already received: selecting a category never triggers a new call.
 */
public enum SearchFilter {

    /** Every result, whatever its type. */
    ALL,

    /** Movies only. */
    MOVIES,

    /** Series only. */
    SERIES,

    /** Artists only. */
    ARTISTS;

    /**
     * @param media result to test, may be {@code null}
     * @return {@code true} when {@code media} belongs to this category
     */
    public boolean matches(Media media) {
        if (media == null) {
            return false;
        }
        return switch (this) {
            case ALL -> true;
            case MOVIES -> Media.MOVIE.equals(media.getMediaType());
            case SERIES -> Media.SERIE.equals(media.getMediaType());
            case ARTISTS -> Media.ARTIST.equals(media.getMediaType());
        };
    }
}
