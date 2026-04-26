package fr.eseo.dis.amiaudluc.spinoffapp.ui;

import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.BottomSheetGenresBinding;
import fr.eseo.dis.amiaudluc.databinding.FragmentHomeBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.TmdbApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.DiscoverFilters;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Genre;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.CheckboxFilterAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FilterItem;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieDiscoveryFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieDiscoveryFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.DiscoveryViewModel;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryFilter;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.MovieType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.SerieType;

public class HomeFragment extends Fragment {

    private static final String ARG_TYPE = "fragment_type";
    private FragmentHomeBinding binding;

    private FragmentType fragmentType;
    private DiscoveryViewModel discoveryViewModel;
    private DiscoveryType type = DiscoveryType.POPULAR;

    private List<Integer> selectedGenreIds = new ArrayList<>();
    private Integer selectedYear = null;
    private List<Genre> availableGenres = new ArrayList<>();


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(FragmentType type) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TYPE, type);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fragmentType = (FragmentType) getArguments().getSerializable(ARG_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.discoveryViewModel = new ViewModelProvider(requireActivity()).get(DiscoveryViewModel.class);

        // Restore state from ViewModel
        DiscoveryFilter savedFilter = this.discoveryViewModel.getFilter(fragmentType).getValue();
        if (savedFilter != null) {
            this.type = savedFilter.type();
            DiscoverFilters filters = savedFilter.filters();
            if (filters != null) {
                if (filters.withGenres() != null && !filters.withGenres().isEmpty()) {
                    selectedGenreIds = Arrays.stream(filters.withGenres().split(","))
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                } else {
                    selectedGenreIds = new ArrayList<>();
                }
                selectedYear = fragmentType == FragmentType.MOVIE ? filters.primaryReleaseYear() : filters.firstAirDateYear();
            }
        }

        binding.textWelcome.setText(R.string.menu_discover);

        if (savedInstanceState == null) {
            Fragment discoveryFragment = fragmentType == FragmentType.MOVIE ?
                    MovieDiscoveryFragment.newInstance() :
                    SerieDiscoveryFragment.newInstance();

            getChildFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.discover_fragment_container, discoveryFragment)
                    .commit();
        }

        setupFilters();
        setupSpinners();
    }

    private void setupSpinners() {
        TmdbApiRepository repository = TmdbApiRepository.getInstance();
        if (fragmentType == FragmentType.MOVIE) {
            repository.getMovieGenres().observe(getViewLifecycleOwner(), genres -> {
                this.availableGenres = genres;
                updateGenreUI();
            });
        } else {
            repository.getSerieGenres().observe(getViewLifecycleOwner(), genres -> {
                this.availableGenres = genres;
                updateGenreUI();
            });
        }

        binding.spinnerGenre.setOnClickListener(v -> showGenreBottomSheet());

        List<String> years = new ArrayList<>();
        String allYears = getString(R.string.filter_all_year);
        years.add(allYears);
        years.addAll(IntStream.rangeClosed(1900, LocalDate.now().getYear())
                .boxed()
                .sorted((a, b) -> b - a)
                .map(String::valueOf)
                .collect(Collectors.toList()));

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, years);
        binding.spinnerYear.setAdapter(yearAdapter);
        if (selectedYear != null) {
            binding.spinnerYear.setText(String.valueOf(selectedYear), false);
        }
        binding.spinnerYear.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            selectedYear = allYears.equals(selected) ? null : Integer.parseInt(selected);
            pushStateToViewModel();
        });
    }

    private void showGenreBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), R.style.BentoBottomSheetDialog);
        BottomSheetGenresBinding binding = BottomSheetGenresBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        List<FilterItem> filterItems = availableGenres.stream()
                .map(g -> new FilterItem(g.getId(), g.getName(), selectedGenreIds.contains(g.getId())))
                .collect(Collectors.toList());

        CheckboxFilterAdapter adapter = new CheckboxFilterAdapter(null);
        binding.recyclerGenres.setAdapter(adapter);
        adapter.submitList(filterItems);

        binding.btnClearGenres.setOnClickListener(v -> {
            List<FilterItem> clearedList = adapter.getCurrentList().stream()
                    .map(item -> new FilterItem(item.id(), item.name(), false))
                    .collect(Collectors.toList());
            adapter.submitList(clearedList);
        });

        binding.btnApply.setOnClickListener(v -> {
            selectedGenreIds = adapter.getSelectedIds();
            updateGenreUI();
            pushStateToViewModel();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateGenreUI() {
        if (availableGenres == null || availableGenres.isEmpty()) return;

        if (selectedGenreIds.isEmpty()) {
            binding.spinnerGenre.setText(R.string.filter_all_genres);
        } else {
            String names = availableGenres.stream()
                    .filter(g -> selectedGenreIds.contains(g.getId()))
                    .map(Genre::getName)
                    .collect(Collectors.joining(", "));
            binding.spinnerGenre.setText(names);
        }
    }

    private void setupFilters() {
        updatePresetFilter(type);
        binding.chipPopular.setOnClickListener(v -> updatePresetFilter(DiscoveryType.POPULAR));
        binding.chipTopRated.setOnClickListener(v -> updatePresetFilter(DiscoveryType.TOP_RATED));
        binding.chipOnAir.setOnClickListener(v -> updatePresetFilter(DiscoveryType.ON_AIR));

        binding.btnAddFilters.setOnClickListener(v -> {
            boolean isVisible = binding.layoutExtraFilters.getVisibility() == View.VISIBLE;
            TransitionManager.beginDelayedTransition(binding.getRoot());
            binding.layoutExtraFilters.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        });
    }

    private void updatePresetFilter(DiscoveryType type) {
        this.type = type;
        DiscoverFilters presetFilters = fragmentType == FragmentType.MOVIE ?
                MovieType.valueOf(type.name()).getDiscoverFilters().apply(1) :
                SerieType.valueOf(type.name()).getDiscoverFilters().apply(1);

        if (presetFilters.withGenres() != null && !presetFilters.withGenres().isEmpty()) {
            selectedGenreIds = Arrays.stream(presetFilters.withGenres().split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } else {
            selectedGenreIds = new ArrayList<>();
        }

        selectedYear = fragmentType == FragmentType.MOVIE ?
                presetFilters.primaryReleaseYear() :
                presetFilters.firstAirDateYear();

        // Update UI
        updateGenreUI();
        updateSpinnerSelections();

        switch (type) {
            case POPULAR:
                binding.chipPopular.setChecked(true);
                break;
            case TOP_RATED:
                binding.chipTopRated.setChecked(true);
                break;
            case ON_AIR:
                binding.chipOnAir.setChecked(true);
                break;
        }
        pushStateToViewModel();
    }

    private void updateSpinnerSelections() {
        if (selectedYear != null) {
            binding.spinnerYear.setText(String.valueOf(selectedYear), false);
        } else {
            binding.spinnerYear.setText(getString(R.string.filter_all_year), false);
        }
    }

    private void pushStateToViewModel() {
        DiscoverFilters presetFilters = fragmentType == FragmentType.MOVIE ?
                MovieType.valueOf(type.name()).getDiscoverFilters().apply(1) :
                SerieType.valueOf(type.name()).getDiscoverFilters().apply(1);

        DiscoverFilters.DiscoverFiltersBuilder builder = presetFilters.toBuilder();

        if (!selectedGenreIds.isEmpty()) {
            String genreString = selectedGenreIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            builder.withGenres(genreString);
        } else {
            builder.withGenres(null);
        }

        if (fragmentType == FragmentType.MOVIE) {
            builder.primaryReleaseYear(selectedYear);
        } else {
            builder.firstAirDateYear(selectedYear);
        }

        this.discoveryViewModel.setFilter(new DiscoveryFilter(type, builder.build()), fragmentType);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
