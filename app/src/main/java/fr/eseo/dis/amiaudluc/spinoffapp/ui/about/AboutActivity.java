package fr.eseo.dis.amiaudluc.spinoffapp.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import fr.eseo.dis.amiaudluc.BuildConfig;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ActivityAboutBinding;

/**
 * About screen: application identity, version and the TMDB attribution required by the TMDB API
 * terms of use (official logo and verbatim notice).
 * <p>
 * Opened from the About row of the settings destination. The logo must stay unmodified and the
 * notice must remain visible in this About section.
 */
public class AboutActivity extends AppCompatActivity {

    private static final String TMDB_WEBSITE = "https://www.themoviedb.org";

    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.appBarMain.topBar.setTitle(R.string.about_title);
        binding.appBarMain.topBar.setAlpha(1);
        binding.appBarMain.closeButton.setOnClickListener(view -> finish());

        binding.textAboutVersion.setText(getString(R.string.about_version, BuildConfig.VERSION_NAME));
        binding.textAboutTmdbLink.setOnClickListener(view -> openTmdbWebsite());
    }

    private void openTmdbWebsite() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(TMDB_WEBSITE)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
