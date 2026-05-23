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
import java.util.Objects;
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
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter.ArraySearchAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.countries.CountryUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.filters.FilterItem;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.filters.GenreChipAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.filters.RuntimeChipAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieDiscoveryFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieDiscoveryFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.DiscoveryViewModel;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryFilter;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.strategy.DiscoveryStrategy;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.strategy.RuntimeFilter;

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
    private String selectedCountryCode = null;
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

        binding.textWelcome.setText(R.string.menu_discover);

        setupState(fragmentType);
        setupFragments(savedInstanceState);
        setupFilters();
        observeGenres();
    }

    private void setupState(FragmentType fragmentType) {
        this.fragmentType = fragmentType;
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
                selectedCountryCode = filters.withOriginCountry();
            }
        } else {
            // Default state if no saved filter
            this.type = DiscoveryType.POPULAR;
            selectedGenreIds = new ArrayList<>();
            selectedYear = null;
            selectedRuntimeGte = null;
            selectedRuntimeLte = null;
            selectedCountryCode = null;
        }
    }

    private void setupFragments(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            replaceDiscoveryFragment();
        }
    }

    private void replaceDiscoveryFragment() {
        Fragment discoveryFragment = fragmentType == FragmentType.MOVIE ?
                MovieDiscoveryFragment.newInstance() :
                SerieDiscoveryFragment.newInstance();

        getChildFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.discover_fragment_container, discoveryFragment)
                .commit();
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

        // Setup Discovery Type Chips
        switch (type) {
            case POPULAR:
                sheetBinding.chipPopular.setChecked(true);
                break;
            case TOP_RATED:
                sheetBinding.chipTopRated.setChecked(true);
                break;
            case ON_AIR:
                sheetBinding.chipOnAir.setChecked(true);
                break;
        }

        sheetBinding.chipGroupDiscoveryType.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chip_popular)) {
                type = DiscoveryType.POPULAR;
            } else if (checkedIds.contains(R.id.chip_top_rated)) {
                type = DiscoveryType.TOP_RATED;
            } else if (checkedIds.contains(R.id.chip_on_air)) {
                type = DiscoveryType.ON_AIR;
            }
        });

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
        List<String> years = IntStream.rangeClosed(1900, LocalDate.now().getYear())
                .boxed()
                .sorted((a, b) -> b - a)
                .map(String::valueOf)
                .collect(Collectors.toList());

        ArrayAdapter<String> yearAdapter = new ArraySearchAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                years,
                year -> year
        );
        sheetBinding.spinnerYear.setAdapter(yearAdapter);
        if (selectedYear != null) {
            sheetBinding.spinnerYear.setText(String.valueOf(selectedYear), false);
        }

        sheetBinding.spinnerYear.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            selectedYear = selected == null ? null : Integer.parseInt(selected);
        });

        sheetBinding.spinnerYear.setOnClickListener(v -> sheetBinding.spinnerYear.showDropDown());

        // Setup Country Spinner
        List<CountryUtils.Country> countries = CountryUtils.countries();

        ArrayAdapter<CountryUtils.Country> countryAdapter = new ArraySearchAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>(countries),
                country -> country.name() + " (" + country.code() + ")"
        ) {

        };
        sheetBinding.spinnerCountry.setAdapter(countryAdapter);

        if (selectedCountryCode != null) {
            String countryName = CountryUtils.displayCountry(selectedCountryCode);
            sheetBinding.spinnerCountry.setText(countryName, false);
        }

        sheetBinding.spinnerCountry.setOnItemClickListener((parent, view, position, id) -> {
            CountryUtils.Country selected = (CountryUtils.Country) parent.getItemAtPosition(position);
            selectedCountryCode = selected.code();
        });

        sheetBinding.spinnerCountry.setOnClickListener(v -> sheetBinding.spinnerCountry.showDropDown());

        // Setup Runtime RecyclerView
        List<RuntimeFilter> runtimeFilters = discoveryStrategy.getRuntimeFilters();
        RuntimeFilter currentRuntimeFilter = runtimeFilters.stream()
                .filter(f -> Objects.equals(f.min(), selectedRuntimeGte) && Objects.equals(f.max(), selectedRuntimeLte))
                .findFirst()
                .orElse(null);

        RuntimeChipAdapter runtimeAdapter = new RuntimeChipAdapter(filter -> {
            if (filter != null) {
                selectedRuntimeGte = filter.min();
                selectedRuntimeLte = filter.max();
            } else {
                selectedRuntimeGte = null;
                selectedRuntimeLte = null;
            }
        }, currentRuntimeFilter);
        sheetBinding.recyclerRuntime.setAdapter(runtimeAdapter);
        runtimeAdapter.submitList(runtimeFilters);

        // Setup Actions
        sheetBinding.btnReset.setOnClickListener(v -> {
            type = DiscoveryType.POPULAR;
            selectedGenreIds.clear();
            selectedYear = null;
            selectedRuntimeGte = null;
            selectedRuntimeLte = null;
            selectedCountryCode = null;

            // Refresh UI in bottom sheet
            sheetBinding.chipPopular.setChecked(true);
            List<FilterItem> resetItems = availableGenres.stream()
                    .map(g -> new FilterItem(g.getId(), g.getName(), false))
                    .collect(Collectors.toList());
            genreAdapter.submitList(resetItems);
            sheetBinding.spinnerYear.setText(null, false);
            sheetBinding.spinnerCountry.setText(null, false);
            runtimeAdapter.clearSelection();
        });

        sheetBinding.btnApply.setOnClickListener(v -> {
            pushStateToViewModel();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setupFilters() {
        if (fragmentType == FragmentType.MOVIE) {
            binding.chipMovies.setChecked(true);
        } else {
            binding.chipSeries.setChecked(true);
        }

        binding.chipGroupType.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chip_movies) && fragmentType != FragmentType.MOVIE) {
                updateFragmentType(FragmentType.MOVIE);
            } else if (checkedIds.contains(R.id.chip_series) && fragmentType != FragmentType.SERIE) {
                updateFragmentType(FragmentType.SERIE);
            }
        });

        binding.btnAddFilters.setOnClickListener(v -> showFiltersBottomSheet());
    }

    private void updateFragmentType(FragmentType newType) {
        setupState(newType);
        replaceDiscoveryFragment();
        observeGenres();
    }

    private void pushStateToViewModel() {
        this.discoveryViewModel.setFilter(
                new DiscoveryFilter(type, mergeFilters(type, selectedGenreIds, selectedYear, selectedRuntimeGte, selectedRuntimeLte, selectedCountryCode)),
                fragmentType
        );
    }

    private DiscoverFilters mergeFilters(
            DiscoveryType type,
            List<Integer> genreIds,
            Integer year,
            Integer runtimeGte,
            Integer runtimeLte,
            String countryCode
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
        builder.withOriginCountry(countryCode);

        return builder.build();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
