package fr.eseo.dis.amiaudluc.spinoffapp.ui;

import android.os.Bundle;
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
import fr.eseo.dis.amiaudluc.databinding.BottomSheetFiltersBinding;
import fr.eseo.dis.amiaudluc.databinding.FragmentHomeBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.TmdbApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.DiscoverFilters;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Genre;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FilterItem;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.GenreChipAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieDiscoveryFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieDiscoveryFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.DiscoveryViewModel;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryFilter;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.strategy.DiscoveryStrategy;

public class HomeFragment extends Fragment {

    private static final String ARG_TYPE = "fragment_type";
    private FragmentHomeBinding binding;

    private FragmentType fragmentType;
    private DiscoveryViewModel discoveryViewModel;
    private DiscoveryStrategy discoveryStrategy;
    private DiscoveryType type = DiscoveryType.POPULAR;

    private List<Integer> selectedGenreIds = new ArrayList<>();
    private Integer selectedYear = null;
    private Integer selectedRuntimeGte = null;
    private Integer selectedRuntimeLte = null;
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
        this.discoveryStrategy = DiscoveryStrategy.from(fragmentType);

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
                selectedYear = discoveryStrategy.getYear(filters);
                selectedRuntimeGte = filters.withRuntimeGte();
                selectedRuntimeLte = filters.withRuntimeLte();
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
        observeGenres();
    }

    private void observeGenres() {
        TmdbApiRepository repository = TmdbApiRepository.getInstance();
        discoveryStrategy.getGenres(repository).observe(getViewLifecycleOwner(), genres -> {
            this.availableGenres = genres;
        });
    }

    private void showFiltersBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), R.style.BentoBottomSheetDialog);
        BottomSheetFiltersBinding sheetBinding = BottomSheetFiltersBinding.inflate(getLayoutInflater());
        dialog.setContentView(sheetBinding.getRoot());

        // Setup Genres RecyclerView
        List<FilterItem> filterItems = availableGenres.stream()
                .map(g -> new FilterItem(g.getId(), g.getName(), selectedGenreIds.contains(g.getId())))
                .collect(Collectors.toList());

        GenreChipAdapter genreAdapter = new GenreChipAdapter(selectedIds -> {
            selectedGenreIds = selectedIds;
        });
        sheetBinding.recyclerGenres.setAdapter(genreAdapter);
        genreAdapter.submitList(filterItems);

        // Setup Year Spinner
        List<String> years = new ArrayList<>();
        String allYears = getString(R.string.filter_all_year);
        years.add(allYears);
        years.addAll(IntStream.rangeClosed(1900, LocalDate.now().getYear())
                .boxed()
                .sorted((a, b) -> b - a)
                .map(String::valueOf)
                .collect(Collectors.toList()));

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, years);
        sheetBinding.spinnerYear.setAdapter(yearAdapter);
        if (selectedYear != null) {
            sheetBinding.spinnerYear.setText(String.valueOf(selectedYear), false);
        } else {
            sheetBinding.spinnerYear.setText(allYears, false);
        }

        sheetBinding.spinnerYear.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            selectedYear = allYears.equals(selected) ? null : Integer.parseInt(selected);
        });

        // Setup Runtime
        if (selectedRuntimeGte != null && selectedRuntimeGte == 120 && selectedRuntimeLte == null) {
            sheetBinding.chipRuntimeLong.setChecked(true);
        } else if (selectedRuntimeLte != null && selectedRuntimeLte == 90 && selectedRuntimeGte == null) {
            sheetBinding.chipRuntimeShort.setChecked(true);
        }

        sheetBinding.chipGroupRuntime.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chip_runtime_short)) {
                selectedRuntimeLte = 90;
                selectedRuntimeGte = null;
            } else if (checkedIds.contains(R.id.chip_runtime_long)) {
                selectedRuntimeGte = 120;
                selectedRuntimeLte = null;
            }
        });

        // Setup Actions
        sheetBinding.btnReset.setOnClickListener(v -> {
            selectedGenreIds.clear();
            selectedYear = null;
            selectedRuntimeGte = null;
            selectedRuntimeLte = null;

            // Refresh UI in bottom sheet
            List<FilterItem> resetItems = availableGenres.stream()
                    .map(g -> new FilterItem(g.getId(), g.getName(), false))
                    .collect(Collectors.toList());
            genreAdapter.submitList(resetItems);
            sheetBinding.spinnerYear.setText(allYears, false);
            sheetBinding.chipGroupRuntime.clearCheck();
        });

        sheetBinding.btnApply.setOnClickListener(v -> {
            pushStateToViewModel();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setupFilters() {
        updatePresetFilter(type);
        binding.chipPopular.setOnClickListener(v -> updatePresetFilter(DiscoveryType.POPULAR));
        binding.chipTopRated.setOnClickListener(v -> updatePresetFilter(DiscoveryType.TOP_RATED));
        binding.chipOnAir.setOnClickListener(v -> updatePresetFilter(DiscoveryType.ON_AIR));

        binding.btnAddFilters.setOnClickListener(v -> showFiltersBottomSheet());
    }

    private void updatePresetFilter(DiscoveryType type) {
        this.type = type;
        DiscoverFilters presetFilters = mergeFilters(type, null, null, null, null);

        if (presetFilters.withGenres() != null && !presetFilters.withGenres().isEmpty()) {
            selectedGenreIds = Arrays.stream(presetFilters.withGenres().split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } else {
            selectedGenreIds = new ArrayList<>();
        }

        selectedYear = discoveryStrategy.getYear(presetFilters);
        selectedRuntimeGte = presetFilters.withRuntimeGte();
        selectedRuntimeLte = presetFilters.withRuntimeLte();

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

    private void pushStateToViewModel() {
        this.discoveryViewModel.setFilter(
                new DiscoveryFilter(type, mergeFilters(type, selectedGenreIds, selectedYear, selectedRuntimeGte, selectedRuntimeLte)),
                fragmentType
        );
    }

    private DiscoverFilters mergeFilters(
            DiscoveryType type,
            List<Integer> genreIds,
            Integer year,
            Integer runtimeGte,
            Integer runtimeLte
    ) {
        DiscoverFilters.DiscoverFiltersBuilder builder = discoveryStrategy.getBaseFilters(type);

        String genreString = (genreIds == null || genreIds.isEmpty()) ? null :
                genreIds.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
        builder.withGenres(genreString);

        discoveryStrategy.applyYear(builder, year);

        builder.withRuntimeGte(runtimeGte);
        builder.withRuntimeLte(runtimeLte);

        return builder.build();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
