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
 * Created by lucasamiaud on 03/03/2018.
 */

public class TopRatedSeriesFragment extends BaseSerieFragment {
    
    private Context ctx;
    private SeriesAdapter seriesAdapter;
    private View topRatedSeriesView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        topRatedSeriesView = inflater.inflate(R.layout.layout_main, container, false);
        ctx = topRatedSeriesView.getContext();
        db = AppDatabase.getAppDatabase(ctx);
        this.serieViewModel = new SerieViewModel(ApiRepository.getInstance());
        this.serieViewModel.initTopRatedSeries(1);
        observeSeries();

        RecyclerView recycler = (RecyclerView) topRatedSeriesView.findViewById(R.id.cardList);
        recycler.setHasFixedSize(true);
        int columns = getResources().getInteger(R.integer.scripts_columns);
        recycler.setLayoutManager(new GridLayoutManager(ctx, columns));

        seriesAdapter = new SeriesAdapter(ctx,this, new ArrayList<>());
        recycler.setAdapter(seriesAdapter);

        initializeSwipeContainer();

        this.endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager)recycler.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                serieViewModel.initTopRatedSeries(page + 1);
                observeSeries();
            }
        };
        recycler.addOnScrollListener(this.endlessRecyclerViewScrollListener);

        return topRatedSeriesView;
    }

    private void observeSeries() {
        this.serieViewModel.getSeries().observe(this, series -> {
            swipeContainer.setRefreshing(false);
            topRatedSeriesView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            topRatedSeriesView.findViewById(R.id.cardList).setVisibility(View.VISIBLE);
            topRatedSeriesView.findViewById(R.id.no_media_display).setVisibility(View.GONE);
            if (series != null) {
                loadSeries(series.stream().map(Serie::toDataBaseFormat).collect(Collectors.toList()));
            } else {
                topRatedSeriesView.findViewById(R.id.cardList).setVisibility(View.GONE);
                topRatedSeriesView.findViewById(R.id.no_media_display).setVisibility(View.VISIBLE);
                Snackbar.make(topRatedSeriesView, R.string.no_results, Snackbar.LENGTH_LONG)
                        .setAction("Refresh", null).show();
            }
        });
    }

    private void loadSeries(List<SerieDatabase> serieDatabases){
        seriesAdapter.setSeries(serieDatabases);
        seriesAdapter.notifyDataSetChanged();
    }

    private void initializeSwipeContainer() {
        swipeContainer = (SwipeRefreshLayout) topRatedSeriesView.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(() -> {
            this.serieViewModel.initTopRatedSeries(1);
            this.observeSeries();
        });

        swipeContainer.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.white);
    }
}
