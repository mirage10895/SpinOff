package fr.eseo.dis.amiaudluc.spinoffapp.ui.series;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import fr.eseo.dis.amiaudluc.spinoffapp.ui.BaseSerieFragment;

/**
 * Created by lucasamiaud on 03/03/2018.
 */

public class TopRatedSeriesFragment extends BaseSerieFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.serieViewModel.initTopRatedSeries(1);
        this.observeSeries();
    }

    @Override
    protected void initializeSwipeContainer(){
        super.initializeSwipeContainer();
        binding.swipeContainer.setOnRefreshListener(() -> {
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
