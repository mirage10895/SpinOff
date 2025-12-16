package fr.eseo.dis.amiaudluc.spinoffapp.ui.artists;

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
import fr.eseo.dis.amiaudluc.spinoffapp.view_model.ArtistViewModel;

public class ArtistActivity extends AppCompatActivity {

    private ArtistViewModel artistViewModel;
    private FrameLayout content;
    private RelativeLayout noMedia;
    private ArtistFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        Integer id = getIntent().getIntExtra("id", 0);
        this.fragment = new ArtistFragment();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setTitle(getString(R.string.emptyField));
        }

        this.artistViewModel = new ViewModelProvider(this).get(ArtistViewModel.class);
        this.artistViewModel.initGetArtistById(id);
        this.artistViewModel.getArtist().observe(this, artist -> {
            if (artist != null) {
                this.fragment.setArtist(artist);
                noMedia.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content,
                        fragment, getString(R.string.fragment_artist))
                        .commit();
                if (artist.getName() != null) {
                    actionBar.setTitle(artist.getName());
                }
            } else {
                noMedia.setVisibility(View.VISIBLE);
                Snackbar.make(content, R.string.no_results, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        findViewById(R.id.fab).setVisibility(View.GONE);

        content = findViewById(R.id.content);
        content.setVisibility(View.GONE);
        noMedia = (RelativeLayout) findViewById(R.id.no_media_display);
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
