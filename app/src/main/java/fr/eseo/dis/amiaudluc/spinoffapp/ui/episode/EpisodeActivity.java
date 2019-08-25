package fr.eseo.dis.amiaudluc.spinoffapp.ui.episode;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.repository.ApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.view_model.SerieViewModel;

/**
 * Created by lucasamiaud on 19/03/2018.
 */

public class EpisodeActivity extends AppCompatActivity {

    private SerieViewModel serieViewModel;
    private FrameLayout content;
    private RelativeLayout noMedia;
    private EpisodeFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Integer episodeNumber = getIntent().getIntExtra("episodeNumber", 0);
        Integer seasonNumber = getIntent().getIntExtra("seasonNumber", 0);
        Integer serieId = getIntent().getIntExtra("serieId", 0);
        ActionBar actionBar = getSupportActionBar();
        findViewById(R.id.fab).setVisibility(View.GONE);
        this.content = findViewById(R.id.content);
        this.content.setVisibility(View.GONE);
        this.noMedia = findViewById(R.id.no_media_display);
        this.fragment = new EpisodeFragment();
        this.serieViewModel = new SerieViewModel(ApiRepository.getInstance());
        this.serieViewModel.initGetEpisodeBySeasonNumberBySerieId(serieId, seasonNumber, episodeNumber);
        this.serieViewModel.getEpisode().observe(this, episode -> {
            if (episode != null) {
                fragment.setEpisode(episode);
                noMedia.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        fragment, getString(R.string.fragment_episode)).commit();
                actionBar.setTitle(episode.getName());
            } else {
                noMedia.setVisibility(View.VISIBLE);
                Snackbar.make(content, R.string.no_results, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setTitle(getString(R.string.emptyField));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.menu.options_menu:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
