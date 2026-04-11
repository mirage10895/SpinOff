package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.widget.TextView;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;

import androidx.core.widget.TextViewCompat;
import androidx.lifecycle.ViewModelProvider;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.SerieStats;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.SerieViewModel;

public class MySeriesFragment extends BaseLibraryFragment {

    private SerieViewModel serieViewModel;

    public MySeriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.serieViewModel = new ViewModelProvider(requireActivity()).get(SerieViewModel.class);
    }

    @Override
    protected FragmentType getFragmentType() {
        return FragmentType.SERIE;
    }

    @Override
    protected void setupViewModel() {
        this.serieViewModel.getSerieStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                updateUI(stats);
            }
        });
    }

    private void updateUI(SerieStats stats) {
        binding.totalRuntimeValue.setText(String.format(Locale.getDefault(), "%,d", Duration.ofMinutes(stats.getTotalMinutes()).toHours()));
        binding.daysEquivalent.setText(getString(R.string.stats_days_equivalent, DateUtils.displayDuration(Duration.ofMinutes(stats.getTotalMinutes()))));

        binding.metric1Value.setText(String.valueOf(stats.getTotalSeries()));
        binding.metric1Label.setText(R.string.stats_series_watched);
        binding.metric1Icon.setImageResource(R.drawable.ic_tv);

        binding.watchlistValue.setText(String.valueOf(stats.getWatchlistCount()));

        binding.metric3Value.setText(String.valueOf(stats.getTotalEpisodes()));
        binding.metric3Label.setText(R.string.stats_episodes_watched);
        binding.metric3Icon.setImageResource(R.drawable.ic_movie);

        // Genres
        binding.topGenreName.setText(stats.getTopGenre());
        binding.genreListContainer.removeAllViews();
        if (stats.getTop3Genres() != null) {
            for (Map.Entry<String, Integer> entry : stats.getTop3Genres()) {
                TextView textView = new TextView(getContext());
                TextViewCompat.setTextAppearance(textView, R.style.BentoGenreText);
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
                TextView textView = new TextView(getContext());
                TextViewCompat.setTextAppearance(textView, R.style.BentoGenreText);
                int percent = stats.getTotalSeries() > 0 ? (entry.getValue() * 100 / stats.getTotalSeries()) : 0;
                textView.setText(String.format(Locale.getDefault(), "%d: %d series (%d%%)", entry.getKey(), entry.getValue(), percent));
                binding.yearsListContainer.addView(textView);
            }
        }
    }

    @Override
    protected void updateContextMenu(ContextMenu menu) {
    }

    @Override
    protected void deleteMedia(Integer id) {
        this.serieViewModel.deleteById(id);
    }

    @Override
    protected void toggleWatched(Integer id) {
        this.serieViewModel.toggleSerieIsWatched(id);
    }

    @Override
    public void onItemClick(Integer id, FragmentType type) {
        Intent intent = new Intent(getContext(), SerieActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
