package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Media;

/**
 * State of the search screen, exposed as a single source of truth by {@link SearchViewModel}.
 * <p>
 * The view only renders this state: it never decides by itself whether a search is running or
 * whether the displayed results are up to date.
 */
public sealed interface SearchUiState {

    /** No query typed yet: the screen shows the welcome message. */
    record Idle() implements SearchUiState {
    }

    /**
     * A query is being debounced or fetched. {@code previousResults} holds the last successful
     * results so the screen can keep them visible (dimmed) instead of clearing them, including
     * after a configuration change.
     */
    record Loading(String query, List<Media> previousResults) implements SearchUiState {
    }

    /** The API answered for {@code query}. */
    record Success(String query, List<Media> medias) implements SearchUiState {
    }

    /** The API call for {@code query} failed. */
    record Error(String query) implements SearchUiState {
    }
}
