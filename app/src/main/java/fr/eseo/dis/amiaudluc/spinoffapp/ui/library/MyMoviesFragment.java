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
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieAdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MoviesAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.MovieViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class MyMoviesFragment extends Fragment implements SearchInterface {

    private MovieViewModel movieViewModel;
    private MoviesAdapter moviesAdapter;
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

        this.movieViewModel.getDatabaseMovies().observe(
                requireActivity(),
                movieDatabases -> this.setDbMovies(
                        movieDatabases.stream()
                                .map(m -> MovieAdapterData.of(
                                        m.getId(),
                                        m.getPosterPath()
                                ))
                                .collect(Collectors.toList())
                )
        );

        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        RecyclerView recycler = view.findViewById(R.id.cardList);
        recycler.setHasFixedSize(true);
        int columns = getResources().getInteger(R.integer.scripts_columns);
        recycler.setLayoutManager(new GridLayoutManager(ctx, columns));

        this.moviesAdapter = new MoviesAdapter(
                ctx,
                this,
                new ArrayList<>(),
                false
        );
        recycler.setAdapter(moviesAdapter);

        return view;
    }

    private void setDbMovies(List<MovieAdapterData> movies) {
        this.movies = movies;
        if (moviesAdapter != null) {
            moviesAdapter.setMovies(this.movies);
            moviesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_library, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.context_menu_delete) {
            this.movieViewModel.deleteMovieById(this.selectedMovieId);
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(Integer id) {
        Intent intent = new Intent(getContext(), MovieActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer movieId) {
        this.selectedMovieId = movieId;
        onCreateContextMenu(contextMenu, v, menuInfo);
    }

    @Override
    public void setType(FragmentType type) {
        // stub
    }
}
