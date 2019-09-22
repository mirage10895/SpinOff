package fr.eseo.dis.amiaudluc.spinoffapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.repository.ApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MoviesAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.view_model.MovieViewModel;

/**
 * Created by lucasamiaud on 04/04/2018.
 */

public abstract class BaseMovieFragment extends BaseFragment {

    public MovieViewModel movieViewModel;
    protected MoviesAdapter moviesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.movieViewModel = new MovieViewModel(ApiRepository.getInstance());
        this.moviesAdapter = new MoviesAdapter(this.getContext(),this, new ArrayList<>());

        super.recycler.setAdapter(this.moviesAdapter);

        return super.view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.view = view;
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.context_menu_add) {

        }
        return false;
    }

    public void observeMovies() {
        this.movieViewModel.getMovies().observe(this, movies -> {
            swipeContainer.setRefreshing(false);
            this.view.findViewById(R.id.progressBar).setVisibility(View.GONE);
            this.view.findViewById(R.id.cardList).setVisibility(View.VISIBLE);
            this.view.findViewById(R.id.no_media_display).setVisibility(View.GONE);
            if (movies != null) {
                loadMovies(movies.stream().map(Movie::toDatabaseFormat).collect(Collectors.toList()));
            } else {
                this.view.findViewById(R.id.cardList).setVisibility(View.GONE);
                this.view.findViewById(R.id.no_media_display).setVisibility(View.VISIBLE);
                Snackbar.make(this.view, R.string.no_results, Snackbar.LENGTH_LONG)
                        .setAction("Refresh", view -> onRecyclerLoadMore(0)).show();
            }
        });
    }

    private void loadMovies(List<MovieDatabase> movies) {
        this.moviesAdapter.setMovies(movies);
        this.moviesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Integer id) {
        Intent intent = new Intent(getContext(), MovieActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer selectedContextId) {
        onCreateContextMenu(contextMenu, v, menuInfo);
        super.selectedContextId = selectedContextId;
    }
}
