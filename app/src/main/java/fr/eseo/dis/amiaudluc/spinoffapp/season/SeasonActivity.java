package fr.eseo.dis.amiaudluc.spinoffapp.season;

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
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.parser.WebServiceParser;

public class SeasonActivity extends AppCompatActivity {

    private Serie serie = Content.currentSerie;
    private Season season = Content.currentSeason;
    private FrameLayout content;
    private RelativeLayout noMedia;
    private String currentFragment;
    private SeasonFragment fragment = new SeasonFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setTitle(getString(R.string.emptyField));
            if (season.getName() != null) {
                actionBar.setTitle(season.getName());
            }
        }

        findViewById(R.id. fab).setVisibility(View.GONE);

        content = findViewById(R.id.content);
        content.setVisibility(View.GONE);
        noMedia = (RelativeLayout) findViewById(R.id.no_media_display);

        GetSingleSeason mGetTaskSS = new GetSingleSeason();
        mGetTaskSS.setTVId(this.serie.getId());
        mGetTaskSS.setSeasonNumber(this.season.getSeasonNumber());
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

    public class GetSingleSeason extends android.os.AsyncTask<String, Void, String>{

        private int tvId;
        private int seasonNumber;

        public String getTVId() {
            return String.valueOf(tvId);
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


        @Override
        protected String doInBackground(String... strings) {
            HttpsHandler sh = new HttpsHandler();
            String args = "&language=en-US&append_to_response=credits,videos";

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall("tv",this.getTVId()+"/season/"+this.getSeasonNumber(),args);

            return jsonStr;
        }

        @Override
        protected void onPostExecute(String result) {
            //findViewById(R.id.progressBarBody).setVisibility(View.GONE);
            if(!result.isEmpty()) {
                Season season = WebServiceParser.singleSeasonParser(result);
                if (serie.getId() == -1){
                    noMedia.setVisibility(View.VISIBLE);
                }else {
                    Content.currentSeason = season;
                    fragment.setSeason(season);
                    noMedia.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);
                    currentFragment = getString(R.string.fragment_season);
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
