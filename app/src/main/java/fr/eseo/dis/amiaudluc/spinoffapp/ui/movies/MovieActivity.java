package fr.eseo.dis.amiaudluc.spinoffapp.ui.movies;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.action.AddMovieActionListener;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.MovieViewModel;

public class MovieActivity extends AppCompatActivity {

    private MovieViewModel movieViewModel;
    private Movie movie;

    // layout
    private FrameLayout content;
    private RelativeLayout noMedia;
    private String currentFragment;
    private SingleMovieFragment fragment;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.fab = findViewById(R.id.fab);
        this.fab.setEnabled(false);
        this.content = findViewById(R.id.content);
        this.noMedia = findViewById(R.id.no_media_display);

        this.fragment = SingleMovieFragment.newInstance();
        this.movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Integer id = getIntent().getIntExtra("id", 0);
        ActionBar actionBar = getSupportActionBar();

        this.movieViewModel.initGetMovieById(id);
        this.movieViewModel.initDatabaseMovies();
        this.movieViewModel.getMovie().observe(
                this,
                movieResult -> {
                    if (movieResult != null) {
                        this.movie = movieResult;
                        noMedia.setVisibility(View.GONE);
                        content.setVisibility(View.VISIBLE);
                        fragment.setMovie(movieResult);
                        currentFragment = getString(R.string.fragment_single_movie);
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment, currentFragment).commit();
                        setBackground(this.getResources().getString(R.string.base_url_poster_original) + movie.getBackdropPath());
                        actionBar.setTitle(movieResult.getTitle());
                        this.fab.setOnClickListener(new AddMovieActionListener(this.movieViewModel, movieResult));
                    } else {
                        noMedia.setVisibility(View.VISIBLE);
                        Snackbar.make(content, R.string.no_results, Snackbar.LENGTH_LONG)
                                .setAction("DAMN", view -> view.setVisibility(View.GONE))
                                .show();
                    }
                }
        );
        this.movieViewModel.getDatabaseMovies().observe(this, movies -> {
            boolean hasTheId = movies.stream().map(MovieDatabase::getId).anyMatch(mid -> mid.equals(id));
            this.fab.setEnabled(!hasTheId);
        });

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(null);
        }
    }

    /**
     * Get the backdrop picture for the top
     */
    private void setBackground(String link) {
        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        final Target target = new Target() {

            final ProgressBar progressBar = findViewById(R.id.progressBar);

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                progressBar.setVisibility(View.GONE);
                collapsingToolbarLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(Exception e, final Drawable errorDrawable) {
                progressBar.setVisibility(View.GONE);
                collapsingToolbarLayout.setBackground(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_launcher_foreground));
                Log.d("TAG", "FAILED");
            }

            @Override
            public void onPrepareLoad(final Drawable placeHolderDrawable) {
                progressBar.setVisibility(View.VISIBLE);
                Log.d("TAG", "Prepare Load");
            }
        };
        collapsingToolbarLayout.setTag(target);
        Picasso.get().load(link).error(R.drawable.ic_launcher_foreground).into(target);
    }

    /**
     * This function is simply allowing us to go back to the previous activity when the user
     * tap on the back button in the supportActionBar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
