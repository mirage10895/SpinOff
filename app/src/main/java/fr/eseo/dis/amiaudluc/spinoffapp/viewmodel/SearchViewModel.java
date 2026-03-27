package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.ApiRepository;
import lombok.Getter;

public class SearchViewModel extends AndroidViewModel {
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    @Getter
    private final LiveData<List<Media>> medias;
    private final ApiRepository apiRepository;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        this.apiRepository = ApiRepository.getInstance();
        
        // switchMap ensures that whenever searchQuery changes, we switch to a new LiveData source
        // but the 'medias' reference remains the same for the Observer in the Activity.
        this.medias = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.trim().isEmpty()) {
                return new MutableLiveData<>(); // Return empty if no query
            }
            return this.apiRepository.getSearchByQuery(query);
        });
    }

    public void initSearchByQuery(String query) {
        // Just update the query value; switchMap handles the rest
        this.searchQuery.setValue(query);
    }
}
