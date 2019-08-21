package fr.eseo.dis.amiaudluc.spinoffapp.ui.episode;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
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
import fr.eseo.dis.amiaudluc.spinoffapp.model.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.parser.WebServiceParser;

/**
 * Created by lucasamiaud on 19/03/2018.
 */

public class EpisodeActivity extends AppCompatActivity {

    private Serie serie = Content.currentSerie;
    private Season season = Content.currentSeason;
    private Episode episode = Content.currentEpisode;
    private FrameLayout content;
    private RelativeLayout noMedia;
    private String currentFragment;
    private BottomNavigationView navigation;
    private EpisodeFragment fragment = new EpisodeFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setTitle(getString(R.string.emptyField));
            if (episode.getName() != null) {
                actionBar.setTitle(episode.getName());
            }
        }

        findViewById(R.id.fab).setVisibility(View.GONE);

        content = findViewById(R.id.content);
        content.setVisibility(View.GONE);
        noMedia = (RelativeLayout) findViewById(R.id.no_media_display);

        GetEpisode mGetTaskEp = new GetEpisode();
        mGetTaskEp.setTVId(this.serie.getId());
        mGetTaskEp.setEpisodeNumber(this.episode.getEpisodeNumber());
        mGetTaskEp.setSeasonNumber(this.season.getSeasonNumber());
        mGetTaskEp.execute();

    }

    private void setEpisode(Episode episode){
        this.episode = episode;
        Content.currentEpisode = episode;
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

    public class GetEpisode extends android.os.AsyncTask<String, Void, String>{

        private int tvId;
        private int seasonNumber;
        private int episodeNumber;

        public String getTvId() {
            return tvId+"";
        }

        public void setTVId(int id) {
            this.tvId = id;
        }

        public int getSeasonNumber() {
            return seasonNumber;
        }

        public void setSeasonNumber(int seasonNumber) {
            this.seasonNumber = seasonNumber;
        }

        public int getEpisodeNumber() {
            return episodeNumber;
        }

        public void setEpisodeNumber(int episodeNumber) {
            this.episodeNumber = episodeNumber;
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpsHandler sh = new HttpsHandler();
            String args = "&language=en-US&append_to_response=credits";

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall("tv",this.getTvId()+"/season/"+this.getSeasonNumber()+"/episode/+"+this.getEpisodeNumber(),args);

            //Log.e("TAG",jsonStr);
            return jsonStr;
        }

        @Override
        protected void onPostExecute(String result) {
            //findViewById(R.id.progressBarBody).setVisibility(View.GONE);
            if(!result.isEmpty()) {
                Episode episode = WebServiceParser.singleEpisodeParser(result);
                if (episode.getId() == -1){
                    noMedia.setVisibility(View.VISIBLE);
                }else {
                    setEpisode(episode);
                    fragment.setEpisode(episode);
                    noMedia.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);
                    currentFragment = getString(R.string.fragment_episode);
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
