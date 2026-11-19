package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.TmdbApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Media;

/**
 * ViewModel of the search screen.
 * <p>
 * Owns the search state machine: the typed text is debounced before hitting the TMDB API, the
 * state switches to {@link SearchUiState.Loading} as soon as a character is entered, and only the
 * response of the latest request is published. A response for an older query is dropped, so the
 * loader cannot be hidden by a stale answer.
 * <p>
 * Also owns the category filter ({@link SearchFilter}) applied on the received results, since the
 * TMDB search endpoint only accepts a free-text query.
 */
public class SearchViewModel extends AndroidViewModel {
    /** Delay between the last keystroke and the API call. */
    private static final long DEBOUNCE_DELAY_MS = 500L;

    private final TmdbApiRepository apiRepository = TmdbApiRepository.getInstance();
    private final Handler debounceHandler = new Handler(Looper.getMainLooper());
    private final Runnable searchRunnable = this::executeSearch;

    private final MediatorLiveData<SearchUiState> uiState =
            new MediatorLiveData<>(new SearchUiState.Idle());

    /** Text currently displayed in the search field, trimmed. */
    private String pendingQuery = "";
    /** Last successful results, kept to display them dimmed while a new query is loading. */
    private List<Media> lastResults = List.of();
    /** Category currently displayed by the result rows. */
    private SearchFilter selectedFilter = SearchFilter.ALL;
    /** Bumped on every query change so that an obsolete response can be ignored. */
    private int requestGeneration;
    private LiveData<List<Media>> currentSource;

    public SearchViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<SearchUiState> getUiState() {
        return uiState;
    }

    /**
     * Called on every change of the search field.
     * <p>
     * An identical query is ignored: restoring the view (rotation, tab switch) must neither
     * restart a search nor reset a pending debounce. After a process death the query differs
     * from the pending one, so the search is replayed.
     */
    public void onQueryChanged(String rawQuery) {
        String query = rawQuery == null ? "" : rawQuery.trim();
        if (query.equals(pendingQuery)) {
            return;
        }
        pendingQuery = query;
        debounceHandler.removeCallbacks(searchRunnable);

        // The previous query is obsolete as soon as the text changes: its in-flight response must
        // not overwrite the state of the query being typed.
        cancelRequest();

        if (query.isEmpty()) {
            lastResults = List.of();
            selectedFilter = SearchFilter.ALL;
            uiState.setValue(new SearchUiState.Idle());
            return;
        }

        uiState.setValue(new SearchUiState.Loading(query, lastResults, selectedFilter));
        debounceHandler.postDelayed(searchRunnable, DEBOUNCE_DELAY_MS);
    }

    /**
     * Applies a category filter to the results already received.
     * <p>
     * The filter is purely visual: it never restarts the search, so switching category is
     * instantaneous and does not cost an API call. The change is published through a new state so
     * the screen keeps rendering a single source of truth.
     */
    public void onFilterSelected(SearchFilter filter) {
        if (filter == null || filter == selectedFilter) {
            return;
        }
        selectedFilter = filter;

        SearchUiState state = uiState.getValue();
        if (state instanceof SearchUiState.Loading loading) {
            uiState.setValue(new SearchUiState.Loading(
                    loading.query(), loading.previousResults(), filter));
        } else if (state instanceof SearchUiState.Success success) {
            uiState.setValue(new SearchUiState.Success(success.query(), success.medias(), filter));
        }
    }

    /** Runs the search once the debounce delay has elapsed without any new character. */
    private void executeSearch() {
        String query = pendingQuery;
        if (query.isEmpty()) {
            return;
        }

        int generation = ++requestGeneration;
        LiveData<List<Media>> source = apiRepository.getSearchByQuery(query);
        currentSource = source;

        uiState.addSource(source, medias -> {
            boolean obsolete = generation != requestGeneration;
            if (currentSource == source) {
                detachCurrentSource();
            }
            if (obsolete) {
                return;
            }
            if (medias == null) {
                uiState.setValue(new SearchUiState.Error(query));
                return;
            }
            lastResults = medias;
            uiState.setValue(new SearchUiState.Success(query, medias, effectiveFilter(medias)));
        });
    }

    /**
     * Keeps the selected category only while the new results still contain it.
     * <p>
     * A chip whose category is empty is disabled: keeping it selected would leave the screen on an
     * empty result set with no way back, so the filter silently falls back to {@link SearchFilter#ALL}.
     */
    private SearchFilter effectiveFilter(List<Media> medias) {
        if (selectedFilter == SearchFilter.ALL || medias.stream().anyMatch(selectedFilter::matches)) {
            return selectedFilter;
        }
        selectedFilter = SearchFilter.ALL;
        return selectedFilter;
    }

    /** Invalidates the in-flight request and detaches its source. */
    private void cancelRequest() {
        requestGeneration++;
        detachCurrentSource();
    }

    private void detachCurrentSource() {
        if (currentSource != null) {
            uiState.removeSource(currentSource);
            currentSource = null;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        debounceHandler.removeCallbacks(searchRunnable);
        detachCurrentSource();
    }
}
