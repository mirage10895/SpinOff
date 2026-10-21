package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library;

/**
 * The two lists the library screen can show: the titles already watched, and the watchlist.
 * <p>
 * A movie belongs to {@link #WATCHED} when its watched flag is set, to {@link #WATCHLIST}
 * otherwise. A serie belongs to {@link #WATCHED} as soon as it has been started — its aggregated
 * watched flag is set or at least one episode was watched — and to {@link #WATCHLIST} while it
 * has not been started at all.
 */
public enum LibraryFilter {
    WATCHED,
    WATCHLIST
}
