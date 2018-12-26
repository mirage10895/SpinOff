package fr.eseo.dis.amiaudluc.spinoffapp.series;

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
import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.DatabaseTransactionManager;
import fr.eseo.dis.amiaudluc.spinoffapp.https.HttpsHandler;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.parser.WebServiceParser;

public class SerieActivity extends AppCompatActivity {

    private Serie serie;
    private FrameLayout content;
    private RelativeLayout noMedia;
    private String currentFragment;
    private SingleSerieFragment fragment = new SingleSerieFragment();
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = AppDatabase.getAppDatabase(this);

        serie=Content.currentSerie;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if (DatabaseTransactionManager.getAllSerieIds(db).contains(serie.getId())){
                Snackbar.make(view, "You already got this serie on your library !", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }else{
                DatabaseTransactionManager.addSerieWithSeasons(db,Content.currentSerie);
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setTitle(getString(R.string.emptyField));
            if (serie.getName() != null) {
                actionBar.setTitle(serie.getName());
            }
        }

        setBackground(this.getResources().getString(R.string.base_url_poster_original) + serie.getBackdropPath());

        content = findViewById(R.id.content);
        content.setVisibility(View.GONE);
        noMedia = (RelativeLayout) findViewById(R.id.no_media_display);

        GetSingleSerie taskDetSer = new GetSingleSerie();
        taskDetSer.setId(serie.getId());
        taskDetSer.execute();

    }

    /**
     * Get the backdrop picture for the top
     * @param link
     */
    private void setBackground(String link){
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        final Target target = new Target(){

            final ProgressBar progressBar = findViewById(R.id.progressBar);
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                progressBar.setVisibility(View.GONE);
                collapsingToolbarLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(final Drawable errorDrawable) {
                progressBar.setVisibility(View.GONE);
                collapsingToolbarLayout.setBackground(getDrawable(R.drawable.ic_loading));
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class GetSingleSerie extends android.os.AsyncTask<String, Void, String>{

        private int id;

        public String getId() {
            return id+"";
        }

        public void setId(int id) {
            this.id = id;
        }


        @Override
        protected String doInBackground(String... strings) {
            HttpsHandler sh = new HttpsHandler();
            String args = "&language=en-US";

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall("tv",this.getId(),args);

            return jsonStr;
        }

        @Override
        protected void onPostExecute(String result) {
            if(!result.isEmpty()) {
                Serie serie = WebServiceParser.singleSerieParser(result);
                if (serie.getId() == -1){
                    noMedia.setVisibility(View.VISIBLE);
                }else {
                    Content.currentSerie = serie;
                    fragment.setSerie(serie);
                    noMedia.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);
                    currentFragment = getString(R.string.fragment_single_serie);
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
