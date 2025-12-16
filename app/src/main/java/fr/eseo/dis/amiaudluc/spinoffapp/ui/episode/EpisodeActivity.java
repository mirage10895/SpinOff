package fr.eseo.dis.amiaudluc.spinoffapp.ui.episode;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.view_model.SerieViewModel;

/**
 * Created by lucasamiaud on 19/03/2018.
 */

public class EpisodeActivity extends AppCompatActivity {

    private SerieViewModel serieViewModel;
    private FrameLayout content;
    private RelativeLayout noMedia;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        this.findViewById(R.id.fab).setVisibility(View.GONE);
        this.content = this.findViewById(R.id.content);
        this.content.setVisibility(View.GONE);
        this.noMedia = this.findViewById(R.id.no_media_display);

        this.serieViewModel = new ViewModelProvider(this).get(SerieViewModel.class);

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.emptyField));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActionBar actionBar = getSupportActionBar();

        Integer episodeNumber = getIntent().getIntExtra("episodeNumber", 0);
        Integer seasonNumber = getIntent().getIntExtra("seasonNumber", 0);
        Integer serieId = getIntent().getIntExtra("serieId", 0);

        EpisodeFragment fragment = new EpisodeFragment();
        this.serieViewModel.initGetEpisodeBySeasonNumberBySerieId(serieId, seasonNumber, episodeNumber);
        this.serieViewModel.getEpisode().observe(this, episode -> {
            if (episode != null) {
                fragment.setEpisode(episode);
                this.noMedia.setVisibility(View.GONE);
                this.content.setVisibility(View.VISIBLE);
                this.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content, fragment, getString(R.string.fragment_episode))
                        .commit();
                if (actionBar != null) {
                    actionBar.setTitle(episode.getName());
                }
            } else {
                noMedia.setVisibility(View.VISIBLE);
                Snackbar.make(content, R.string.no_results, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.menu.options_menu) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
