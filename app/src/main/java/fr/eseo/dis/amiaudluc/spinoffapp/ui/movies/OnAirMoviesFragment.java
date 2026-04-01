package fr.eseo.dis.amiaudluc.spinoffapp.ui.movies;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import fr.eseo.dis.amiaudluc.spinoffapp.ui.BaseMovieFragment;

/**
 * Created by lucasamiaud on 05/03/2018.
 */

public class OnAirMoviesFragment extends BaseMovieFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.movieViewModel.initOnAirMovies(1);
        this.observeMovies();
    }

    @Override
    protected void initializeSwipeContainer(){
        super.initializeSwipeContainer();
        binding.swipeContainer.setOnRefreshListener(() -> {
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
