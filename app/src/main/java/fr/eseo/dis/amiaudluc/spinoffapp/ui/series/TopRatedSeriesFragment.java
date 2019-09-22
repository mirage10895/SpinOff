package fr.eseo.dis.amiaudluc.spinoffapp.ui.series;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.eseo.dis.amiaudluc.spinoffapp.ui.BaseSerieFragment;

/**
 * Created by lucasamiaud on 03/03/2018.
 */

public class TopRatedSeriesFragment extends BaseSerieFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        this.serieViewModel.initTopRatedSeries(1);
        observeSeries();
        this.initializeSwipeContainer();

        return super.view;
    }

    @Override
    public void initializeSwipeContainer() {
        super.initializeSwipeContainer();
        swipeContainer.setOnRefreshListener(() -> {
            this.serieViewModel.initTopRatedSeries(1);
            this.observeSeries();
        });
    }

    @Override
    public void onRecyclerLoadMore(Integer page) {
        serieViewModel.initTopRatedSeries(page + 1);
        observeSeries();
    }
}
