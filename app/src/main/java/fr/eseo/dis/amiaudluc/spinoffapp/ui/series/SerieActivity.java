package fr.eseo.dis.amiaudluc.spinoffapp.ui.series;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ActivityMediaBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.action.AddSerieActionListener;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SerieViewModel;

public class SerieActivity extends AppCompatActivity {

    private ActivityMediaBinding binding;
    private SerieViewModel serieViewModel;
    private int serieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMediaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(null);
        }

        serieId = getIntent().getIntExtra("id", -1);
        if (serieId == -1) {
            finish();
            return;
        }

        binding.fab.setEnabled(false);

        serieViewModel = new ViewModelProvider(this).get(SerieViewModel.class);
        
        if (savedInstanceState == null) {
            serieViewModel.initGetSerieById(serieId);
            serieViewModel.initDatabaseSeries();
            
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.content, SingleSerieFragment.newInstance(serieId), "SingleSerieFragment")
                    .commit();
        }

        setupObservers();
    }

    private void setupObservers() {
        serieViewModel.getSerie().observe(this, serie -> {
            if (serie != null) {
                binding.content.noMediaDisplay.getRoot().setVisibility(View.GONE);
                binding.content.content.setVisibility(View.VISIBLE);
                
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(serie.getName());
                }
                
                String backdropUrl = getString(R.string.base_url_poster_original) + serie.getBackdropPath();
                setBackground(backdropUrl);
                
                binding.fab.setOnClickListener(new AddSerieActionListener(serieViewModel, serie));
            } else {
                binding.content.noMediaDisplay.getRoot().setVisibility(View.VISIBLE);
                Snackbar.make(binding.getRoot(), R.string.no_results, Snackbar.LENGTH_LONG).show();
            }
        });

        serieViewModel.getDatabaseSeries().observe(this, series -> {
            boolean isAlreadyInDatabase = series.stream().anyMatch(s -> s.getId().equals(serieId));
            binding.fab.setEnabled(!isAlreadyInDatabase);
        });
    }

    private void setBackground(String link) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                binding.progressBar.setVisibility(View.GONE);
                binding.toolbarLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                binding.progressBar.setVisibility(View.GONE);
                binding.toolbarLayout.setBackgroundResource(R.drawable.ic_launcher_foreground);
                Log.e("SerieActivity", "Failed to load backdrop", e);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                binding.progressBar.setVisibility(View.VISIBLE);
            }
        };
        
        // Storing target as a tag to prevent GC
        binding.toolbarLayout.setTag(target);
        Picasso.get().load(link).into(target);
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
