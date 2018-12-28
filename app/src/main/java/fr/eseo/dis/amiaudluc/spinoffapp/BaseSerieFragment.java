package fr.eseo.dis.amiaudluc.spinoffapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import fr.eseo.dis.amiaudluc.spinoffapp.action.DeleteSerieActionListener;
import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.DatabaseTransactionManager;
import fr.eseo.dis.amiaudluc.spinoffapp.series.SerieActivity;

/**
 * Created by lucasamiaud on 28/12/2018.
 */

public abstract class BaseSerieFragment extends BaseFragment{

    private View view;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.view = view;
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu_add:
                if (!DatabaseTransactionManager.getAllSerieIds(db).contains(Content.currentSerie.getId())) {
                    DatabaseTransactionManager.addSerieWithSeasons(db, Content.currentSerie);
                    Snackbar.make(this.view, "This movie is already in your library", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new DeleteSerieActionListener(db, Content.currentSerie)).show();
                }else{
                    Snackbar.make(this.view, "This serie is already in your library", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onItemClick(int position) {
        Content.currentSerie = Content.series.get(position);

        Intent intent = new Intent(getContext(), SerieActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, int position) {
        Content.currentSerie = Content.series.get(position);
        onCreateContextMenu(contextMenu,v,menuInfo);
    }
}
