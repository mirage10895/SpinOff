package fr.eseo.dis.amiaudluc.spinoffapp.movies;

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
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.parser.WebServiceParser;

public class MovieActivity extends AppCompatActivity {

    private Movie movie = Content.currentMovie;
    private FrameLayout content;
    private RelativeLayout noMedia;
    private String currentFragment;
    private SingleMovieFragment fragment = SingleMovieFragment.newInstance();
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = AppDatabase.getAppDatabase(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if (DatabaseTransactionManager.getAllMovieIds(db).contains(movie.getId())){
                Snackbar.make(view,"Movie is already in your library",Snackbar.LENGTH_LONG)
                        .setAction("Action",null).show();
            }else{
                DatabaseTransactionManager.addMovie(db, movie);
                Snackbar.make(view,"Movie added to your library",Snackbar.LENGTH_LONG)
                        .setAction("Action",null).show();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setTitle(getString(R.string.emptyField));
            if (movie.getTitle() != null) {
                actionBar.setTitle(movie.getOriginalTitle());
            }
        }

        setBackground(this.getResources().getString(R.string.base_url_poster_original) + movie.getBackdropPath());

        content = findViewById(R.id.content);
        content.setVisibility(View.GONE);
        noMedia = (RelativeLayout) findViewById(R.id.no_media_display);

        GetSingleMovie mTaskGetSMov = new GetSingleMovie();
        mTaskGetSMov.setId(movie.getId());
        mTaskGetSMov.execute();

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
                collapsingToolbarLayout.setBackground(getDrawable(R.drawable.ic_cam_iris));
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

    private class GetSingleMovie extends android.os.AsyncTask<String, Void, String>{

        private int id;

        public String getId() {
            return String.valueOf(id);
        }

        public void setId(int id) {
            this.id = id;
        }


        @Override
        protected String doInBackground(String... strings) {
            HttpsHandler sh = new HttpsHandler();
            String args = "&language=en-US&append_to_response=credits,videos";

            // Making a request to url and getting response

            return sh.makeServiceCall("movie", this.getId(),args);
        }

        @Override
        protected void onPostExecute(String result) {
            if(!result.isEmpty()) {
                Movie movie = WebServiceParser.singleMovieParser(result);
                if (movie.getId() == -1){
                    noMedia.setVisibility(View.VISIBLE);
                }else {
                    noMedia.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);
                    Content.currentMovie = movie;
                    fragment.setMovie(movie);
                    currentFragment = getString(R.string.fragment_single_movie);
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
