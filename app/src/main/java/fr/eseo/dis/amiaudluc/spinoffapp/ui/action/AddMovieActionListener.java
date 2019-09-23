package fr.eseo.dis.amiaudluc.spinoffapp.ui.action;

import android.support.design.widget.Snackbar;
import android.view.View;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.DatabaseTransactionManager;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;

public class AddMovieActionListener implements View.OnClickListener {

    private AppDatabase db;
    private Movie movie;


    public AddMovieActionListener(AppDatabase db, Movie movie) {
        this.db = db;
        this.movie = movie;
    }

    @Override
    public void onClick(View view) {
        view.setEnabled(false);
        DatabaseTransactionManager.executeAsync(() -> db.moviesDAO().insertMovie(this.movie));
        Snackbar.make(view, R.string.movie_added, Snackbar.LENGTH_LONG)
                .setAction("Undo", new DeleteMovieActionListener(this.db, this.movie)).show();
    }
}
