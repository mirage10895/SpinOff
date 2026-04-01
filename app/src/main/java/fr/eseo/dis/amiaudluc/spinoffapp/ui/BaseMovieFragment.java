package fr.eseo.dis.amiaudluc.spinoffapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.action.DeleteMovieActionListener;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieAdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MoviesAdapter;

/**
 * Created by lucasamiaud on 04/04/2018.
 */

public abstract class BaseMovieFragment extends BaseFragment {
    protected MoviesAdapter moviesAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.moviesAdapter = new MoviesAdapter(
                requireContext(),
                this,
                new ArrayList<>(),
                false
        );

        binding.cardList.setAdapter(this.moviesAdapter);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.context_menu_add) {
            super.movieViewModel.insert(super.selectedContextId);
            Snackbar.make(binding.getRoot(), R.string.movie_added, Snackbar.LENGTH_LONG)
                    .setAction(
                            R.string.undo_action,
                            new DeleteMovieActionListener(this.movieViewModel, super.selectedContextId)
                    )
                    .show();
            return true;
        } else if (item.getItemId() == R.id.context_menu_delete) {
            super.movieViewModel.deleteMovieById(super.selectedContextId);
            Snackbar.make(binding.getRoot(), R.string.movie_added, Snackbar.LENGTH_LONG)
                    .show();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void observeMovies() {
        this.movieViewModel.getMovies().observe(getViewLifecycleOwner(), movies -> {
            binding.swipeContainer.setRefreshing(false);
            binding.progressBar.setVisibility(View.GONE);
            binding.cardList.setVisibility(View.VISIBLE);
            binding.noMediaDisplay.getRoot().setVisibility(View.GONE);
            if (movies != null) {
                loadMovies(
                        movies.stream()
                                .map(Movie::toAdapterFormat)
                                .collect(Collectors.toList())
                );
            } else {
                binding.cardList.setVisibility(View.GONE);
                binding.noMediaDisplay.getRoot().setVisibility(View.VISIBLE);
                Snackbar.make(binding.getRoot(), R.string.no_results, Snackbar.LENGTH_LONG)
                        .setAction("Refresh", view -> onRecyclerLoadMore(0)).show();
            }
        });
    }

    private void loadMovies(List<MovieAdapterData> movies) {
        this.moviesAdapter.setMovies(movies);
    }

    @Override
    public void onItemClick(Integer id, FragmentType type) {
        Intent intent = new Intent(requireContext(), MovieActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (super.selectedContextId != null) {
            super.movieViewModel.getDatabaseMovies().observe(getViewLifecycleOwner(), movies -> {
                boolean isPresent = movies.stream().map(MovieDatabase::getId).anyMatch(id -> id.equals(super.selectedContextId));
                if (isPresent) {
                    menu.removeItem(R.id.context_menu_add);
                } else {
                    menu.removeItem(R.id.context_menu_delete);
                }
            });
        }
    }
}
