package fr.eseo.dis.amiaudluc.spinoffapp.ui.movies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.eseo.dis.amiaudluc.spinoffapp.ui.BaseMovieFragment;

/**
 * Created by lucasamiaud on 03/03/2018.
 */

public class TopRatedMoviesFragment extends BaseMovieFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        this.initializeSwipeContainer();
        this.movieViewModel.initTopRatedMovies(1);
        super.observeMovies();

        return super.view;
    }

    @Override
    public void initializeSwipeContainer(){
        super.initializeSwipeContainer();
        swipeContainer.setOnRefreshListener(() -> {
            this.movieViewModel.initTopRatedMovies(1);
            super.observeMovies();
        });
    }

    @Override
    public void onRecyclerLoadMore(Integer page) {
        movieViewModel.initTopRatedMovies(page + 1);
        observeMovies();
    }

}
