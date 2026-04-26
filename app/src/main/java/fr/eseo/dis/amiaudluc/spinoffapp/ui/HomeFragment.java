package fr.eseo.dis.amiaudluc.spinoffapp.ui;

import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.FragmentHomeBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.TmdbApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.DiscoverFilters;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.MovieType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.SerieType;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Genre;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieDiscoveryFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieDiscoveryFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.DiscoveryViewModel;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryFilter;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryType;

public class HomeFragment extends Fragment {

    private static final String ARG_TYPE = "fragment_type";
    private FragmentHomeBinding binding;

    private FragmentType fragmentType;
    private DiscoveryViewModel discoveryViewModel;
    private DiscoveryType type = DiscoveryType.POPULAR;

    private Integer selectedGenreId = null;
    private Integer selectedYear = null;


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
                if (filters.withGenres() != null) {
                    selectedGenreId = Integer.parseInt(filters.withGenres());
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
            repository.getMovieGenres().observe(getViewLifecycleOwner(), this::updateGenreSpinner);
        } else {
            repository.getSerieGenres().observe(getViewLifecycleOwner(), this::updateGenreSpinner);
        }

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

    private void updateGenreSpinner(List<Genre> genres) {
        if (genres == null) return;
        List<String> genreNames = new ArrayList<>();
        String allGenres = getString(R.string.filter_all_genres);
        genreNames.add(allGenres);
        genreNames.addAll(
                genres.stream()
                        .map(Genre::getName)
                        .collect(Collectors.toList())
        );

        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, genreNames);
        binding.spinnerGenre.setAdapter(genreAdapter);
        if (selectedGenreId != null) {
            binding.spinnerGenre.setText(
                    genres.stream()
                            .filter(g -> g.getId() == selectedGenreId)
                            .findFirst()
                            .map(Genre::getName)
                            .orElse(null), false
            );
        }
        binding.spinnerGenre.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            if (allGenres.equals(selected)) {
                selectedGenreId = null;
            } else {
                selectedGenreId = genres.stream()
                        .filter(g -> g.getName().equals(selected))
                        .findFirst()
                        .map(Genre::getId)
                        .orElse(null);
            }
            pushStateToViewModel();
        });
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

        if (presetFilters.withGenres() != null) {
            selectedGenreId = Integer.parseInt(presetFilters.withGenres());
        } else {
            selectedGenreId = null;
        }

        selectedYear = fragmentType == FragmentType.MOVIE ?
                presetFilters.primaryReleaseYear() :
                presetFilters.firstAirDateYear();

        // Update UI Spinners
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
        TmdbApiRepository repository = TmdbApiRepository.getInstance();
        LiveData<List<Genre>> genresLiveData = fragmentType == FragmentType.MOVIE ?
                repository.getMovieGenres() :
                repository.getSerieGenres();

        List<Genre> genres = genresLiveData.getValue();
        if (genres != null) {
            if (selectedGenreId != null) {
                binding.spinnerGenre.setText(
                        genres.stream()
                                .filter(g -> g.getId() == (int) selectedGenreId)
                                .findFirst()
                                .map(Genre::getName)
                                .orElse(getString(R.string.filter_all_genres)), false
                );
            } else {
                binding.spinnerGenre.setText(getString(R.string.filter_all_genres), false);
            }
        }

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

        if (selectedGenreId != null) {
            builder.withGenres(String.valueOf(selectedGenreId));
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
