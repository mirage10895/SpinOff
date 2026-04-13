package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery;

import android.app.Application;

import org.jetbrains.annotations.NotNull;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryType;

public class DiscoveryViewModel extends AndroidViewModel {

    private final MutableLiveData<DiscoveryType> movieFilter = new MutableLiveData<>(DiscoveryType.POPULAR);
    private final MutableLiveData<DiscoveryType> serieFilter = new MutableLiveData<>(DiscoveryType.POPULAR);

    public DiscoveryViewModel(@NotNull Application application) {
        super(application);
    }

    public LiveData<DiscoveryType> getFilter(FragmentType fragmentType) {
        return fragmentType == FragmentType.MOVIE ? movieFilter : serieFilter;
    }

    public void setFilter(DiscoveryType type, FragmentType fragmentType) {
        if (fragmentType == FragmentType.MOVIE) {
            movieFilter.setValue(type);
        } else {
            serieFilter.setValue(type);
        }
    }
}
