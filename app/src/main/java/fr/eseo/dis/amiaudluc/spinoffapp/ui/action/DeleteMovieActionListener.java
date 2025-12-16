package fr.eseo.dis.amiaudluc.spinoffapp.ui.action;

import com.google.android.material.snackbar.Snackbar;
import android.view.View;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.view_model.MovieViewModel;

/**
 * Created by lucasamiaud on 27/12/2018.
 */

public class DeleteMovieActionListener implements View.OnClickListener {

    private final MovieViewModel movieViewModel;
    private final Movie movie;


    public DeleteMovieActionListener(MovieViewModel movieViewModel, Movie movie) {
        this.movieViewModel = movieViewModel;
        this.movie = movie;
    }

    @Override
    public void onClick(View v) {
        this.movieViewModel.deleteMovieById(movie.getId());
        Snackbar.make(v, R.string.movie_deleted, Snackbar.LENGTH_LONG)
                .setAction("Undo", new AddMovieActionListener(this.movieViewModel, this.movie)).show();
    }
}
