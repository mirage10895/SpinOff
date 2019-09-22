package fr.eseo.dis.amiaudluc.spinoffapp.ui.series;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.eseo.dis.amiaudluc.spinoffapp.ui.BaseSerieFragment;

/**
 * Created by lucasamiaud on 01/03/2018.
 */

public class PopularSeriesFragment extends BaseSerieFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        this.serieViewModel.initPopularSeries(1);
        observeSeries();
        this.initializeSwipeContainer();

        return this.view;
    }

    @Override
    public void initializeSwipeContainer(){
        super.initializeSwipeContainer();
        super.swipeContainer.setOnRefreshListener(() -> {
            serieViewModel.initPopularSeries(1);
            observeSeries();
        });
    }

    @Override
    public void onRecyclerLoadMore(Integer page) {
        serieViewModel.initPopularSeries(page + 1);
        observeSeries();
    }
}
