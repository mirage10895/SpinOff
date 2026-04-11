package fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.pagesearch;

import android.app.Application;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import lombok.Getter;

public abstract class PageSearchViewModel<T, F> extends AndroidViewModel {
    @Getter
    private final MutableLiveData<Integer> page = new MutableLiveData<>(1);
    private final MediatorLiveData<List<T>> _results = new MediatorLiveData<>();
    @Getter
    private final LiveData<List<T>> results = _results;
    private LiveData<F> boundFilter;
    private final Observer<F> filterObserver = filter -> resetSearch();

    public PageSearchViewModel(@NotNull Application application) {
        super(application);
    }

    protected abstract LiveData<List<T>> searchApi(F filter, int pageNumber);

    public void bindFilters(LiveData<F> filters) {
        if (this.boundFilter != null) {
            unbindFilters();
        }
        this.boundFilter = filters;
        if (this.boundFilter != null) {
            this.boundFilter.observeForever(filterObserver);
        }
    }

    public void unbindFilters() {
        if (this.boundFilter != null) {
            this.boundFilter.removeObserver(filterObserver);
            this.boundFilter = null;
        }
    }

    // Method to load next page
    public void loadPage(int nextPage) {
        if (boundFilter == null) {
            return;
        }
        F filter = boundFilter.getValue();
        if (filter == null) {
            return;
        }
        page.setValue(nextPage);

        loadPage(filter, nextPage);
    }

    // Method to reset search
    public void resetSearch() {
        page.setValue(1);                       // reset pagination

        if (boundFilter == null) {
            return;
        }
        F filter = boundFilter.getValue();
        if (filter != null) {
            loadPage(filter, 1);                // load first page with current filter
        }
    }

    private void loadPage(F filter, int pageNumber) {
        LiveData<List<T>> pageLiveData = searchApi(filter, pageNumber);
        _results.addSource(pageLiveData, newPage -> {
            List<T> current = _results.getValue();
            if (current == null || pageNumber == 1) current = new ArrayList<>();
            current.addAll(newPage);
            _results.setValue(current);
            _results.removeSource(pageLiveData); // remove to avoid multiple triggers
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        unbindFilters();
    }
}
