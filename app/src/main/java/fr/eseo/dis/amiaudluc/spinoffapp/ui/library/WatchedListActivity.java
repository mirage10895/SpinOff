package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ActivityWatchedListBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.WatchedTileListener;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter.WatchedMediaAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter.WatchedRow;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library.WatchedListViewModel;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library.WatchedMediaItem;

/**
 * Shows what the user has watched, as a single column of tiles split into A-Z sections.
 * <p>
 * Opened from the "watched" card of the movie and series library screens, one media type at a
 * time. The letter dividers are purely visual, and quick browsing relies on the native
 * {@code RecyclerView} fast scroller. Tiles only display information the local library holds,
 * and their actions live behind the overflow button rather than a long press menu.
 */
public class WatchedListActivity extends AppCompatActivity implements WatchedTileListener {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_TYPE = "extra_type";

    private ActivityWatchedListBinding binding;
    private WatchedMediaAdapter adapter;
    private WatchedListViewModel viewModel;

    private FragmentType type = FragmentType.MOVIE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWatchedListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FragmentType requestedType = (FragmentType) getIntent().getSerializableExtra(EXTRA_TYPE);
        if (requestedType != null) {
            this.type = requestedType;
        }

        binding.appBarMain.topBar.setTitle(getIntent().getStringExtra(EXTRA_TITLE));
        binding.appBarMain.topBar.setAlpha(1);
        binding.appBarMain.closeButton.setOnClickListener(view -> finish());

        binding.watchedEmpty.tvListNothing.setText(R.string.watched_list_empty_title);
        binding.watchedEmpty.tvListNothing2.setText(R.string.watched_list_empty_desc);

        adapter = new WatchedMediaAdapter(this, this);
        binding.watchedList.setLayoutManager(new LinearLayoutManager(this));
        binding.watchedList.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(WatchedListViewModel.class);
        itemsSource().observe(this, this::submit);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private LiveData<List<WatchedMediaItem>> itemsSource() {
        return type == FragmentType.MOVIE ? viewModel.getMovies() : viewModel.getSeries();
    }

    private void submit(List<WatchedMediaItem> media) {
        List<WatchedRow> rows = WatchedRow.group(media != null ? media : List.of());
        adapter.submitList(rows);
        binding.watchedEmpty.getRoot().setVisibility(rows.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onTileClick(WatchedMediaItem item) {
        Intent intent = item.fragmentType() == FragmentType.MOVIE
                ? new Intent(this, MovieActivity.class)
                : new Intent(this, SerieActivity.class);
        intent.putExtra("id", item.id());
        startActivity(intent);
    }

    @Override
    public void onTileMenu(View anchor, WatchedMediaItem item) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.inflate(R.menu.context_menu_library);

        // Everything in this list is watched already, so "Mark as seen" is pointless.
        if (item.fragmentType() == FragmentType.MOVIE || item.fullyWatched()) {
            popup.getMenu().removeItem(R.id.context_menu_mark_as);
        }

        popup.setOnMenuItemClickListener(menuItem -> onTileMenuAction(menuItem, item));
        popup.show();
    }

    private boolean onTileMenuAction(MenuItem menuItem, WatchedMediaItem item) {
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
