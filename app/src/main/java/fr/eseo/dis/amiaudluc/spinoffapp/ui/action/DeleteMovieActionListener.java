package fr.eseo.dis.amiaudluc.spinoffapp.ui.action;

import android.support.design.widget.Snackbar;
import android.view.View;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.DatabaseTransactionManager;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;

/**
 * Created by lucasamiaud on 27/12/2018.
 */

public class DeleteMovieActionListener implements View.OnClickListener {

    private AppDatabase db;
    private Movie movie;


    public DeleteMovieActionListener(AppDatabase db, Movie movie) {
        this.db = db;
        this.movie = movie;
    }

    @Override
    public void onClick(View v) {
        DatabaseTransactionManager.executeAsync(() -> db.moviesDAO().deleteMovie(movie));
        Snackbar.make(v, R.string.movie_deleted, Snackbar.LENGTH_LONG)
                .setAction("Undo", new AddMovieActionListener(this.db, this.movie)).show();
    }
}
