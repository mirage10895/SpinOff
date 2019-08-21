package fr.eseo.dis.amiaudluc.spinoffapp.ui.movies;

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

import fr.eseo.dis.amiaudluc.spinoffapp.ui.BaseMovieFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.EndlessRecyclerViewScrollListener;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.repository.ApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.view_model.MovieViewModel;

/**
 * Created by lucasamiaud on 03/03/2018.
 */

public class TopRatedMoviesFragment extends BaseMovieFragment {
    private Context ctx;
    private MoviesAdapter moviesAdapter;
    private View topRatedMoviesView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        topRatedMoviesView = inflater.inflate(R.layout.layout_main, container, false);
        ctx = topRatedMoviesView.getContext();

        db = AppDatabase.getAppDatabase(ctx);

        final RecyclerView recycler = (RecyclerView) topRatedMoviesView.findViewById(R.id.cardList);
        recycler.setHasFixedSize(true);
        int columns = getResources().getInteger(R.integer.scripts_columns);
        recycler.setLayoutManager(new GridLayoutManager(ctx, columns));

        this.movieViewModel = new MovieViewModel(ApiRepository.getInstance());
        this.moviesAdapter = new MoviesAdapter(ctx,this, new ArrayList<>());
        recycler.setAdapter(moviesAdapter);

        initializeSwipeContainer();

        this.endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager)recycler.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                movieViewModel.initTopRatedMovies(page + 1);
                observeMovies();
            }
        };
        recycler.addOnScrollListener(endlessRecyclerViewScrollListener);

        this.movieViewModel.initTopRatedMovies(1);
        this.observeMovies();

        return topRatedMoviesView;
    }

    private void observeMovies() {
        this.movieViewModel.getMovies().observe(this, movies -> {
            swipeContainer.setRefreshing(false);
            topRatedMoviesView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            topRatedMoviesView.findViewById(R.id.cardList).setVisibility(View.VISIBLE);
            topRatedMoviesView.findViewById(R.id.no_media_display).setVisibility(View.GONE);
            if (movies != null) {
                loadMovies(movies.stream().map(Movie::toDatabaseFormat).collect(Collectors.toList()));
            } else {
                topRatedMoviesView.findViewById(R.id.cardList).setVisibility(View.GONE);
                topRatedMoviesView.findViewById(R.id.no_media_display).setVisibility(View.VISIBLE);
                Snackbar.make(topRatedMoviesView, R.string.no_results, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void loadMovies(List<MovieDatabase> movies) {
        moviesAdapter.setMovies(movies);
        moviesAdapter.notifyDataSetChanged();
    }

    private void initializeSwipeContainer(){
        swipeContainer = (SwipeRefreshLayout) topRatedMoviesView.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(() -> {
            this.movieViewModel.initTopRatedMovies(1);
            this.observeMovies();
        });

        swipeContainer.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.white);
    }

}
