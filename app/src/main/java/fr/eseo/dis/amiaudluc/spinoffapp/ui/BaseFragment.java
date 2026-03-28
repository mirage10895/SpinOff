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
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.LayoutMainBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.EndlessRecyclerViewScrollListener;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.MovieViewModel;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SerieViewModel;

/**
 * Created by lucasamiaud on 19/03/2018.
 */

public abstract class BaseFragment extends Fragment implements ItemInterface {

    protected LayoutMainBinding binding;
    public EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    public SerieViewModel serieViewModel;
    public MovieViewModel movieViewModel;

    protected Integer selectedContextId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        this.serieViewModel = new ViewModelProvider(requireActivity()).get(SerieViewModel.class);

        int columns = getResources().getInteger(R.integer.scripts_columns);
        binding.cardList.setLayoutManager(new GridLayoutManager(requireContext(), columns));

        this.endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) binding.cardList.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                onRecyclerLoadMore(page);
            }
        };

        binding.cardList.addOnScrollListener(this.endlessRecyclerViewScrollListener);
        
        initializeSwipeContainer();
    }

    @Override
    public void onRegisterContextMenu(View view, Integer id) {
        view.setTag(id);
        registerForContextMenu(view);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getTag() instanceof Integer) {
            this.selectedContextId = (Integer) v.getTag();
        }
        if (getActivity() != null) {
            MenuInflater menuInflater = getActivity().getMenuInflater();
            menuInflater.inflate(R.menu.context_menu_main, menu);
        }
    }

    protected void initializeSwipeContainer() {
        binding.swipeContainer.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.white
        );
    }

    public abstract void onRecyclerLoadMore(Integer page);

    @Override
    public void onDestroyView() {
        if (binding != null) {
            binding.cardList.removeOnScrollListener(this.endlessRecyclerViewScrollListener);
        }
        this.endlessRecyclerViewScrollListener.resetState();
        super.onDestroyView();
        binding = null;
    }
}
