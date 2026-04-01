package fr.eseo.dis.amiaudluc.spinoffapp.ui.movies;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import fr.eseo.dis.amiaudluc.spinoffapp.ui.BaseMovieFragment;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

public class PopularMoviesFragment extends BaseMovieFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.movieViewModel.initPopularMovies(1);
        this.observeMovies();
    }

    @Override
    protected void initializeSwipeContainer(){
        super.initializeSwipeContainer();
        binding.swipeContainer.setOnRefreshListener(() -> {
            this.movieViewModel.initPopularMovies(1);
            this.observeMovies();
        });
    }

    @Override
    public void onRecyclerLoadMore(Integer page) {
        movieViewModel.initPopularMovies(page + 1);
        observeMovies();
    }
}
