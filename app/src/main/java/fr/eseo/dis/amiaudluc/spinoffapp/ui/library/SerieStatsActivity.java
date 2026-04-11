package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.os.Bundle;
import android.view.MenuItem;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ActivitySerieStatsBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.SerieStats;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.SerieViewModel;

public class SerieStatsActivity extends AppCompatActivity {

    private ActivitySerieStatsBinding binding;
    private SerieViewModel serieViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySerieStatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.appBarMain.topBar.setAlpha(1);
        binding.appBarMain.topBar.setTitle(R.string.stats_serie_title);
        binding.appBarMain.closeButton.setOnClickListener(v -> finish());

        serieViewModel = new ViewModelProvider(this).get(SerieViewModel.class);
        serieViewModel.getSerieStats().observe(this, stats -> {
            if (stats != null) {
                updateUI(stats);
            }
        });
    }

    private void updateUI(SerieStats stats) {
        binding.totalRuntimeValue.setText(String.format(Locale.getDefault(), "%,d", Duration.ofMinutes(stats.getTotalMinutes()).toHours()));
        binding.daysEquivalent.setText(getString(R.string.stats_days_equivalent, DateUtils.displayDuration(Duration.ofMinutes(stats.getTotalMinutes()))));
        binding.totalSeriesValue.setText(String.valueOf(stats.getTotalSeries()));
        binding.totalEpisodesValue.setText(String.format(Locale.getDefault(), "%,d", stats.getTotalEpisodes()));

        // Genres
        binding.topGenreName.setText(stats.getTopGenre());
        binding.genreListContainer.removeAllViews();
        if (stats.getTop3Genres() != null) {
            for (Map.Entry<String, Integer> entry : stats.getTop3Genres()) {
                android.widget.TextView textView = new android.widget.TextView(this);
                androidx.core.widget.TextViewCompat.setTextAppearance(textView, R.style.BentoGenreText);
                int percent = stats.getTotalSeries() > 0 ? (entry.getValue() * 100 / stats.getTotalSeries()) : 0;
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
                android.widget.TextView textView = new android.widget.TextView(this);
                androidx.core.widget.TextViewCompat.setTextAppearance(textView, R.style.BentoGenreText);
                int percent = stats.getTotalSeries() > 0 ? (entry.getValue() * 100 / stats.getTotalSeries()) : 0;
                textView.setText(String.format(Locale.getDefault(), "%d: %d series (%d%%)", entry.getKey(), entry.getValue(), percent));
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
