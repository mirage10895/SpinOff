package fr.eseo.dis.amiaudluc.spinoffapp.ui.series;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.FragmentSingleSerieBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Genre;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.CircularImageBar;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MoviesAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.networks.NetworksAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.season.SeasonActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.season.SeasonsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SerieViewModel;

public class SingleSerieFragment extends Fragment implements SearchInterface {

    private static final String ARG_SERIE_ID = "serie_id";

    private FragmentSingleSerieBinding binding;
    private SerieViewModel serieViewModel;
    private FragmentType type;
    private int serieId;

    public SingleSerieFragment() {
        // Required empty public constructor
    }

    public static SingleSerieFragment newInstance(int serieId) {
        SingleSerieFragment fragment = new SingleSerieFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SERIE_ID, serieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            serieId = getArguments().getInt(ARG_SERIE_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSingleSerieBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel shared with Activity
        serieViewModel = new ViewModelProvider(requireActivity()).get(SerieViewModel.class);
        
        setupRecyclerViews();
        observeViewModel();
    }

    private void setupRecyclerViews() {
        binding.seasons.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.seasons.setHasFixedSize(true);
        binding.seasons.setNestedScrollingEnabled(false);

        binding.realisators.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.realisators.setHasFixedSize(true);

        binding.networks.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.networks.setHasFixedSize(true);

        binding.recyclerRecommendations.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerRecommendations.setHasFixedSize(true);
    }

    private void observeViewModel() {
        serieViewModel.getSerie().observe(getViewLifecycleOwner(), this::updateUI);
    }

    private void updateUI(Serie serie) {
        if (serie == null) return;

        binding.getRoot().setBackgroundColor(requireContext().getColor(R.color.color_primary_semi_opaq));

        if (serie.getVoteAverage() != -1) {
            binding.rate.setImageBitmap(CircularImageBar.buildNote(serie.getVoteAverage()));
        } else {
            binding.rate.setImageBitmap(CircularImageBar.buildNote(0));
        }

        if (serie.getOriginCountry() != null) {
            binding.language.setText(serie.getOriginCountry().stream().findFirst().orElse("N/A"));
        } else {
            binding.language.setText(R.string.emptyField);
        }

        binding.numberOfSeason.setText(serie.getNumberOfSeasons() != -1 ? String.valueOf(serie.getNumberOfSeasons()) : "0");

        if (serie.getGenres() != null && !serie.getGenres().isEmpty()) {
            binding.genres.setText(serie.getGenres().stream().map(Genre::getName).collect(Collectors.joining(", ")));
        } else {
            binding.genres.setText(R.string.emptyField);
        }

        binding.overview.setText(serie.getOverview() != null ? serie.getOverview() : getString(R.string.emptyField));

        binding.seasons.setAdapter(new SeasonsAdapter(requireContext(), this, serie.getSeasons()));
        binding.realisators.setAdapter(new ArtistsAdapter(requireContext(), this, serie.getCreatedBy()));
        binding.networks.setAdapter(new NetworksAdapter(requireContext(), this, serie.getNetworks()));

        if (serie.getRecommendations() != null) {
            binding.recyclerRecommendations.setAdapter(new MoviesAdapter(
                    requireContext(),
                    this,
                    serie.getRecommendations().getResults().stream()
                            .map(Movie::toAdapterFormat)
                            .collect(Collectors.toList()),
                    true
            ));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(Integer id) {
        if (type == null) return;
        
        switch (this.type) {
            case SEASON: {
                Intent intent = new Intent(requireContext(), SeasonActivity.class);
                intent.putExtra("serieId", serieId);
                intent.putExtra("seasonNumber", id);
                startActivity(intent);
                break;
            }
            case ARTIST: {
                Intent intent = new Intent(requireContext(), ArtistActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onRegisterContextMenu(View view, Integer id) {
        // unused
    }

    @Override
    public void setType(FragmentType type) {
        this.type = type;
    }
}
