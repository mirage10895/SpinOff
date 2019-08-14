package fr.eseo.dis.amiaudluc.spinoffapp.artists;

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
import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.https.HttpsHandler;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.parser.WebServiceParser;

public class ArtistActivity extends AppCompatActivity {

    private Artist artist;
    private FrameLayout content;
    private RelativeLayout noMedia;
    private String currentFragment;
    private ArtistFragment fragment = new ArtistFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setTitle(getString(R.string.emptyField));
            if (artist.getName() != null) {
                actionBar.setTitle(artist.getName());
            }
        }

        findViewById(R.id.fab).setVisibility(View.GONE);

        content = findViewById(R.id.content);
        content.setVisibility(View.GONE);
        noMedia = (RelativeLayout) findViewById(R.id.no_media_display);

        //findViewById(R.id.progressBarBody).setVisibility(View.VISIBLE);
        ArtistActivity.GetArtist mGetTaskSS = new GetArtist();
        mGetTaskSS.setIdArtist(artist.getId());
        mGetTaskSS.execute();
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

    private class GetArtist extends android.os.AsyncTask<String, Void, String>{

        private int idArtist;

        public String getIdArtist() {
            return idArtist +"";
        }

        public void setIdArtist(int id) {
            this.idArtist = id;
        }


        @Override
        protected String doInBackground(String... strings) {
            HttpsHandler sh = new HttpsHandler();
            String args = "&language=en-US&append_to_response=tv_credits,movie_credits";

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall("person",this.getIdArtist(),args);

            return jsonStr;
        }

        @Override
        protected void onPostExecute(String result) {
            if(!result.isEmpty()) {
                Artist artist = WebServiceParser.singleArtistParser(result);
                if (artist.getId() == -1){
                    noMedia.setVisibility(View.VISIBLE);
                }else {
                    Content.currentArtist = artist;
                    fragment.setArtist(artist);
                    noMedia.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);
                    currentFragment = getString(R.string.fragment_artist);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content,
                            fragment, currentFragment).commit();
                }
            }else{
                noMedia.setVisibility(View.VISIBLE);
                Snackbar.make(content, R.string.no_results, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }
}
