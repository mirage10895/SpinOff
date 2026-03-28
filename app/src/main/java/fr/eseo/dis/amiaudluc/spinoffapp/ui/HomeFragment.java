package fr.eseo.dis.amiaudluc.spinoffapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.FragmentHomeBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.PopularMoviesFragment;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private final BaseFragment popularFragment;
    private final BaseFragment topRatedFragment;
    private final BaseFragment onAirFragment;

    private HomeFragment(
            BaseFragment popularFragment,
            BaseFragment topRatedFragment,
            BaseFragment onAirFragment
    ) {
        this.popularFragment = popularFragment;
        this.topRatedFragment = topRatedFragment;
        this.onAirFragment = onAirFragment;
    }

     public static HomeFragment newInstance(
            BaseFragment popularFragment,
            BaseFragment topRatedFragment,
            BaseFragment onAirFragment
    ) {

        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment(
                popularFragment,
                topRatedFragment,
                onAirFragment
        );
        fragment.setArguments(args);
        return fragment;
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

        binding.textWelcome.setText("Discover");

        if (savedInstanceState == null) {
            binding.chipPopular.setChecked(true);
            switchFragment(new PopularMoviesFragment());
        }
        binding.chipPopular.setOnClickListener(chipView -> switchFragment(popularFragment));
        binding.chipTopRated.setOnClickListener(chipView -> switchFragment(topRatedFragment));
        binding.chipOnAir.setOnClickListener(chipView -> switchFragment(onAirFragment));

        // Setup RecyclerViews with dummy or actual data logic later
        // binding.recyclerContinueWatching.setAdapter(...)
        // binding.recyclerPopular.setAdapter(...)
    }

    private void switchFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                // L'utilisation de replace() supprime l'ancien fragment automatiquement
                .replace(R.id.discover_fragment_container, fragment)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
