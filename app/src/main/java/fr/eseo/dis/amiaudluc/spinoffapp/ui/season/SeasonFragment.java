package fr.eseo.dis.amiaudluc.spinoffapp.ui.season;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.FragmentSeasonBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ActorsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.episode.EpisodeActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.episode.EpisodesAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SerieViewModel;

public class SeasonFragment extends Fragment implements ItemInterface {

    private static final String ARG_SERIE_ID = "serie_id";
    private static final String ARG_SEASON_NUMBER = "season_number";

    private FragmentSeasonBinding binding;
    private SerieViewModel serieViewModel;
    private int serieId;
    private int seasonNumber;

    private SeasonFragment() {
        // Required empty public constructor
    }

    public static SeasonFragment newInstance(int serieId, int seasonNumber) {
        SeasonFragment fragment = new SeasonFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SERIE_ID, serieId);
        args.putInt(ARG_SEASON_NUMBER, seasonNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            serieId = getArguments().getInt(ARG_SERIE_ID);
            seasonNumber = getArguments().getInt(ARG_SEASON_NUMBER);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSeasonBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        serieViewModel = new ViewModelProvider(requireActivity()).get(SerieViewModel.class);
        
        setupRecyclerViews();
        observeViewModel();
    }

    private void setupRecyclerViews() {
        binding.episodesRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.episodesRecycler.setHasFixedSize(true);
        binding.episodesRecycler.setNestedScrollingEnabled(false);

        binding.guestStars.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.guestStars.setHasFixedSize(true);
    }

    private void observeViewModel() {
        serieViewModel.getSeason().observe(getViewLifecycleOwner(), this::updateUI);
    }

    private void updateUI(Season season) {
        if (season == null || binding == null) return;

        // CircularImageBar for Air Date (Year)
        binding.airDate.setText(DateUtils.toDisplayString(season.getAirDate()));

        binding.numberOfSeason.setText(season.getSeasonNumber() != null ? String.valueOf(season.getSeasonNumber()) : "0");
        
        if (season.getEpisodes() != null) {
            binding.episodes.setText(String.valueOf(season.getEpisodes().size()));
            binding.episodesRecycler.setAdapter(new EpisodesAdapter(requireContext(), this, season.getEpisodes()));
        } else {
            binding.episodes.setText("0");
        }

        if (season.getOverview() != null && !season.getOverview().isEmpty()) {
            binding.overview.setTextColor(requireContext().getColor(R.color.white));
            binding.overview.setText(season.getOverview());
        } else {
            binding.overview.setText(R.string.emptyField);
        }

        if (season.getCredits() != null && season.getCredits().getCast() != null) {
            binding.guestStars.setAdapter(new ActorsAdapter(requireContext(), this, season.getCredits().getCast()));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(Integer id, FragmentType type) {
        if (type == null) return;
        
        if (type == FragmentType.ACTOR) {
            Intent intent = new Intent(requireContext(), ArtistActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        } else if (type == FragmentType.EPISODE) {
            Intent intent = new Intent(requireContext(), EpisodeActivity.class);
            intent.putExtra("serieId", serieId);
            intent.putExtra("seasonNumber", seasonNumber);
            intent.putExtra("episodeNumber", id);
            startActivity(intent);
        }
    }

    @Override
    public void onRegisterContextMenu(View view, Integer id) {
        // unused
    }
}
