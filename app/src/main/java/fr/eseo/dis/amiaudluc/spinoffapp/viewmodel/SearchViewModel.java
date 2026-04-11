package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.tmdb.ApiRepository;
import lombok.Getter;

public class SearchViewModel extends AndroidViewModel {
    private static final String SEARCH_QUERY_KEY = "searchQuery";
    private final SavedStateHandle savedStateHandle;
    private final LiveData<String> searchQuery;

    @Getter
    private final LiveData<List<Media>> medias;
    private final ApiRepository apiRepository;

    public SearchViewModel(@NonNull Application application, @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        this.savedStateHandle = savedStateHandle;
        this.searchQuery = savedStateHandle.getLiveData(SEARCH_QUERY_KEY);

        this.apiRepository = ApiRepository.getInstance();
        
        // switchMap ensures that whenever searchQuery changes, we switch to a new LiveData source
        // but the 'medias' reference remains the same for the Observer in the Activity.
        this.medias = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.trim().isEmpty()) {
                return new androidx.lifecycle.MutableLiveData<>(); // Return empty if no query
            }
            return this.apiRepository.getSearchByQuery(query);
        });
    }

    public void initSearchByQuery(String query) {
        if (query == null || !query.equals(savedStateHandle.get(SEARCH_QUERY_KEY))) {
            savedStateHandle.set(SEARCH_QUERY_KEY, query);
        }
    }
}
