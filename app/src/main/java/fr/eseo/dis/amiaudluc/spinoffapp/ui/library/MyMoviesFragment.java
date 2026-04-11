package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import androidx.lifecycle.ViewModelProvider;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.AdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.movie.MovieViewModel;

public class MyMoviesFragment extends BaseLibraryFragment {

    private MovieViewModel movieViewModel;

    public MyMoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
    }

    @Override
    protected int getTitleResId() {
        return R.string.library_movies;
    }

    @Override
    protected FragmentType getFragmentType() {
        return FragmentType.MOVIE;
    }

    @Override
    protected void setupViewModel() {
        this.movieViewModel.getDatabaseMovies().observe(
                getViewLifecycleOwner(),
                movieDatabases -> {
                    List<MovieDatabase> seenRaw = movieDatabases.stream()
                            .filter(MovieDatabase::isWatched)
                            .collect(Collectors.toList());

                    List<AdapterData> seen = seenRaw.stream()
                            .map(m -> new AdapterData(
                                    m.getId(),
                                    m.getTitle(),
                                    m.getPosterPath(),
                                    FragmentType.MOVIE
                            ))
                            .collect(Collectors.toList());

                    List<AdapterData> toSee = movieDatabases.stream()
                            .filter(movieDatabase -> !movieDatabase.isWatched())
                            .map(m -> new AdapterData(
                                    m.getId(),
                                    m.getTitle(),
                                    m.getPosterPath(),
                                    FragmentType.MOVIE
                            ))
                            .collect(Collectors.toList());

                    int seenRuntime = seenRaw.stream()
                            .mapToInt(MovieDatabase::getRuntime)
                            .sum();

                    updateUI(seen, toSee, DateUtils.displayDuration(Duration.ofMinutes(seenRuntime)));
                }
        );
    }

    @Override
    protected void updateContextMenu(ContextMenu menu) {
        List<MovieDatabase> movies = this.movieViewModel.getDatabaseMovies().getValue();
        if (movies != null) {
            boolean hasBeenWatched = movies.stream()
                    .anyMatch(i -> i.isWatched() && i.getId().equals(this.selectedId));
            if (hasBeenWatched) {
                menu.removeItem(R.id.context_menu_mark_as);
            } else {
                menu.removeItem(R.id.context_menu_mark_as_not);
            }
        }
    }

    @Override
    protected void deleteMedia(Integer id) {
        this.movieViewModel.deleteMovieById(id);
    }

    @Override
    protected void toggleWatched(Integer id) {
        this.movieViewModel.toggleMovieIsWatched(id);
    }

    @Override
    protected void onSeeStatsClick() {
        Intent intent = new Intent(getContext(), MovieStatsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(Integer id, FragmentType type) {
        Intent intent = new Intent(getContext(), MovieActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
