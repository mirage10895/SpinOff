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
import androidx.recyclerview.widget.GridLayoutManager;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.LayoutMainBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.GridSpacingItemDecoration;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.OnScrollLoadMoreListener;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ScrollBehaviorHandler;

/**
 * Created by lucasamiaud on 19/03/2018.
 */

public abstract class DiscoveryBaseFragment extends Fragment implements ItemInterface, OnScrollLoadMoreListener {

    protected LayoutMainBinding binding;
    protected ScrollBehaviorHandler scrollBehaviorHandler;

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

        int columns = getResources().getInteger(R.integer.scripts_columns);
        binding.cardList.setLayoutManager(new GridLayoutManager(requireContext(), columns));

        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        binding.cardList.addItemDecoration(new GridSpacingItemDecoration(columns, spacing));

        this.scrollBehaviorHandler = new ScrollBehaviorHandler(binding.cardList, this);
        this.scrollBehaviorHandler.setup();

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
                R.color.colorPrimary,
                R.color.colorAccent,
                R.color.colorSecondary,
                R.color.white
        );
    }

    public abstract void onRecyclerLoadMore(int page);

    public void resetScroll() {
        if (binding != null) {
            binding.cardList.scrollToPosition(0);
        }
        if (scrollBehaviorHandler != null) {
            scrollBehaviorHandler.resetState();
        }
    }

    @Override
    public void onDestroyView() {
        if (scrollBehaviorHandler != null) {
            scrollBehaviorHandler.detach();
        }
        super.onDestroyView();
        binding = null;
    }
}
