package fr.eseo.dis.amiaudluc.spinoffapp.ui.action;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SerieViewModel;

/**
 * Created by lucasamiaud on 28/12/2018.
 */

public class DeleteSerieActionListener implements View.OnClickListener {

    private final SerieViewModel serieViewModel;
    private final Serie serie;


    public DeleteSerieActionListener(SerieViewModel serieViewModel, Serie serie) {
        this.serieViewModel = serieViewModel;
        this.serie = serie;
    }

    @Override
    public void onClick(View v) {
        this.serieViewModel.deleteById(serie.getId());
        Snackbar.make(v, R.string.serie_deleted, Snackbar.LENGTH_LONG)
                .setAction("Undo", new AddSerieActionListener(this.serieViewModel, this.serie)).show();
    }
}
