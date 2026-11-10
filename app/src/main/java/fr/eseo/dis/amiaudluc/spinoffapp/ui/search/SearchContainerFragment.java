package fr.eseo.dis.amiaudluc.spinoffapp.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.FragmentSearchContainerBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SearchUiState;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SearchViewModel;

/**
 * Hosts the search field and renders the {@link SearchUiState} exposed by {@link SearchViewModel}.
 * <p>
 * The fragment only forwards the typed text and displays the state: the debounce, the loading
 * flag and the freshness of the results are owned by the ViewModel.
 */
public class SearchContainerFragment extends Fragment {

    /** Opacity of the last results kept visible while a new query is loading. */
    private static final float DIMMED_ALPHA = 0.4f;
    private static final long ALPHA_ANIMATION_DURATION_MS = 150L;

    private FragmentSearchContainerBinding binding;
    private SearchViewModel searchViewModel;
    private InputMethodManager imm;

    public SearchContainerFragment() {
        // Required empty public constructor
    }

    public static SearchContainerFragment newInstance() {
        return new SearchContainerFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchContainerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container, SearchFragment.newInstance(), "SearchFragment")
                    .commit();
        }

        setupListeners();
        setupObservers();
    }

    private void setupListeners() {
        binding.searchBarContainer.setOnClickListener(view -> showKeyboard(binding.query));

        binding.clearQuery.setOnClickListener(view -> binding.query.setText(""));

        binding.query.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard(view);
                return true;
            }
            return false;
        });

        binding.query.setOnKeyListener((view, keyCode, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // The search is already handled by the debounce: Enter only dismisses the keyboard.
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
                updateClearButton(s);
                searchViewModel.onQueryChanged(s == null ? "" : s.toString());
            }
        });
    }

    /** Keeps the space of the clear button reserved so the text does not jump when it appears. */
    private void updateClearButton(CharSequence text) {
        boolean hasText = text != null && text.length() > 0;
        binding.clearQuery.setVisibility(hasText ? View.VISIBLE : View.INVISIBLE);
    }

    private void setupObservers() {
        searchViewModel.getUiState().observe(getViewLifecycleOwner(), this::render);
    }

    private void render(SearchUiState state) {
        if (binding == null) {
            return;
        }
        if (state instanceof SearchUiState.Loading loading) {
            renderLoading(loading.previousResults());
        } else if (state instanceof SearchUiState.Success success) {
            renderSuccess(success.medias());
        } else if (state instanceof SearchUiState.Error) {
            renderError();
        } else {
            renderIdle();
        }
    }

    /**
     * Shows the loader for the whole debounce + network window, keeping the last results visible
     * (dimmed) so the screen does not flash to an empty state on every keystroke.
     */
    private void renderLoading(List<Media> previousResults) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.noMediaDisplay.setVisibility(View.GONE);
        renderResults(previousResults, true);
    }

    private void renderSuccess(List<Media> medias) {
        binding.progressBar.setVisibility(View.GONE);
        if (medias.isEmpty()) {
            hideResults();
            showEmpty(R.string.no_results);
            return;
        }
        renderResults(medias, false);
    }

    private void renderError() {
        binding.progressBar.setVisibility(View.GONE);
        hideResults();
        showEmpty(R.string.empty_desc_results);
        Snackbar.make(binding.getRoot(), R.string.no_results, Snackbar.LENGTH_LONG).show();
    }

    private void renderIdle() {
        binding.progressBar.setVisibility(View.GONE);
        hideResults();
        showEmpty(R.string.search_for);
    }

    private void renderResults(List<Media> medias, boolean dimmed) {
        if (medias.isEmpty()) {
            hideResults();
            return;
        }
        float targetAlpha = dimmed ? DIMMED_ALPHA : 1f;
        boolean wasVisible = binding.resultsContainer.getVisibility() == View.VISIBLE;

        binding.noMediaDisplay.setVisibility(View.GONE);
        binding.resultsContainer.setVisibility(View.VISIBLE);

        if (wasVisible) {
            binding.resultsContainer.animate()
                    .alpha(targetAlpha)
                    .setDuration(ALPHA_ANIMATION_DURATION_MS)
                    .start();
        } else {
            // Appearing results must not flash at full opacity before being dimmed.
            binding.resultsContainer.setAlpha(targetAlpha);
        }
    }

    private void showEmpty(@StringRes int textRes) {
        binding.noMediaDisplay.setVisibility(View.VISIBLE);
        binding.nothingText.setText(textRes);
    }

    private void hideResults() {
        binding.resultsContainer.animate().cancel();
        binding.resultsContainer.setAlpha(1f);
        binding.resultsContainer.setVisibility(View.GONE);
    }

    private void showKeyboard(View view) {
        view.requestFocus();
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard(View view) {
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
