package fr.eseo.dis.amiaudluc.spinoffapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;

import fr.eseo.dis.amiaudluc.spinoffapp.common.EndlessRecyclerViewScrollListener;
import fr.eseo.dis.amiaudluc.spinoffapp.common.FilterKingFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.AppDatabase;

/**
 * Created by lucasamiaud on 19/03/2018.
 */

public abstract class BaseFragment extends Fragment implements SearchInterface{


    public EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    public SwipeRefreshLayout swipeContainer;
    public FilterKingFragment fKFragment;
    public AppDatabase db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = AppDatabase.getAppDatabase(this.getContext());

        fKFragment = new FilterKingFragment();
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction()
                .replace(R.id.filterKing, fKFragment)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_main, menu);
    }

    @Override
    public void setType(String type) {
        //To override
    }

    @Override
    public void onItemClick(int position) {
        //To override
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, int position) {
        //To override
    }
}
