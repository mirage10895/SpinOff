package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.repositories.tmdb.ApiRepository;
import lombok.Getter;

public class ArtistViewModel extends AndroidViewModel {

    private static final String ARTIST_ID_KEY = "artistId";
    private final SavedStateHandle savedStateHandle;
    private final LiveData<Integer> artistIdTrigger;

    @Getter
    private final LiveData<Artist> artist;

    private final ApiRepository apiRepository;

    public ArtistViewModel(@NonNull Application application, @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        this.savedStateHandle = savedStateHandle;
        this.artistIdTrigger = savedStateHandle.getLiveData(ARTIST_ID_KEY);

        this.apiRepository = ApiRepository.getInstance();

        this.artist = Transformations.switchMap(artistIdTrigger, apiRepository::getArtistById);
    }

    public void initGetArtistById(Integer id) {
        if (!id.equals(savedStateHandle.get(ARTIST_ID_KEY))) {
            savedStateHandle.set(ARTIST_ID_KEY, id);
        }
    }
}
