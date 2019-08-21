package fr.eseo.dis.amiaudluc.spinoffapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.EndlessRecyclerViewScrollListener;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.AppDatabase;

/**
 * Created by lucasamiaud on 19/03/2018.
 */

public abstract class BaseFragment extends Fragment implements SearchInterface {


    public EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    public SwipeRefreshLayout swipeContainer;
    public AppDatabase db;
    public String type;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getAppDatabase(this.getContext());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (getActivity() != null) {
            MenuInflater menuInflater = getActivity().getMenuInflater();
            menuInflater.inflate(R.menu.context_menu_main, menu);
        }
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void onDetach() {
        endlessRecyclerViewScrollListener.resetState();
        super.onDetach();
    }
}
