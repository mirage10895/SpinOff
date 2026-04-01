package fr.eseo.dis.amiaudluc.spinoffapp.ui.action;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.MovieViewModel;

/**
 * Created by lucasamiaud on 27/12/2018.
 */

public class DeleteMovieActionListener implements View.OnClickListener {

    private final MovieViewModel movieViewModel;
    private final int movieId;


    public DeleteMovieActionListener(MovieViewModel movieViewModel, int movieId) {
        this.movieViewModel = movieViewModel;
        this.movieId = movieId;
    }

    @Override
    public void onClick(View v) {
        this.movieViewModel.deleteMovieById(this.movieId);
        Snackbar.make(v, R.string.movie_deleted, Snackbar.LENGTH_LONG)
                .setAction("Undo", new AddMovieActionListener(this.movieViewModel, this.movieId)).show();
    }
}
