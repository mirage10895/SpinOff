package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery;

import android.app.Application;

import org.jetbrains.annotations.NotNull;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class DiscoveryViewModel extends AndroidViewModel {

    private final MutableLiveData<DiscoveryType> filter = new MutableLiveData<>(DiscoveryType.POPULAR);

    public DiscoveryViewModel(@NotNull Application application) {
        super(application);
    }

    public LiveData<DiscoveryType> getFilter() {
        return filter;
    }

    public void setFilter(DiscoveryType type) {
        filter.setValue(type);
    }
}
