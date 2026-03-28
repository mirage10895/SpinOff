package fr.eseo.dis.amiaudluc.spinoffapp.ui.episode;

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
import fr.eseo.dis.amiaudluc.databinding.FragmentEpisodeBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ActorsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SerieViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class EpisodeFragment extends Fragment implements SearchInterface {

    private static final String ARG_SERIE_ID = "serie_id";
    private static final String ARG_SEASON_NUMBER = "season_number";
    private static final String ARG_EPISODE_NUMBER = "episode_number";

    private FragmentEpisodeBinding binding;
    private SerieViewModel serieViewModel;
    private FragmentType type;

    public EpisodeFragment() {
        // Required empty public constructor
    }

    public static EpisodeFragment newInstance(int serieId, int seasonNumber, int episodeNumber) {
        EpisodeFragment fragment = new EpisodeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SERIE_ID, serieId);
        args.putInt(ARG_SEASON_NUMBER, seasonNumber);
        args.putInt(ARG_EPISODE_NUMBER, episodeNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEpisodeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        serieViewModel = new ViewModelProvider(requireActivity()).get(SerieViewModel.class);
        
        setupRecyclerView();
        observeViewModel();
    }

    private void setupRecyclerView() {
        binding.guestStars.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.guestStars.setHasFixedSize(true);
    }

    private void observeViewModel() {
        serieViewModel.getEpisode().observe(getViewLifecycleOwner(), this::updateUI);
    }

    private void updateUI(Episode episode) {
        if (episode == null || binding == null) return;

        if (episode.getAirDate() != null) {
            binding.airDate.setText(DateUtils.toDisplayString(episode.getAirDate()));
        } else {
            binding.airDate.setText(R.string.emptyField);
        }

        if (episode.getSeasonNumber() != -1) {
            binding.numberOfSeason.setText(String.valueOf(episode.getSeasonNumber()));
        } else {
            binding.numberOfSeason.setText(R.string.emptyField);
        }

        if (episode.getEpisodeNumber() != -1) {
            binding.episodes.setText(DateUtils.displayDuration(episode.getRuntime()));
        } else {
            binding.episodes.setText(R.string.emptyField);
        }

        if (episode.getOverview() != null && !episode.getOverview().isEmpty()) {
            binding.overview.setText(episode.getOverview());
        } else {
            binding.overview.setText(R.string.emptyField);
        }

        if (episode.getGuestStars() != null && !episode.getGuestStars().isEmpty()) {
            binding.layerGuest.setVisibility(View.VISIBLE);
            binding.guestStars.setAdapter(new ActorsAdapter(requireContext(), this, episode.getGuestStars()));
        } else {
            binding.layerGuest.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void setType(FragmentType type) {
        this.type = type;
    }

    @Override
    public void onItemClick(Integer id) {
        if (this.type == FragmentType.ACTOR) {
            Intent intent = new Intent(requireContext(), ArtistActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }

    @Override
    public void onRegisterContextMenu(View view, Integer id) {
        // unused
    }
}
