package fr.eseo.dis.amiaudluc.spinoffapp.ui;

import android.os.Bundle;
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
import androidx.lifecycle.ViewModelProvider;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.FragmentHomeBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.TmdbApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.DiscoverFilters;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Genre;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieDiscoveryFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieDiscoveryFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryFilter;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.DiscoveryViewModel;

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
        years.add("All years");
        years.addAll(IntStream.rangeClosed(1900, LocalDate.now().getYear())
                .boxed()
                .sorted((a, b) -> b - a)
                .map(String::valueOf)
                .collect(Collectors.toList()));

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, years);
        binding.spinnerYear.setAdapter(yearAdapter);
        binding.spinnerYear.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            selectedYear = "All years".equals(selected) ? null : Integer.parseInt(selected);
            pushStateToViewModel();
        });
    }

    private void updateGenreSpinner(List<Genre> genres) {
        if (genres == null) return;
        List<String> genreNames = new ArrayList<>();
        genreNames.add("All genres");
        genreNames.addAll(genres.stream().map(Genre::getName).collect(Collectors.toList()));

        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, genreNames);
        binding.spinnerGenre.setAdapter(genreAdapter);
        binding.spinnerGenre.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            if ("All genres".equals(selected)) {
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
    }

    private void updatePresetFilter(DiscoveryType type) {
        this.type = type;
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
        DiscoverFilters.DiscoverFiltersBuilder extraBuilder = DiscoverFilters.builder();
        if (selectedGenreId != null) {
            extraBuilder.withGenres(String.valueOf(selectedGenreId));
        }
        if (selectedYear != null) {
            if (fragmentType == FragmentType.MOVIE) {
                extraBuilder.primaryReleaseYear(selectedYear);
            } else {
                extraBuilder.year(selectedYear);
            }
        }

        this.discoveryViewModel.setFilter(new DiscoveryFilter(type, extraBuilder.build()), fragmentType);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
