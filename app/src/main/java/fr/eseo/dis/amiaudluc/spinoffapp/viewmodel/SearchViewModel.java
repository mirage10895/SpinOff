package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.ApiRepository;
import lombok.Getter;

public class SearchViewModel extends AndroidViewModel {
    @Getter
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
}
