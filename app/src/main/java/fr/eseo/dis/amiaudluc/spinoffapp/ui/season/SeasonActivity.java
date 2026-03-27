package fr.eseo.dis.amiaudluc.spinoffapp.ui.season;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ActivitySeasonBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SerieViewModel;

public class SeasonActivity extends AppCompatActivity {

    private ActivitySeasonBinding binding;
    private SerieViewModel serieViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeasonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.emptyField);
        }

        binding.contentMedia.content.setVisibility(View.GONE);

        serieViewModel = new ViewModelProvider(this).get(SerieViewModel.class);

        int seasonNumber = getIntent().getIntExtra("seasonNumber", 0);
        int serieId = getIntent().getIntExtra("serieId", 0);

        if (savedInstanceState == null) {
            serieViewModel.initGetSeasonBySerieId(serieId, seasonNumber);
            
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.content, SeasonFragment.newInstance(serieId, seasonNumber), "SeasonFragment")
                    .commit();
        }

        setupObservers();
    }

    private void setupObservers() {
        serieViewModel.getSeason().observe(this, season -> {
            if (season != null) {
                binding.contentMedia.noMediaDisplay.getRoot().setVisibility(View.GONE);
                binding.contentMedia.content.setVisibility(View.VISIBLE);
                if (getSupportActionBar() != null && season.getName() != null) {
                    getSupportActionBar().setTitle(season.getName());
                }
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
