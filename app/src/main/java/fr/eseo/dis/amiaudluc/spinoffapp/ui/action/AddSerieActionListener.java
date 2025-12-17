package fr.eseo.dis.amiaudluc.spinoffapp.ui.action;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SerieViewModel;

public class AddSerieActionListener implements View.OnClickListener {

    private final SerieViewModel serieViewModel;
    private final Serie serie;


    public AddSerieActionListener(SerieViewModel serieViewModel, Serie serie) {
        this.serieViewModel = serieViewModel;
        this.serie = serie;
    }

    @Override
    public void onClick(View view) {
        this.serieViewModel.insert(serie.getId());
        Snackbar.make(view, R.string.serie_added, Snackbar.LENGTH_LONG)
                .setAction("Undo", new DeleteSerieActionListener(this.serieViewModel, this.serie)).show();
    }
}
