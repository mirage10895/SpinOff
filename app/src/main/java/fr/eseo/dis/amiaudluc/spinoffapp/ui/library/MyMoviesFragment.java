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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieAdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MoviesAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.MovieViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class MyMoviesFragment extends Fragment implements SearchInterface {

    private MovieViewModel movieViewModel;
    private MoviesAdapter seenMoviesAdapter;
    private MoviesAdapter toSeeMoviesAdapter;
    private Context ctx;
    private List<MovieAdapterData> movies;
    private Integer selectedMovieId;


    public MyMoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        this.movieViewModel.initDatabaseMovies();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_my_medias, container, false);
        ctx = view.getContext();

        View toSeeContainer = view.findViewById(R.id.media_to_see_container);
        View seenContainer = view.findViewById(R.id.media_seen_container);
        TextView title = view.findViewById(R.id.title);
        TextView mediaNumber = view.findViewById(R.id.media_number);
        TextView seenNumber = view.findViewById(R.id.media_seen_number);
        TextView runtime = view.findViewById(R.id.media_runtime);

        title.setText(R.string.library_movies);

        RecyclerView recyclerToSee = view.findViewById(R.id.media_to_see);
        recyclerToSee.setHasFixedSize(true);
        recyclerToSee.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));

        this.toSeeMoviesAdapter = new MoviesAdapter(
                ctx,
                this,
                new ArrayList<>(),
                true
        );
        recyclerToSee.setAdapter(toSeeMoviesAdapter);

        RecyclerView recyclerSeen = view.findViewById(R.id.media_seen);
        recyclerSeen.setHasFixedSize(true);
        recyclerSeen.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));

        this.seenMoviesAdapter = new MoviesAdapter(
                ctx,
                this,
                new ArrayList<>(),
                true
        );
        recyclerSeen.setAdapter(seenMoviesAdapter);

        this.movieViewModel.getDatabaseMovies().observe(
                requireActivity(),
                movieDatabases -> {
                    List<MovieDatabase> seen = movieDatabases.stream()
                            .filter(MovieDatabase::isWatched)
                            .collect(Collectors.toList());

                    List<MovieAdapterData> toSee = movieDatabases.stream()
                            .filter(movieDatabase -> !movieDatabase.isWatched())
                            .map(m -> MovieAdapterData.of(
                                    m.getId(),
                                    m.getPosterPath()
                            ))
                            .collect(Collectors.toList());

                    mediaNumber.setText(String.valueOf(movieDatabases.size()));
                    seenNumber.setText(String.valueOf(seen.size()));
                    int seenRuntime = seen.stream()
                            .mapToInt(MovieDatabase::getRuntime)
                            .sum();
                    runtime.setText(DateUtils.displayDuration(seenRuntime));

                    seenContainer.setVisibility(seen.isEmpty() ? View.GONE : View.VISIBLE);
                    toSeeContainer.setVisibility(toSee.isEmpty() ? View.GONE : View.VISIBLE);

                    this.setMovies(
                            seen
                                    .stream()
                                    .map(m -> MovieAdapterData.of(
                                            m.getId(),
                                            m.getPosterPath()
                                    ))
                                    .collect(Collectors.toList()),
                            toSee
                    );
                }
        );

        return view;
    }

    private void setMovies(
            List<MovieAdapterData> seenMovies,
            List<MovieAdapterData> toSeeMovies
    ) {
        toSeeMoviesAdapter.setMovies(toSeeMovies);
        seenMoviesAdapter.setMovies(seenMovies);
    }

    @Override
    public void onCreateContextMenu(
            @NonNull ContextMenu menu,
            @NonNull View v,
            ContextMenu.ContextMenuInfo menuInfo
    ) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_library, menu);

        boolean hasBeenWatched = this.movieViewModel.getDatabaseMovies().getValue()
                .stream()
                .anyMatch(i -> i.isWatched() && i.getId().equals(this.selectedMovieId));
        if (hasBeenWatched) {
            menu.removeItem(R.id.context_menu_mark_as);
        } else {
            menu.removeItem(R.id.context_menu_mark_as_not);
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
        return true;
    }

    @Override
    public void onItemClick(Integer id) {
        Intent intent = new Intent(getContext(), MovieActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onRegisterContextMenu(View view, Integer id) {
        view.setTag(id);
        registerForContextMenu(view);
    }

    @Override
    public void setType(FragmentType type) {
        // stub
    }
}
