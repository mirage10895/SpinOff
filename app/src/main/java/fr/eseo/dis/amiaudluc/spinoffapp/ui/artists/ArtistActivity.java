package fr.eseo.dis.amiaudluc.spinoffapp.ui.artists;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ActivityArtistBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.ArtistViewModel;

public class ArtistActivity extends AppCompatActivity {

    private ActivityArtistBinding binding;
    private ArtistViewModel artistViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArtistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.emptyField);
        }

        binding.content.content.setVisibility(View.GONE);

        artistViewModel = new ViewModelProvider(this).get(ArtistViewModel.class);
        int artistId = getIntent().getIntExtra("id", 0);

        if (savedInstanceState == null) {
            artistViewModel.initGetArtistById(artistId);
            
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.content, ArtistFragment.newInstance(artistId), "ArtistFragment")
                    .commit();
        }

        setupObservers();
    }

    private void setupObservers() {
        artistViewModel.getArtist().observe(this, artist -> {
            if (artist != null) {
                binding.content.noMediaDisplay.getRoot().setVisibility(View.GONE);
                binding.content.content.setVisibility(View.VISIBLE);
                if (getSupportActionBar() != null && artist.getName() != null) {
                    getSupportActionBar().setTitle(artist.getName());
                }
            } else {
                binding.content.noMediaDisplay.getRoot().setVisibility(View.VISIBLE);
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
