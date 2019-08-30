package fr.eseo.dis.amiaudluc.spinoffapp.ui.series;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.action.DeleteSerieActionListener;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.DatabaseTransactionManager;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.repository.ApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.view_model.SerieViewModel;

public class SerieActivity extends AppCompatActivity {

    private SerieViewModel serieViewModel;
    private Serie serie;
    private FrameLayout content;
    private RelativeLayout noMedia;
    private String currentFragment;
    private SingleSerieFragment fragment;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        content = findViewById(R.id.content);
        noMedia = findViewById(R.id.no_media_display);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Integer id = getIntent().getIntExtra("id", 0);
        ActionBar actionBar = getSupportActionBar();
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setEnabled(false);
        this.db = AppDatabase.getAppDatabase(this);
        this.fragment = new SingleSerieFragment();
        this.serieViewModel = new SerieViewModel(ApiRepository.getInstance());
        this.serieViewModel.initGetSerieById(id);
        this.serieViewModel.getSerie().observe(this, serieResult -> {
            if (serieResult != null) {
                this.serie = serieResult;
                setBackground(this.getResources().getString(R.string.base_url_poster_original) + serieResult.getBackdropPath());
                fragment.setSerie(serieResult);
                noMedia.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
                currentFragment = getString(R.string.fragment_single_serie);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, fragment, currentFragment)
                        .commit();
                actionBar.setTitle(serieResult.getName());
            } else {
                noMedia.setVisibility(View.VISIBLE);
                Snackbar.make(content, R.string.no_results, Snackbar.LENGTH_LONG)
                        .setAction("DAMN", view -> view.setVisibility(View.GONE)).show();
            }
        });
        db.serieDAO().getAllIds().observe(this, integers -> {
            if (integers != null && !integers.contains(id)) {
                fab.setEnabled(true);
            } else if (integers != null && integers.contains(id)) {
                fab.setEnabled(false);
            }
        });

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setTitle(null);
        }
        fab.setOnClickListener(view -> {
            fab.setEnabled(false);
            DatabaseTransactionManager.addSerieWithSeasons(db, this.serie);
            Snackbar.make(view, "Serie added to your library !", Snackbar.LENGTH_LONG)
                    .setAction("Undo", new DeleteSerieActionListener(db, this.serie)).show();
        });

    }

    /**
     * Get the backdrop picture for the top
     *
     * @param link
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
            public void onBitmapFailed(final Drawable errorDrawable) {
                progressBar.setVisibility(View.GONE);
                collapsingToolbarLayout.setBackground(getDrawable(R.drawable.ic_launcher_foreground));
                Log.d("TAG", "FAILED");
            }

            @Override
            public void onPrepareLoad(final Drawable placeHolderDrawable) {
                progressBar.setVisibility(View.VISIBLE);
                Log.d("TAG", "Prepare Load");
            }
        };
        collapsingToolbarLayout.setTag(target);
        Picasso.with(this).load(link).into(target);
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
