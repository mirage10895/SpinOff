package fr.eseo.dis.amiaudluc.spinoffapp.ui.movies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.eseo.dis.amiaudluc.spinoffapp.ui.BaseMovieFragment;

/**
 * Created by lucasamiaud on 05/03/2018.
 */

public class OnAirMoviesFragment extends BaseMovieFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        this.initializeSwipeContainer();
        this.movieViewModel.initOnAirMovies(1);
        this.observeMovies();

        return super.view;
    }

    @Override
    public void initializeSwipeContainer(){
        super.initializeSwipeContainer();
        swipeContainer.setOnRefreshListener(() -> {
            this.movieViewModel.initOnAirMovies(1);
            this.observeMovies();
        });
    }

    @Override
    public void onRecyclerLoadMore(Integer page) {
        movieViewModel.initOnAirMovies(page + 1);
        observeMovies();
    }

}
