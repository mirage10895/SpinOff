package fr.eseo.dis.amiaudluc.spinoffapp.ui.action;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.DatabaseTransactionManager;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;

public class MediaTransactionObserver<T extends Media> implements Observer<T> {
    private AppDatabase db;
    private View view;
    private Boolean shouldDelete;

    public MediaTransactionObserver(AppDatabase db, View view, Boolean shouldDelete) {
        this.db = db;
        this.view = view;
        this.shouldDelete = shouldDelete;
    }

    @Override
    public void onChanged(@Nullable T o) {
        if (o != null) {
            if (shouldDelete) {
                if (o instanceof Movie) {
                    Movie movieToDelete = (Movie) o;
                    DatabaseTransactionManager.executeAsync(() -> this.db.moviesDAO().deleteMovie(movieToDelete));
                    Snackbar.make(this.view, R.string.movie_deleted, Snackbar.LENGTH_LONG)
                            .setAction("Undo", new AddMovieActionListener(this.db, movieToDelete)).show();
                } else if (o instanceof Serie) {
                    Serie serieToAdd = (Serie) o;
                    DatabaseTransactionManager.executeAsync(() -> this.db.serieDAO().deleteSerie(serieToAdd));
                    Snackbar.make(this.view, R.string.serie_deleted, Snackbar.LENGTH_LONG)
                            .setAction("Undo", new AddSerieActionListener(this.db, serieToAdd)).show();
                }
            } else {
                if (o instanceof Movie) {
                    Movie movieToAdd = (Movie) o;
                    DatabaseTransactionManager.executeAsync(() -> this.db.moviesDAO().insertMovie(movieToAdd));
                    Snackbar.make(this.view, R.string.movie_added, Snackbar.LENGTH_LONG)
                            .setAction("Undo", new DeleteMovieActionListener(this.db, movieToAdd)).show();
                } else if (o instanceof Serie) {
                    Serie serieToAdd = (Serie) o;
                    serieToAdd.setAverageEpisodeRunTime(serieToAdd.getEpisodeRunTime().stream().mapToDouble(Integer::doubleValue).average().orElse(0D));
                    DatabaseTransactionManager.executeAsync(() -> this.db.serieDAO().insertSerie(serieToAdd));
                    Snackbar.make(this.view, R.string.serie_added, Snackbar.LENGTH_LONG)
                            .setAction("Undo", new DeleteSerieActionListener(this.db, serieToAdd)).show();
                }
            }

        } else {
            Snackbar.make(this.view, R.string.error_library_transaction, Snackbar.LENGTH_LONG)
                    .setAction("DAMN", view1 -> view1.setVisibility(View.GONE)).show();
        }
    }
}
