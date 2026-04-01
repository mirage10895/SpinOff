package fr.eseo.dis.amiaudluc.spinoffapp.ui.movies;

import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ActivityMediaBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.MovieViewModel;

public class MovieActivity extends AppCompatActivity {

    private ActivityMediaBinding binding;
    private MovieViewModel movieViewModel;
    private int movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMediaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // to delegate the behavior of the app bar to the activity
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        movieId = getIntent().getIntExtra("id", -1);
        if (movieId == -1) {
            finish();
            return;
        }

        binding.closeButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        if (savedInstanceState == null) {
            movieViewModel.initGetMovieById(movieId);

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.content, SingleMovieFragment.newInstance(movieId), "SingleMovieFragment")
                    .commit();
        }

        setupObservers();
    }

    private void setupObservers() {
        movieViewModel.getMovie().observe(this, movie -> {
            if (movie != null) {
                binding.content.noMediaDisplay.getRoot().setVisibility(View.GONE);
                binding.content.content.setVisibility(View.VISIBLE);

                Picasso.get()
                        .load(getString(R.string.base_url_poster_500) + movie.getPosterPath())
                        .resize(25, 25)
                        .centerCrop()
                        .into(binding.blurredBackground);

                Picasso.get()
                        .load(getString(R.string.base_url_poster_original) + movie.getPosterPath())
                        .into(binding.mainImage);

                // 2. Application du flou matériel (API 31+)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    RenderEffect blurEffect = RenderEffect.createBlurEffect(
                            25f, // Rayon de flou horizontal
                            25f, // Rayon de flou vertical
                            Shader.TileMode.CLAMP // Comment les bords sont gérés
                    );
                    binding.blurredBackground.setRenderEffect(blurEffect);
                }

                binding.mediaName.setText(movie.getOriginalTitle());
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
