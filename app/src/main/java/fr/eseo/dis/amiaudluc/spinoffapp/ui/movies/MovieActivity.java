package fr.eseo.dis.amiaudluc.spinoffapp.ui.movies;

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
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.action.AddMovieActionListener;
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

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(null);
        }

        movieId = getIntent().getIntExtra("id", -1);
        if (movieId == -1) {
            finish();
            return;
        }

        binding.fab.setEnabled(false);
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        if (savedInstanceState == null) {
            movieViewModel.initGetMovieById(movieId);
            movieViewModel.initDatabaseMovies();

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

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(movie.getTitle());
                }

                String backdropUrl = getString(R.string.base_url_poster_original) + movie.getBackdropPath();
                setBackground(backdropUrl);

                binding.fab.setOnClickListener(new AddMovieActionListener(movieViewModel, movie));
            } else {
                binding.content.noMediaDisplay.getRoot().setVisibility(View.VISIBLE);
                Snackbar.make(binding.getRoot(), R.string.no_results, Snackbar.LENGTH_LONG).show();
            }
        });

        movieViewModel.getDatabaseMovies().observe(this, movies -> {
            boolean isAlreadyInDatabase = movies.stream().map(MovieDatabase::getId).anyMatch(id -> id.equals(movieId));
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
                Log.e("MovieActivity", "Failed to load backdrop", e);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                binding.progressBar.setVisibility(View.VISIBLE);
            }
        };

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
