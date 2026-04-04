package fr.eseo.dis.amiaudluc.spinoffapp.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.FragmentSearchContainerBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SearchViewModel;

public class SearchContainerFragment extends Fragment {

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
                    binding.progressBar.setVisibility(View.GONE);
                    binding.resultsContainer.setVisibility(View.GONE);
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
        searchViewModel.getMedias().observe(getViewLifecycleOwner(), medias -> {
            binding.progressBar.setVisibility(View.GONE);
            if (medias == null) {
                binding.noMediaDisplay.setVisibility(View.VISIBLE);
                binding.nothingText.setText(R.string.empty_desc_results);
                Snackbar.make(binding.getRoot(), R.string.no_results, Snackbar.LENGTH_LONG).show();
            } else if (medias.isEmpty()) {
                binding.resultsContainer.setVisibility(View.GONE);
                binding.noMediaDisplay.setVisibility(View.VISIBLE);
                binding.nothingText.setText(R.string.no_results);
            } else {
                binding.noMediaDisplay.setVisibility(View.GONE);
                binding.resultsContainer.setVisibility(View.VISIBLE);
            }
        });
    }

    private void onSearch(String query) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.noMediaDisplay.setVisibility(View.GONE);
        searchViewModel.initSearchByQuery(query);
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
