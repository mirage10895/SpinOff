package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ActivityMediaListBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter.AdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.MediaAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.movie.MovieViewModel;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.SerieViewModel;

public class MediaListActivity extends AppCompatActivity implements ItemInterface {

    public static final String EXTRA_SHOW_WATCHED = "extra_show_watched";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_TYPE = "extra_type";

    private MovieViewModel movieViewModel;
    private SerieViewModel serieViewModel;
    private MediaAdapter adapter;
    private Integer selectedMediaId;
    private boolean showWatched;
    private FragmentType type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMediaListBinding binding = ActivityMediaListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showWatched = getIntent().getBooleanExtra(EXTRA_SHOW_WATCHED, false);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        type = (FragmentType) getIntent().getSerializableExtra(EXTRA_TYPE);

        binding.appBarMain.topBar.setTitle(title);
        binding.appBarMain.topBar.setAlpha(1);
        binding.appBarMain.closeButton.setOnClickListener(v -> finish());

        adapter = new MediaAdapter(this, this, new ArrayList<>(), false);
        binding.mediaList.setLayoutManager(new GridLayoutManager(this, 3));
        binding.mediaList.setAdapter(adapter);

        if (type == FragmentType.MOVIE) {
            movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
            movieViewModel.getDatabaseMovies().observe(this, movies -> {
                List<AdapterData> filteredMovies = movies.stream()
                        .filter(m -> m.isWatched() == showWatched)
                        .map(m -> new AdapterData(
                                m.getId(),
                                m.getTitle(),
                                m.getPosterPath(),
                                FragmentType.MOVIE
                        ))
                        .collect(Collectors.toList());
                adapter.submitList(filteredMovies);
            });
        } else {
            serieViewModel = new ViewModelProvider(this).get(SerieViewModel.class);
            serieViewModel.getDatabaseSeries().observe(this, series -> {
                List<AdapterData> filteredSeries = series.stream()
                        .filter(m -> m.isWatched() == showWatched)
                        .map(m -> new AdapterData(
                                m.getId(),
                                m.getName(),
                                m.getPosterPath(),
                                FragmentType.SERIE
                        ))
                        .collect(Collectors.toList());
                adapter.submitList(filteredSeries);
            });
        }
    }

    @Override
    public void onItemClick(Integer id, FragmentType type) {
        Intent intent;
        if (type == FragmentType.MOVIE) {
            intent = new Intent(this, MovieActivity.class);
        } else {
            intent = new Intent(this, SerieActivity.class);
        }
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onRegisterContextMenu(View view, Integer id) {
        view.setTag(id);
        registerForContextMenu(view);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_library, menu);

        if (v.getTag() instanceof Integer) {
            this.selectedMediaId = (Integer) v.getTag();
        }

        if (type == FragmentType.MOVIE) {
            List<MovieDatabase> movies = this.movieViewModel.getDatabaseMovies().getValue();
            if (movies != null) {
                boolean hasBeenWatched = movies.stream()
                        .anyMatch(i -> i.isWatched() && i.getId().equals(this.selectedMediaId));
                if (hasBeenWatched) {
                    menu.removeItem(R.id.context_menu_mark_as);
                } else {
                    menu.removeItem(R.id.context_menu_mark_as_not);
                }
            }
        } else {
            List<SerieDatabase> series = this.serieViewModel.getDatabaseSeries().getValue();
            if (series != null) {
                boolean hasBeenWatched = series.stream()
                        .anyMatch(i -> i.isWatched() && i.getId().equals(this.selectedMediaId));
                if (hasBeenWatched) {
                    menu.removeItem(R.id.context_menu_mark_as);
                } else {
                    menu.removeItem(R.id.context_menu_mark_as_not);
                }
            }
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.context_menu_delete) {
            if (type == FragmentType.MOVIE) {
                this.movieViewModel.deleteMovieById(this.selectedMediaId);
            } else {
                this.serieViewModel.deleteById(this.selectedMediaId);
            }
            return true;
        }
        if (item.getItemId() == R.id.context_menu_mark_as || item.getItemId() == R.id.context_menu_mark_as_not) {
            if (type == FragmentType.MOVIE) {
                this.movieViewModel.toggleMovieIsWatched(this.selectedMediaId);
            } else {
                this.serieViewModel.toggleSerieIsWatched(this.selectedMediaId);
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }
}
