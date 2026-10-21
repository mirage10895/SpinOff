package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ActivityLibraryListBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.LibraryTileListener;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter.LibraryAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter.LibraryRow;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library.LibraryFilter;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library.LibraryItem;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library.LibraryListViewModel;

/**
 * Lists the media of the library as a single column of tiles split into A-Z sections.
 * <p>
 * Opened from the "seen" and "watchlist" cards of the movie and series library screens, for one
 * media type and one {@link LibraryFilter} at a time. The letter dividers are purely visual, and
 * quick browsing relies on the native {@code RecyclerView} fast scroller. Tiles only display
 * information the local library holds, and their actions live behind the overflow button rather
 * than a long press menu.
 */
public class LibraryListActivity extends AppCompatActivity implements LibraryTileListener {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_TYPE = "extra_type";
    public static final String EXTRA_FILTER = "extra_filter";

    private ActivityLibraryListBinding binding;
    private LibraryAdapter adapter;
    private LibraryListViewModel viewModel;

    private FragmentType type = FragmentType.MOVIE;
    private LibraryFilter filter = LibraryFilter.WATCHED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLibraryListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FragmentType requestedType = (FragmentType) getIntent().getSerializableExtra(EXTRA_TYPE);
        if (requestedType != null) {
            this.type = requestedType;
        }
        LibraryFilter requestedFilter = (LibraryFilter) getIntent().getSerializableExtra(EXTRA_FILTER);
        if (requestedFilter != null) {
            this.filter = requestedFilter;
        }

        binding.appBarMain.topBar.setTitle(getIntent().getStringExtra(EXTRA_TITLE));
        binding.appBarMain.topBar.setAlpha(1);
        binding.appBarMain.closeButton.setOnClickListener(view -> finish());

        bindEmptyState();

        adapter = new LibraryAdapter(this, this);
        binding.libraryList.setLayoutManager(new LinearLayoutManager(this));
        binding.libraryList.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(LibraryListViewModel.class);
        viewModel.items(type, filter).observe(this, this::submit);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void bindEmptyState() {
        boolean watchlist = filter == LibraryFilter.WATCHLIST;
        binding.libraryEmpty.tvListNothing.setText(watchlist
                ? R.string.library_list_empty_watchlist_title
                : R.string.library_list_empty_watched_title);
        binding.libraryEmpty.tvListNothing2.setText(watchlist
                ? R.string.library_list_empty_watchlist_desc
                : R.string.library_list_empty_watched_desc);
    }

    private void submit(List<LibraryItem> media) {
        List<LibraryRow> rows = LibraryRow.group(media != null ? media : List.of());
        adapter.submitList(rows);
        binding.libraryEmpty.getRoot().setVisibility(rows.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onTileClick(LibraryItem item) {
        Intent intent = item.fragmentType() == FragmentType.MOVIE
                ? new Intent(this, MovieActivity.class)
                : new Intent(this, SerieActivity.class);
        intent.putExtra("id", item.id());
        startActivity(intent);
    }

    @Override
    public void onTileMenu(View anchor, LibraryItem item) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.inflate(R.menu.context_menu_library);

        // Only offer the transition that actually changes something.
        if (item.fullyWatched()) {
            popup.getMenu().removeItem(R.id.context_menu_mark_as);
        }
        if (!item.hasWatchedContent()) {
            popup.getMenu().removeItem(R.id.context_menu_mark_as_not);
        }

        popup.setOnMenuItemClickListener(menuItem -> onTileMenuAction(menuItem, item));
        popup.show();
    }

    private boolean onTileMenuAction(MenuItem menuItem, LibraryItem item) {
        if (menuItem.getItemId() == R.id.context_menu_delete) {
            viewModel.delete(item.id(), item.fragmentType());
            return true;
        }
        if (menuItem.getItemId() == R.id.context_menu_mark_as
                || menuItem.getItemId() == R.id.context_menu_mark_as_not) {
            viewModel.setWatched(item.id(), item.fragmentType(),
                    menuItem.getItemId() == R.id.context_menu_mark_as);
            return true;
        }
        return false;
    }
}
