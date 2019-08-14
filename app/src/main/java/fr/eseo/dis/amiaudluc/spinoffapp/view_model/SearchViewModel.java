package fr.eseo.dis.amiaudluc.spinoffapp.view_model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.repository.ApiRepository;

public class SearchViewModel extends ViewModel {
    @NonNull
    private LiveData<List<Media>> medias;

    private ApiRepository apiRepository;

    public SearchViewModel(ApiRepository apiRepository) {
        this.medias = new MutableLiveData<>();
        this.apiRepository = apiRepository;
    }

    public void initSearchByQuery(String query) {
        this.medias = this.apiRepository.getSearchByQuery(query);
    }

    @NonNull
    public LiveData<List<Media>> getMedias() {
        return medias;
    }
}
