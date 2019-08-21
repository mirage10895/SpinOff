package fr.eseo.dis.amiaudluc.spinoffapp.ui.series;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.ui.BaseSerieFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.EndlessRecyclerViewScrollListener;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.repository.ApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.view_model.SerieViewModel;

/**
 * Created by lucasamiaud on 05/03/2018.
 */

public class OnAirSeriesFragment extends BaseSerieFragment {
    private Context ctx;
    private SeriesAdapter seriesAdapter;
    private View onAirSeriesView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        onAirSeriesView = inflater.inflate(R.layout.layout_main, container, false);
        ctx = onAirSeriesView.getContext();
        this.serieViewModel = new SerieViewModel(ApiRepository.getInstance());
        this.db = AppDatabase.getAppDatabase(ctx);

        this.serieViewModel.initOnAirSeries(1);
        observeSeries();

        RecyclerView recycler = (RecyclerView) onAirSeriesView.findViewById(R.id.cardList);
        recycler.setHasFixedSize(true);
        int columns = getResources().getInteger(R.integer.scripts_columns);
        recycler.setLayoutManager(new GridLayoutManager(ctx, columns));

        seriesAdapter = new SeriesAdapter(ctx,this, new ArrayList<>());
        recycler.setAdapter(seriesAdapter);

        initializeSwipeContainer();

        this.endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager)recycler.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                serieViewModel.initOnAirSeries(page + 1);
                observeSeries();
            }
        };
        recycler.addOnScrollListener(this.endlessRecyclerViewScrollListener);

        return onAirSeriesView;
    }

    private void observeSeries() {
        this.serieViewModel.getSeries().observe(this, series -> {
            swipeContainer.setRefreshing(false);
            onAirSeriesView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            onAirSeriesView.findViewById(R.id.cardList).setVisibility(View.VISIBLE);
            onAirSeriesView.findViewById(R.id.no_media_display).setVisibility(View.GONE);
            if (series != null) {
                loadSeries(series.stream().map(Serie::toDataBaseFormat).collect(Collectors.toList()));
            } else {
                onAirSeriesView.findViewById(R.id.cardList).setVisibility(View.GONE);
                onAirSeriesView.findViewById(R.id.no_media_display).setVisibility(View.VISIBLE);
                Snackbar.make(onAirSeriesView, R.string.no_results, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void loadSeries(List<SerieDatabase> series){
        seriesAdapter.setSeries(series);
        seriesAdapter.notifyDataSetChanged();
    }

    private void initializeSwipeContainer(){
        swipeContainer = (SwipeRefreshLayout) onAirSeriesView.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(() -> {
            this.serieViewModel.initOnAirSeries(1);
            observeSeries();
        });

        swipeContainer.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.white);
    }
}
