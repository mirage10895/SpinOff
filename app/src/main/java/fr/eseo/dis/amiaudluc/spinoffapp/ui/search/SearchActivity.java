package fr.eseo.dis.amiaudluc.spinoffapp.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ActivitySearchBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SearchViewModel;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private SearchViewModel searchViewModel;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        binding.contentMain.content.setVisibility(View.GONE);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.content, SearchFragment.newInstance(1), "SearchFragment")
                    .commit();
        }

        setupListeners();
        setupObservers();
    }

    private void setupListeners() {
        binding.query.setOnKeyListener((view, keyCode, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                onSearch(binding.query.getText().toString());
                hideKeyboard(view);
                return true;
            }
            return false;
        });

        binding.query.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    binding.contentMain.progressBar.setVisibility(View.GONE);
                    binding.contentMain.content.setVisibility(View.GONE);
                    binding.noMediaDisplay.setVisibility(View.VISIBLE);
                    binding.nothingText.setText(R.string.search_for);
                } else {
                    onSearch(s.toString());
                }
            }
        });

        binding.searchButton.setOnClickListener(v -> {
            onSearch(binding.query.getText().toString());
            hideKeyboard(v);
        });
    }

    private void setupObservers() {
        searchViewModel.getMedias().observe(this, medias -> {
            binding.contentMain.progressBar.setVisibility(View.GONE);
            if (medias == null) {
                binding.noMediaDisplay.setVisibility(View.VISIBLE);
                binding.nothingText.setText(R.string.empty_desc_results);
                Snackbar.make(binding.getRoot(), R.string.no_results, Snackbar.LENGTH_LONG).show();
            } else if (medias.isEmpty()) {
                binding.contentMain.content.setVisibility(View.GONE);
                binding.noMediaDisplay.setVisibility(View.VISIBLE);
                binding.nothingText.setText(R.string.no_results);
            } else {
                binding.noMediaDisplay.setVisibility(View.GONE);
                binding.contentMain.content.setVisibility(View.VISIBLE);
            }
        });
    }

    private void onSearch(String query) {
        binding.contentMain.progressBar.setVisibility(View.VISIBLE);
        binding.noMediaDisplay.setVisibility(View.GONE);
        searchViewModel.initSearchByQuery(query);
    }

    private void hideKeyboard(View view) {
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
