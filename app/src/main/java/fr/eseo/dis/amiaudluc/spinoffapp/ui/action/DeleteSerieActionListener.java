package fr.eseo.dis.amiaudluc.spinoffapp.ui.action;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.SerieViewModel;

/**
 * Created by lucasamiaud on 28/12/2018.
 */

public class DeleteSerieActionListener implements View.OnClickListener {

    private final SerieViewModel serieViewModel;
    private final int serieId;


    public DeleteSerieActionListener(SerieViewModel serieViewModel, int serieId) {
        this.serieViewModel = serieViewModel;
        this.serieId = serieId;
    }

    @Override
    public void onClick(View v) {
        this.serieViewModel.deleteById(this.serieId);
        Snackbar.make(v, R.string.serie_deleted, Snackbar.LENGTH_LONG)
                .setAction("Undo", new AddSerieActionListener(this.serieViewModel, this.serieId)).show();
    }
}
