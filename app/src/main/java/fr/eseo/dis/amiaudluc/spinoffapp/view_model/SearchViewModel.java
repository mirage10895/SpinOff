package fr.eseo.dis.amiaudluc.spinoffapp.view_model;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;
import androidx.annotation.NonNull;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.ApiRepository;

public class SearchViewModel extends AndroidViewModel {
    @NonNull
    private LiveData<List<Media>> medias;

    private final ApiRepository apiRepository;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        this.apiRepository = ApiRepository.getInstance();
        this.medias = new MutableLiveData<>();
    }

    public void initSearchByQuery(String query) {
        this.medias = this.apiRepository.getSearchByQuery(query);
    }

    @NonNull
    public LiveData<List<Media>> getMedias() {
        return medias;
    }
}
