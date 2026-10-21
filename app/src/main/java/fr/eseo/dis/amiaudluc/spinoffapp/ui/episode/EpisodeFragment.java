package fr.eseo.dis.amiaudluc.spinoffapp.ui.episode;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.FragmentEpisodeBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.EpisodeDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SeasonDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ActorsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.SerieViewModel;

/**
 * A simple {@link Fragment} subclass.
 * <p>
 * Carries the same "watched" chip as the season screen: it is shown once the serie is in the
 * library, reflects the episode watched state — the whole season being watched implies every
 * episode is — and toggles it through the view model.
 */
public class EpisodeFragment extends Fragment implements ItemInterface {

    private static final String ARG_SERIE_ID = "serie_id";
    private static final String ARG_SEASON_NUMBER = "season_number";
    private static final String ARG_EPISODE_NUMBER = "episode_number";

    private FragmentEpisodeBinding binding;
    private SerieViewModel serieViewModel;

    private int serieId;
    private int seasonNumber;
    private int episodeNumber;

    private boolean serieInLibrary = false;
    private boolean episodeLoaded = false;
    private SeasonDatabase seasonState;
    private final Set<Integer> watchedEpisodeNumbers = new HashSet<>();

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            serieId = getArguments().getInt(ARG_SERIE_ID);
            seasonNumber = getArguments().getInt(ARG_SEASON_NUMBER);
            episodeNumber = getArguments().getInt(ARG_EPISODE_NUMBER);
        }
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
        setupChip();
        observeViewModel();
    }

    private void setupRecyclerView() {
        binding.guestStars.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.guestStars.setHasFixedSize(true);
    }

    private void setupChip() {
        binding.chipWatched.setOnClickListener(v -> serieViewModel.setEpisodeWatched(
                serieId, seasonNumber, episodeNumber, binding.chipWatched.isChecked()));
    }

    private void observeViewModel() {
        serieViewModel.getEpisode().observe(getViewLifecycleOwner(), this::updateUI);
        serieViewModel.getSeasonState(serieId, seasonNumber).observe(getViewLifecycleOwner(), this::updateSeasonState);
        serieViewModel.getEpisodesBySerieAndSeason(serieId, seasonNumber).observe(getViewLifecycleOwner(), this::updateEpisodeStates);
        serieViewModel.getDatabaseSeries().observe(getViewLifecycleOwner(), series -> {
            serieInLibrary = series.stream().anyMatch(s -> s.getId() == serieId);
            updateControls();
        });
    }

    private void updateUI(Episode episode) {
        if (episode == null || binding == null) return;

        episodeLoaded = true;

        if (episode.getAirDate() != null) {
            binding.airDate.setText(DateUtils.toDisplayString(episode.getAirDate()));
        } else {
            binding.airDate.setText(R.string.emptyField);
        }

        if (episode.getSeasonNumber() != null) {
            binding.numberOfSeason.setText(String.valueOf(episode.getSeasonNumber()));
        } else {
            binding.numberOfSeason.setText(R.string.emptyField);
        }

        if (episode.getRuntime() != null) {
            binding.episodes.setText(DateUtils.displayDuration(Duration.ofMinutes(episode.getRuntime())));
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

        updateControls();
    }

    private void updateSeasonState(SeasonDatabase seasonState) {
        this.seasonState = seasonState;
        updateControls();
    }

    private void updateEpisodeStates(List<EpisodeDatabase> episodes) {
        watchedEpisodeNumbers.clear();
        if (episodes != null) {
            // Every stored row means the episode has been watched
            for (EpisodeDatabase episode : episodes) {
                watchedEpisodeNumbers.add(episode.getEpisodeNumber());
            }
        }
        updateControls();
    }

    /** An episode reads as watched when the whole season is, or when its own row is stored. */
    private boolean isEpisodeWatched() {
        if (seasonState != null && seasonState.isWatched()) {
            return true;
        }
        return watchedEpisodeNumbers.contains(episodeNumber);
    }

    private void updateControls() {
        if (binding == null) return;
        boolean visible = serieInLibrary && episodeLoaded;
        binding.chipWatched.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.chipWatched.setChecked(isEpisodeWatched());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(Integer id, FragmentType type) {
        if (type == FragmentType.ARTIST) {
            Intent intent = new Intent(requireContext(), ArtistActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }
}
