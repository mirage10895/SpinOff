package fr.eseo.dis.amiaudluc.spinoffapp.ui.action;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.movie.MovieViewModel;

public class AddMovieActionListener implements View.OnClickListener {

    private final MovieViewModel movieViewModel;
    private final int movieId;

    public AddMovieActionListener(MovieViewModel movieViewModel, int movieId) {
        this.movieViewModel = movieViewModel;
        this.movieId = movieId;
    }

    @Override
    public void onClick(View view) {
        view.setEnabled(false);
        this.movieViewModel.insert(this.movieId);
        Snackbar.make(view, R.string.movie_added, Snackbar.LENGTH_LONG)
                .setAction("Undo", new DeleteMovieActionListener(this.movieViewModel, this.movieId)).show();
    }
}
