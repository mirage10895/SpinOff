package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;

import java.time.Duration;
import java.util.Locale;

import androidx.lifecycle.ViewModelProvider;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.chart.MultiSegmentProgressView;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.movie.MovieStats;
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
    protected FragmentType getFragmentType() {
        return FragmentType.MOVIE;
    }

    @Override
    protected void setupViewModel() {
        this.movieViewModel.getMovieStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                updateUI(stats);
            }
        });
    }

    private void updateUI(MovieStats stats) {
        binding.totalRuntimeValue.setText(String.format(Locale.getDefault(), "%,d", Duration.ofMinutes(stats.getTotalRuntime()).toHours()));
        binding.daysEquivalent.setText(getString(R.string.stats_days_equivalent, DateUtils.displayDuration(Duration.ofMinutes(stats.getTotalRuntime()))));
        
        binding.metric1Value.setText(String.valueOf(stats.getTotalMovies()));
        binding.metric1Label.setText(R.string.stats_movies_watched);
        binding.metric1Icon.setImageResource(R.drawable.ic_movie);

        binding.watchlistValue.setText(String.valueOf(stats.getWatchlistCount()));

        // Remove Avg Release Year as it's a duplicate of the one below
        binding.metric3Card.setVisibility(View.GONE);

        // Genres
        binding.topGenreName.setText(stats.getTopGenre());
        binding.genreListContainer.removeAllViews();
        if (stats.getTopGenres() != null) {
            MultiSegmentProgressView.builder()
                    .withColors()
                    .withData(stats.getTopGenres())
                    .into(binding.genreRepartitionChart)
                    .withLegendInto(binding.genreListContainer);
        }

        // Combination
        binding.topCombinationValue.setText(stats.getTopCombination());

        // Years
        binding.topYearValue.setText(stats.getTopYear());
        binding.yearsListContainer.removeAllViews();
        if (stats.getTopYears() != null) {
            MultiSegmentProgressView.builder()
                    .withColors()
                    .withData(stats.getTopYears())
                    .into(binding.yearRepartitionChart)
                    .withLegendInto(binding.yearsListContainer);
        }
    }

    @Override
    protected void updateContextMenu(ContextMenu menu) {
        // Context menu was for items in list, which are gone now.
        // But keeping the method for compatibility if needed.
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
    public void onItemClick(Integer id, FragmentType type) {
        Intent intent = new Intent(getContext(), MovieActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
