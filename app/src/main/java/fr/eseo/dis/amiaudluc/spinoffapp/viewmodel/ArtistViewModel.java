package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.tmdb.ApiRepository;
import lombok.Getter;

public class ArtistViewModel extends AndroidViewModel {
    
    private final MutableLiveData<Integer> artistIdTrigger = new MutableLiveData<>();
    
    @Getter
    private final LiveData<Artist> artist;

    private final ApiRepository apiRepository;

    public ArtistViewModel(@NonNull Application application) {
        super(application);
        this.apiRepository = ApiRepository.getInstance();
        
        this.artist = Transformations.switchMap(artistIdTrigger, apiRepository::getArtistById);
    }

    public void initGetArtistById(Integer id) {
        artistIdTrigger.setValue(id);
    }
}
