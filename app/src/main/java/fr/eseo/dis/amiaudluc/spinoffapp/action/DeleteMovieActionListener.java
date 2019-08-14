package fr.eseo.dis.amiaudluc.spinoffapp.action;

import android.support.design.widget.Snackbar;
import android.view.View;

import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.DatabaseTransactionManager;
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
        Snackbar.make(v,"Movie deleted from your library", Snackbar.LENGTH_LONG)
                .setAction("Action",null).show();
    }
}
