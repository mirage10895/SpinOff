package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.FragmentMyMediasBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.AdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.MediaAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.MovieViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class MyMoviesFragment extends Fragment implements ItemInterface {

    private FragmentMyMediasBinding binding;
    private MovieViewModel movieViewModel;
    private MediaAdapter seenMoviesAdapter;
    private MediaAdapter toSeeMoviesAdapter;
    private Integer selectedMovieId;


    public MyMoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentMyMediasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context ctx = view.getContext();

        binding.title.setText(R.string.library_movies);

        binding.mediaToSee.setHasFixedSize(true);
        binding.mediaToSee.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));

        this.toSeeMoviesAdapter = new MediaAdapter(
                ctx,
                this,
                new ArrayList<>(),
                true
        );
        binding.mediaToSee.setAdapter(toSeeMoviesAdapter);

        binding.mediaSeen.setHasFixedSize(true);
        binding.mediaSeen.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));

        this.seenMoviesAdapter = new MediaAdapter(
                ctx,
                this,
                new ArrayList<>(),
                true
        );
        binding.mediaSeen.setAdapter(seenMoviesAdapter);

        this.movieViewModel.getDatabaseMovies().observe(
                getViewLifecycleOwner(),
                movieDatabases -> {
                    List<MovieDatabase> seen = movieDatabases.stream()
                            .filter(MovieDatabase::isWatched)
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

                    binding.mediaNumber.setText(String.valueOf(movieDatabases.size()));
                    binding.mediaSeenNumber.setText(String.valueOf(seen.size()));
                    int seenRuntime = seen.stream()
                            .mapToInt(MovieDatabase::getRuntime)
                            .sum();
                    binding.mediaRuntime.setText(DateUtils.displayDuration(seenRuntime));

                    binding.mediaSeenLayer.setVisibility(seen.isEmpty() ? View.GONE : View.VISIBLE);
                    binding.mediaToSeeLayer.setVisibility(toSee.isEmpty() ? View.GONE : View.VISIBLE);

                    this.setMovies(
                            seen
                                    .stream()
                                    .map(m -> new AdapterData(
                                            m.getId(),
                                            m.getTitle(),
                                            m.getPosterPath(),
                                            FragmentType.MOVIE
                                    ))
                                    .collect(Collectors.toList()),
                            toSee
                    );
                }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setMovies(
            List<AdapterData> seenMovies,
            List<AdapterData> toSeeMovies
    ) {
        toSeeMoviesAdapter.setMedias(toSeeMovies);
        seenMoviesAdapter.setMedias(seenMovies);
    }

    @Override
    public void onCreateContextMenu(
            @NonNull ContextMenu menu,
            @NonNull View v,
            ContextMenu.ContextMenuInfo menuInfo
    ) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = requireActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_library, menu);

        if (v.getTag() instanceof Integer) {
            this.selectedMovieId = (Integer) v.getTag();
        }

        List<MovieDatabase> movies = this.movieViewModel.getDatabaseMovies().getValue();
        if (movies != null) {
            boolean hasBeenWatched = movies.stream()
                    .anyMatch(i -> i.isWatched() && i.getId().equals(this.selectedMovieId));
            if (hasBeenWatched) {
                menu.removeItem(R.id.context_menu_mark_as);
            } else {
                menu.removeItem(R.id.context_menu_mark_as_not);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.context_menu_delete) {
            this.movieViewModel.deleteMovieById(this.selectedMovieId);
            return true;
        }
        if (
                item.getItemId() == R.id.context_menu_mark_as
                || item.getItemId() == R.id.context_menu_mark_as_not
        ) {
            this.movieViewModel.toggleMovieIsWatched(this.selectedMovieId);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onItemClick(Integer id, FragmentType type) {
        Intent intent = new Intent(getContext(), MovieActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onRegisterContextMenu(View view, Integer id) {
        view.setTag(id);
        registerForContextMenu(view);
    }
}
