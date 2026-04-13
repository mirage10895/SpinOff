package fr.eseo.dis.amiaudluc.spinoffapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.FragmentHomeBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieDiscoveryFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieDiscoveryFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.beans.DiscoveryType;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.DiscoveryViewModel;

public class HomeFragment extends Fragment {

    private static final String ARG_TYPE = "fragment_type";
    private FragmentHomeBinding binding;

    private FragmentType fragmentType;
    private DiscoveryViewModel discoveryViewModel;
    private DiscoveryType type = DiscoveryType.POPULAR;


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
        DiscoveryType savedType = this.discoveryViewModel.getFilter(fragmentType).getValue();
        if (savedType != null) {
            this.type = savedType;
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
        this.discoveryViewModel.setFilter(type, fragmentType);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
