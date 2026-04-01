package fr.eseo.dis.amiaudluc.spinoffapp.ui.series;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import fr.eseo.dis.amiaudluc.spinoffapp.ui.BaseSerieFragment;

/**
 * Created by lucasamiaud on 01/03/2018.
 */

public class PopularSeriesFragment extends BaseSerieFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.serieViewModel.initPopularSeries(1);
        this.observeSeries();
    }

    @Override
    protected void initializeSwipeContainer(){
        super.initializeSwipeContainer();
        binding.swipeContainer.setOnRefreshListener(() -> {
            this.serieViewModel.initPopularSeries(1);
            this.observeSeries();
        });
    }

    @Override
    public void onRecyclerLoadMore(Integer page) {
        serieViewModel.initPopularSeries(page + 1);
        observeSeries();
    }
}
