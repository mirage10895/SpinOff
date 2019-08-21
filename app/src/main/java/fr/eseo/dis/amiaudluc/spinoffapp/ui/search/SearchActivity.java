package fr.eseo.dis.amiaudluc.spinoffapp.ui.search;

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

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.repository.ApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.view_model.SearchViewModel;

public class SearchActivity extends AppCompatActivity {

    private SearchViewModel searchViewModel;
    private EditText queryText;
    String currentFragment;
    SearchFragment fragment;
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

        this.searchViewModel = new SearchViewModel(ApiRepository.getInstance());
        this.fragment = SearchFragment.newInstance(1);

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
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content,
                        fragment,
                        currentFragment
                )
                .commit();

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
                } else {
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
        this.searchViewModel.initSearchByQuery(query);
        this.searchViewModel.getMedias().observe(this, media -> {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            if (media == null) {
                noMedia.setVisibility(View.VISIBLE);
                noText.setText(R.string.empty_desc_results);
                Snackbar.make(fragment.getView(), R.string.no_results, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else if (media.isEmpty()) {
                content.setVisibility(View.GONE);
                noMedia.setVisibility(View.VISIBLE);
                noText.setText("No results for your query \n Try again !");
            } else {
                noMedia.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
                fragment.setMedias(media);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
