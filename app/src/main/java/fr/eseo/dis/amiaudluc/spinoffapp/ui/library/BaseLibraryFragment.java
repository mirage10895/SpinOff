package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.FragmentMyMediasBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library.LibraryFilter;

public abstract class BaseLibraryFragment extends Fragment implements ItemInterface {

    protected FragmentMyMediasBinding binding;
    protected Integer selectedId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyMediasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnSeeWatchlist.setOnClickListener(v ->
                openLibraryList(R.string.library_to_see, LibraryFilter.WATCHLIST));

        binding.btnSeeWatched.setOnClickListener(v ->
                openLibraryList(R.string.library_media_seen_number, LibraryFilter.WATCHED));

        setupViewModel();
    }

    /**
     * Opens the A-Z list of the library, restricted to the media type of the screen and to the
     * given list.
     */
    private void openLibraryList(int titleRes, LibraryFilter filter) {
        Intent intent = new Intent(requireContext(), LibraryListActivity.class);
        intent.putExtra(LibraryListActivity.EXTRA_TITLE, getString(titleRes));
        intent.putExtra(LibraryListActivity.EXTRA_TYPE, getFragmentType());
        intent.putExtra(LibraryListActivity.EXTRA_FILTER, filter);
        startActivity(intent);
    }

    protected abstract FragmentType getFragmentType();
    protected abstract void setupViewModel();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRegisterContextMenu(View view, Integer id) {
        view.setTag(id);
        registerForContextMenu(view);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = requireActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_library, menu);

        if (v.getTag() instanceof Integer) {
            this.selectedId = (Integer) v.getTag();
        }

        updateContextMenu(menu);
    }

    protected abstract void updateContextMenu(ContextMenu menu);

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (isResumed()) {
            if (item.getItemId() == R.id.context_menu_delete) {
                deleteMedia(selectedId);
                return true;
            }
            if (item.getItemId() == R.id.context_menu_mark_as) {
                setWatched(selectedId, true);
                return true;
            }
            if (item.getItemId() == R.id.context_menu_mark_as_not) {
                setWatched(selectedId, false);
                return true;
            }
        }
        return super.onContextItemSelected(item);
    }

    protected abstract void deleteMedia(Integer id);

    /**
     * Explicitly sets the watched state instead of toggling it, so "Mark as seen" and
     * "Mark as unseen" always do what they say, whatever the current state is.
     */
    protected abstract void setWatched(Integer id, boolean watched);
}
