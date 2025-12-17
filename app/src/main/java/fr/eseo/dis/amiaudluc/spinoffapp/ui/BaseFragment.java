package fr.eseo.dis.amiaudluc.spinoffapp.ui;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.EndlessRecyclerViewScrollListener;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.MovieViewModel;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SerieViewModel;

/**
 * Created by lucasamiaud on 19/03/2018.
 */

public abstract class BaseFragment extends Fragment implements SearchInterface {

    public EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    public SwipeRefreshLayout swipeContainer;
    public SerieViewModel serieViewModel;
    public MovieViewModel movieViewModel;
    public View view;
    public RecyclerView recycler;

    protected Integer selectedContextId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.view = inflater.inflate(R.layout.layout_main, container, false);
        this.movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        this.serieViewModel = new ViewModelProvider(requireActivity()).get(SerieViewModel.class);

        this.movieViewModel.initDatabaseMovies();
        this.serieViewModel.initDatabaseSeries();
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
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.action_settings);
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
        // STUB
    }

    public abstract void onRecyclerLoadMore(Integer page);

    @Override
    public void onDetach() {
        this.endlessRecyclerViewScrollListener.resetState();
        super.onDetach();
    }
}
