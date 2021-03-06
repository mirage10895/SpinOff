package fr.eseo.dis.amiaudluc.spinoffapp.search;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.https.HttpsHandler;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.parser.WebServiceParser;

public class SearchActivity extends AppCompatActivity {

    EditText queryText;
    boolean loaded;
    String currentFragment;
    SearchFragment fragment = new SearchFragment();
    FrameLayout content;
    RelativeLayout noMedia;
    private InputMethodManager imm;
    private TextView noText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        noText = (TextView)findViewById(R.id.nothing_text);
        content = (FrameLayout) findViewById(R.id.content);
        content.setVisibility(View.GONE);
        noMedia = (RelativeLayout) findViewById(R.id.no_media_display);

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        currentFragment = getString(R.string.fragment_search_medias);
        getSupportFragmentManager().beginTransaction().replace(R.id.content,
                fragment, currentFragment).commit();

        queryText = (EditText) this.findViewById(R.id.query);

        queryText.setOnKeyListener((view, keyCode, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                onSearch(queryText.getText().toString());
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });

        queryText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")) {
                    content.setVisibility(View.GONE);
                    noText.setText(getString(R.string.search_for));
                    noMedia.setVisibility(View.VISIBLE);
                }else{
                    onSearch(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * This is the method called from the button
     * cf related xml file
     * @param view
     */
    public void onSearch(View view) {
        onSearch(queryText.getText().toString());
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void onSearch(String query) {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        GetSearch MtastSearch = new GetSearch();
        MtastSearch.setQuery(query);
        MtastSearch.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private class GetSearch extends android.os.AsyncTask<String, Void, String>{

        private String query;

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpsHandler sh = new HttpsHandler();
            String args = "&language=en-US&query="+this.getQuery();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall("search","multi",args);

            return jsonStr;
        }

        @Override
        protected void onPostExecute(String result) {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            ArrayList<Media> medias = WebServiceParser.multiMediasParser(result);
            if(!result.isEmpty()) {
                if (medias.isEmpty()){
                    content.setVisibility(View.GONE);
                    noMedia.setVisibility(View.VISIBLE);
                    noText.setText("No results for your query \n Try again !");
                }else {
                    noMedia.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);
                    Content.searchedMedias.clear();
                    Content.searchedSeries.clear();
                    Content.searchedMovies.clear();
                    Content.searchedArtists.clear();
                    Content.searchedMedias.addAll(medias);
                    fragment.setMedias(Content.searchedMedias);
                    loaded = true;
                }
            }else{
                noMedia.setVisibility(View.VISIBLE);
                noText.setText(R.string.empty_desc_results);
                Snackbar.make(fragment.getView(), R.string.no_results, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }
}
