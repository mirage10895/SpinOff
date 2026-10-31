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
            uiState.setValue(new SearchUiState.Idle());
            return;
        }

        uiState.setValue(new SearchUiState.Loading(query, lastResults));
        debounceHandler.postDelayed(searchRunnable, DEBOUNCE_DELAY_MS);
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
            uiState.setValue(new SearchUiState.Success(query, medias));
        });
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
