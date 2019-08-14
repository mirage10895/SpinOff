package fr.eseo.dis.amiaudluc.spinoffapp.action;

import android.support.design.widget.Snackbar;
import android.view.View;

import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.DatabaseTransactionManager;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;

/**
 * Created by lucasamiaud on 28/12/2018.
 */

public class DeleteSerieActionListener implements View.OnClickListener {

    private AppDatabase db;
    private Serie serie;


    public DeleteSerieActionListener(AppDatabase db, Serie serie) {
        this.db = db;
        this.serie = serie;
    }
    @Override
    public void onClick(View v) {
        DatabaseTransactionManager.executeAsync(() -> db.serieDAO().deleteSerie(serie));
        Snackbar.make(v,"Serie deleted from your library", Snackbar.LENGTH_LONG)
                .setAction("Action",null).show();
    }
}
