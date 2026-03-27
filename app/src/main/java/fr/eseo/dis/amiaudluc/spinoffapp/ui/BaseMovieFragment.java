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
import fr.eseo.dis.amiaudluc.spinoffapp.ui.action.MediaTransactionObserver;
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
            this.movieViewModel.initGetMovieById(super.selectedContextId);
            this.movieViewModel.getMovie()
                    .observe(getViewLifecycleOwner(), new MediaTransactionObserver<>(
                            super.movieViewModel, super.serieViewModel, binding.getRoot(), false
                    ));
        } else if (item.getItemId() == R.id.context_menu_delete) {
            this.movieViewModel.initGetMovieById(super.selectedContextId);
            this.movieViewModel.getMovie()
                    .observe(getViewLifecycleOwner(), new MediaTransactionObserver<>(
                            super.movieViewModel, super.serieViewModel, binding.getRoot(), true
                    ));
        }
        return false;
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
        this.moviesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Integer id) {
        Intent intent = new Intent(requireContext(), MovieActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer selectedContextId) {
        super.onCreateContextMenu(contextMenu, v, menuInfo);
        super.selectedContextId = selectedContextId;
        super.movieViewModel.getDatabaseMovies().observe(getViewLifecycleOwner(), movies -> {
            boolean isPresent = movies.stream().map(MovieDatabase::getId).anyMatch(id -> id.equals(selectedContextId));
            if (isPresent) {
                contextMenu.removeItem(R.id.context_menu_add);
            } else {
                contextMenu.removeItem(R.id.context_menu_delete);
            }
        });
    }
}
