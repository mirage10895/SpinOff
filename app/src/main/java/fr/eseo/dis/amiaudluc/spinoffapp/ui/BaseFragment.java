package fr.eseo.dis.amiaudluc.spinoffapp.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.EndlessRecyclerViewScrollListener;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;

/**
 * Created by lucasamiaud on 19/03/2018.
 */

public abstract class BaseFragment extends Fragment implements SearchInterface {


    public EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    public SwipeRefreshLayout swipeContainer;
    public AppDatabase db;
    public View view;
    public RecyclerView recycler;

    protected Integer selectedContextId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.view = inflater.inflate(R.layout.layout_main, container, false);
        this.db = AppDatabase.getAppDatabase(this.getContext());

        this.recycler = this.view.findViewById(R.id.cardList);
        this.recycler.setHasFixedSize(true);
        int columns = getResources().getInteger(R.integer.scripts_columns);
        this.recycler.setLayoutManager(new GridLayoutManager(this.getContext(), columns));

        this.endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) this.recycler.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                onRecyclerLoadMore(page);
            }
        };

        this.recycler.addOnScrollListener(this.endlessRecyclerViewScrollListener);

        return this.view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (getActivity() != null) {
            MenuInflater menuInflater = getActivity().getMenuInflater();
            menuInflater.inflate(R.menu.context_menu_main, menu);
        }
    }

    public void initializeSwipeContainer() {
        this.swipeContainer = this.view.findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.white);
    }

    @Override
    public void setType(FragmentType type) {
        //
    }

    public abstract void onRecyclerLoadMore(Integer page);

    @Override
    public void onDetach() {
        this.endlessRecyclerViewScrollListener.resetState();
        super.onDetach();
    }
}
