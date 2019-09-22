package fr.eseo.dis.amiaudluc.spinoffapp.ui.season;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.repository.ApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.view_model.SerieViewModel;

public class SeasonActivity extends AppCompatActivity {

    private SerieViewModel serieViewModel;
    private Season season;
    private FrameLayout content;
    private RelativeLayout noMedia;
    private SeasonFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season);
        Integer seasonNumber = getIntent().getIntExtra("seasonNumber", 0);
        Integer serieId = getIntent().getIntExtra("serieId", 0);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.emptyField));
        }
        this.fragment = new SeasonFragment();
        this.content = findViewById(R.id.content);
        this.noMedia = findViewById(R.id.no_media_display);
        this.serieViewModel = new SerieViewModel(ApiRepository.getInstance());
        this.serieViewModel.initGetSeasonBySerieId(serieId, seasonNumber);
        this.findViewById(R.id. fab).setVisibility(View.GONE);
        this.content.setVisibility(View.GONE);
        this.serieViewModel.getSeason().observe(this, season -> {
            if (season != null) {
                this.season = season;
                season.setSerieId(serieId);
                this.fragment.setSeason(season);
                this.noMedia.setVisibility(View.GONE);
                this.content.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.content,
                        fragment, getString(R.string.fragment_season)).commit();
                if (season.getName() != null) {
                    actionBar.setTitle(season.getName());
                }
            } else {
                noMedia.setVisibility(View.VISIBLE);
                Snackbar.make(content, R.string.no_results, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
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
