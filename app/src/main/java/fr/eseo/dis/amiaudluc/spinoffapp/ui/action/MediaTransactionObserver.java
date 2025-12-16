package fr.eseo.dis.amiaudluc.spinoffapp.ui.action;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.view_model.MovieViewModel;
import fr.eseo.dis.amiaudluc.spinoffapp.view_model.SerieViewModel;

public class MediaTransactionObserver<T extends Media> implements Observer<T> {
    private final MovieViewModel movieViewModel;
    private final SerieViewModel serieViewModel;
    private final View view;
    private final boolean shouldDelete;

    public MediaTransactionObserver(
            MovieViewModel movieViewModel,
            SerieViewModel serieViewModel,
            View view,
            boolean shouldDelete
    ) {
        this.movieViewModel = movieViewModel;
        this.serieViewModel = serieViewModel;
        this.view = view;
        this.shouldDelete = shouldDelete;
    }

    @Override
    public void onChanged(@Nullable T o) {
        if (o == null) {
            Snackbar.make(this.view, R.string.error_library_transaction, Snackbar.LENGTH_LONG)
                    .setAction("DAMN", view1 -> view1.setVisibility(View.GONE)).show();
            return;
        }
        if (shouldDelete) {
            if (o instanceof Movie) {
                Movie movieToDelete = (Movie) o;
                this.movieViewModel.deleteMovieById(movieToDelete.getId());
                Snackbar.make(this.view, R.string.movie_deleted, Snackbar.LENGTH_LONG)
                        .setAction("Undo", new AddMovieActionListener(this.movieViewModel, movieToDelete)).show();
            } else if (o instanceof Serie) {
                Serie serieToAdd = (Serie) o;
                this.serieViewModel.deleteById(serieToAdd.getId());
                Snackbar.make(this.view, R.string.serie_deleted, Snackbar.LENGTH_LONG)
                        .setAction("Undo", new AddSerieActionListener(this.serieViewModel, serieToAdd)).show();
            }
        } else {
            if (o instanceof Movie) {
                Movie movieToAdd = (Movie) o;
                this.movieViewModel.insert(movieToAdd.toDatabaseFormat());
                Snackbar.make(this.view, R.string.movie_added, Snackbar.LENGTH_LONG)
                        .setAction("Undo", new DeleteMovieActionListener(this.movieViewModel, movieToAdd)).show();
            } else if (o instanceof Serie) {
                Serie serieToAdd = (Serie) o;
                this.serieViewModel.insert(serieToAdd.getId());
                Snackbar.make(this.view, R.string.serie_added, Snackbar.LENGTH_LONG)
                        .setAction("Undo", new DeleteSerieActionListener(this.serieViewModel, serieToAdd)).show();
            }
        }
    }
}
