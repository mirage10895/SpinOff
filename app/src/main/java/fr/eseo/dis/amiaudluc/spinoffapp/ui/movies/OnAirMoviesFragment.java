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
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.repository.ApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.view_model.MovieViewModel;

/**
 * Created by lucasamiaud on 05/03/2018.
 */

public class OnAirMoviesFragment extends BaseMovieFragment {

    private Context ctx;
    private View onAirMoviesView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        onAirMoviesView = inflater.inflate(R.layout.layout_main, container, false);
        ctx = onAirMoviesView.getContext();
        this.movieViewModel = new MovieViewModel(ApiRepository.getInstance());

        RecyclerView recycler = (RecyclerView) onAirMoviesView.findViewById(R.id.cardList);
        recycler.setHasFixedSize(true);
        int columns = getResources().getInteger(R.integer.scripts_columns);
        recycler.setLayoutManager(new GridLayoutManager(ctx, columns));

        moviesAdapter = new MoviesAdapter(ctx,this, new ArrayList<>());
        recycler.setAdapter(moviesAdapter);

        this.endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager)recycler.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                movieViewModel.initOnAirMovies(page + 1);
                observeMovies();
            }
        };
        recycler.addOnScrollListener(this.endlessRecyclerViewScrollListener);

        initializeSwipeContainer();
        this.movieViewModel.initOnAirMovies(1);
        this.observeMovies();

        return onAirMoviesView;
    }

    private void observeMovies() {
        this.movieViewModel.getMovies().observe(this, movies -> {
            swipeContainer.setRefreshing(false);
            onAirMoviesView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            onAirMoviesView.findViewById(R.id.cardList).setVisibility(View.VISIBLE);
            onAirMoviesView.findViewById(R.id.no_media_display).setVisibility(View.GONE);
            if (movies != null) {
                loadMovies(movies.stream().map(Movie::toDatabaseFormat).collect(Collectors.toList()));
            } else {
                onAirMoviesView.findViewById(R.id.cardList).setVisibility(View.GONE);
                onAirMoviesView.findViewById(R.id.no_media_display).setVisibility(View.VISIBLE);
                Snackbar.make(onAirMoviesView, R.string.no_results, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void loadMovies(List<MovieDatabase> movieDatabases){
        moviesAdapter.setMovies(movieDatabases);
        moviesAdapter.notifyDataSetChanged();
    }

    private void initializeSwipeContainer(){
        swipeContainer = (SwipeRefreshLayout) onAirMoviesView.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(() -> {
            this.movieViewModel.initOnAirMovies(1);
            this.observeMovies();
        });

        swipeContainer.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.white);
    }
}
