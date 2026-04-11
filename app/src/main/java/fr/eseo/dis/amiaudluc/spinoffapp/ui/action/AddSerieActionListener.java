package fr.eseo.dis.amiaudluc.spinoffapp.ui.action;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.SerieViewModel;

public class AddSerieActionListener implements View.OnClickListener {

    private final SerieViewModel serieViewModel;
    private final int serieId;


    public AddSerieActionListener(SerieViewModel serieViewModel, int serieId) {
        this.serieViewModel = serieViewModel;
        this.serieId = serieId;
    }

    @Override
    public void onClick(View view) {
        this.serieViewModel.insert(this.serieId);
        Snackbar.make(view, R.string.serie_added, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo_action, new DeleteSerieActionListener(this.serieViewModel, this.serieId)).show();
    }
}
