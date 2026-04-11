package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;
import androidx.lifecycle.ViewModelProvider;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ActivityMovieStatsBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.movie.MovieStats;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.movie.MovieViewModel;

public class MovieStatsActivity extends AppCompatActivity {

    private ActivityMovieStatsBinding binding;
    private MovieViewModel movieViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieStatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.appBarMain.topBar.setAlpha(1);
        binding.appBarMain.topBar.setTitle(R.string.stats_movie_title);
        binding.appBarMain.closeButton.setOnClickListener(v -> finish());

        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        movieViewModel.getMovieStats().observe(this, stats -> {
            if (stats != null) {
                updateUI(stats);
            }
        });
    }

    private void updateUI(MovieStats stats) {
        binding.totalRuntimeValue.setText(String.format(Locale.getDefault(), "%,d", Duration.ofMinutes(stats.getTotalRuntime()).toHours()));
        binding.daysEquivalent.setText(getString(R.string.stats_days_equivalent, DateUtils.displayDuration(Duration.ofMinutes(stats.getTotalRuntime()))));
        binding.totalMoviesValue.setText(String.valueOf(stats.getTotalMovies()));

        // Average Year (Keeping existing UI element if still present, or updating it with top year)
        binding.averageYearValue.setText(String.valueOf(stats.getTopYear()));

        // Genres
        binding.topGenreName.setText(stats.getTopGenre());
        binding.genreListContainer.removeAllViews();
        if (stats.getTop3Genres() != null) {
            for (Map.Entry<String, Integer> entry : stats.getTop3Genres()) {
                TextView textView = new TextView(this);
                TextViewCompat.setTextAppearance(textView, R.style.BentoGenreText);
                int percent = stats.getTotalMovies() > 0 ? (entry.getValue() * 100 / stats.getTotalMovies()) : 0;
                textView.setText(String.format(Locale.getDefault(), "%s: %d (%d%%)", entry.getKey(), entry.getValue(), percent));
                binding.genreListContainer.addView(textView);
            }
        }

        // Combination
        binding.topCombinationValue.setText(stats.getTopCombination());

        // Years
        binding.topYearValue.setText(String.valueOf(stats.getTopYear()));
        binding.yearsListContainer.removeAllViews();
        if (stats.getTop3Years() != null) {
            for (Map.Entry<Integer, Integer> entry : stats.getTop3Years()) {
                TextView textView = new TextView(this);
                TextViewCompat.setTextAppearance(textView, R.style.BentoGenreText);
                int percent = stats.getTotalMovies() > 0 ? (entry.getValue() * 100 / stats.getTotalMovies()) : 0;
                textView.setText(String.format(Locale.getDefault(), "%d: %d movies (%d%%)", entry.getKey(), entry.getValue(), percent));
                binding.yearsListContainer.addView(textView);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
