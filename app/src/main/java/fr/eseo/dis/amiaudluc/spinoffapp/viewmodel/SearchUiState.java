package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Media;

/**
 * State of the search screen, exposed as a single source of truth by {@link SearchViewModel}.
 * <p>
 * The view only renders this state: it never decides by itself whether a search is running,
 * whether the displayed results are up to date or which category is filtered.
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
    record Loading(String query, List<Media> previousResults, SearchFilter filter)
            implements SearchUiState {
    }

    /**
     * The API answered for {@code query}. {@code medias} is the complete answer: the
     * {@code filter} is applied by the view, which needs every category to enable its chip.
     */
    record Success(String query, List<Media> medias, SearchFilter filter)
            implements SearchUiState {
    }

    /** The API call for {@code query} failed. */
    record Error(String query) implements SearchUiState {
    }
}
