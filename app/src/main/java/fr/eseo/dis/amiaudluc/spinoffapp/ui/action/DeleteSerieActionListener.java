package fr.eseo.dis.amiaudluc.spinoffapp.ui.action;

import android.support.design.widget.Snackbar;
import android.view.View;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.DatabaseTransactionManager;
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
        Snackbar.make(v, R.string.serie_deleted, Snackbar.LENGTH_LONG)
                .setAction("Undo", new AddSerieActionListener(this.db, this.serie)).show();
    }
}
