package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery;

import android.app.Application;

import org.jetbrains.annotations.NotNull;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryFilter;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryType;

public class DiscoveryViewModel extends AndroidViewModel {

    private final MutableLiveData<DiscoveryFilter> movieFilter = new MutableLiveData<>(DiscoveryFilter.defaultFilter());
    private final MutableLiveData<DiscoveryFilter> serieFilter = new MutableLiveData<>(DiscoveryFilter.defaultFilter());

    public DiscoveryViewModel(@NotNull Application application) {
        super(application);
    }

    public LiveData<DiscoveryFilter> getFilter(FragmentType fragmentType) {
        return fragmentType == FragmentType.MOVIE ? movieFilter : serieFilter;
    }

    public void setFilter(DiscoveryFilter filter, FragmentType fragmentType) {
        if (fragmentType == FragmentType.MOVIE) {
            movieFilter.setValue(filter);
        } else {
            serieFilter.setValue(filter);
        }
    }

    public void setType(DiscoveryType type, FragmentType fragmentType) {
        DiscoveryFilter current = getFilter(fragmentType).getValue();
        if (current != null) {
            setFilter(new DiscoveryFilter(type, current.extraFilters()), fragmentType);
        }
    }
}
