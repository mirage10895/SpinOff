package fr.eseo.dis.amiaudluc.spinoffapp.view_model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import fr.eseo.dis.amiaudluc.spinoffapp.model.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.repository.ApiRepository;

public class ArtistViewModel extends ViewModel {
    private LiveData<Artist> artist;

    private ApiRepository apiRepository;

    public ArtistViewModel(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
    }

    public void initGetArtistById(Integer id) {
        this.artist = this.apiRepository.getArtistById(id);
    }

    public LiveData<Artist> getArtist() {
        return artist;
    }
}
