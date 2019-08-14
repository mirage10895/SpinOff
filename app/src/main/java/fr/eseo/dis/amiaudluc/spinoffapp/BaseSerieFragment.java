package fr.eseo.dis.amiaudluc.spinoffapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import fr.eseo.dis.amiaudluc.spinoffapp.action.DeleteSerieActionListener;
import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.DatabaseTransactionManager;
import fr.eseo.dis.amiaudluc.spinoffapp.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.view_model.SerieViewModel;

/**
 * Created by lucasamiaud on 28/12/2018.
 */

public abstract class BaseSerieFragment extends BaseFragment {

    public SerieViewModel serieViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.context_menu_add) {
            //TODO refacto add to database
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(Integer id) {
        Intent intent = new Intent(getContext(), SerieActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer position) {
        onCreateContextMenu(contextMenu, v, menuInfo);
    }
}
