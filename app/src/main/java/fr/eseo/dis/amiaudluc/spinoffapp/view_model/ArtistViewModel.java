package fr.eseo.dis.amiaudluc.spinoffapp.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.ApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Artist;

public class ArtistViewModel extends AndroidViewModel {
    private LiveData<Artist> artist;

    private final ApiRepository apiRepository;

    public ArtistViewModel(@NonNull Application application) {
        super(application);
        this.apiRepository = ApiRepository.getInstance();;
    }

    public void initGetArtistById(Integer id) {
        this.artist = this.apiRepository.getArtistById(id);
    }

    public LiveData<Artist> getArtist() {
        return artist;
    }
}
