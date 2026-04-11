package fr.eseo.dis.amiaudluc.spinoffapp.ui.episode;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ActivitySeasonBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.SerieViewModel;

/**
 * Created by lucasamiaud on 19/03/2018.
 */

public class EpisodeActivity extends AppCompatActivity {

    private ActivitySeasonBinding binding;
    private SerieViewModel serieViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeasonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.contentMedia.content.setVisibility(View.GONE);

        serieViewModel = new ViewModelProvider(this).get(SerieViewModel.class);

        binding.appBarMain.closeButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        int episodeNumber = getIntent().getIntExtra("episodeNumber", 0);
        int seasonNumber = getIntent().getIntExtra("seasonNumber", 0);
        int serieId = getIntent().getIntExtra("serieId", 0);

        if (savedInstanceState == null) {
            serieViewModel.initGetEpisodeBySeasonNumberBySerieId(serieId, seasonNumber, episodeNumber);
            
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.content, EpisodeFragment.newInstance(serieId, seasonNumber, episodeNumber), "EpisodeFragment")
                    .commit();
        }

        setupObservers();
    }

    private void setupObservers() {
        serieViewModel.getEpisode().observe(this, episode -> {
            if (episode != null) {
                binding.contentMedia.noMediaDisplay.getRoot().setVisibility(View.GONE);
                binding.contentMedia.content.setVisibility(View.VISIBLE);
                binding.appBarMain.topBar.setTitle(episode.getName());
                binding.appBarMain.topBar.setAlpha(1);
            } else {
                binding.contentMedia.noMediaDisplay.getRoot().setVisibility(View.VISIBLE);
                Snackbar.make(binding.getRoot(), R.string.no_results, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
