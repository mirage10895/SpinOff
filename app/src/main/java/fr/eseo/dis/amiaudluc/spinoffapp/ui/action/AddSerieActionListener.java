package fr.eseo.dis.amiaudluc.spinoffapp.ui.action;

import android.support.design.widget.Snackbar;
import android.view.View;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.DatabaseTransactionManager;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;

public class AddSerieActionListener implements View.OnClickListener {

    private AppDatabase db;
    private Serie serie;


    public AddSerieActionListener(AppDatabase db, Serie serie) {
        this.db = db;
        this.serie = serie;
    }

    @Override
    public void onClick(View view) {
        this.serie.setAverageEpisodeRunTime(serie.getEpisodeRunTime().stream().mapToDouble(Integer::doubleValue).average().orElse(0D));
        DatabaseTransactionManager.executeAsync(() -> db.serieDAO().insertSerie(serie));
        Snackbar.make(view, R.string.serie_added, Snackbar.LENGTH_LONG)
                .setAction("Undo", new DeleteSerieActionListener(this.db, this.serie)).show();
    }
}
