package fr.eseo.dis.amiaudluc.spinoffapp.ui.action;

import com.google.android.material.snackbar.Snackbar;
import android.view.View;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.MovieViewModel;

public class AddMovieActionListener implements View.OnClickListener {

    private final MovieViewModel movieViewModel;
    private final Movie movie;

    public AddMovieActionListener(MovieViewModel movieViewModel, Movie movie) {
        this.movieViewModel = movieViewModel;
        this.movie = movie;
    }

    @Override
    public void onClick(View view) {
        view.setEnabled(false);
        this.movieViewModel.insert(this.movie.toDatabaseFormat());
        Snackbar.make(view, R.string.movie_added, Snackbar.LENGTH_LONG)
                .setAction("Undo", new DeleteMovieActionListener(this.movieViewModel, this.movie)).show();
    }
}
