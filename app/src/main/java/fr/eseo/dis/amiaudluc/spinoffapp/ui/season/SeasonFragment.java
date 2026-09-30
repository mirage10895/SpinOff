package fr.eseo.dis.amiaudluc.spinoffapp.ui.season;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.FragmentSeasonBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Video;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.EpisodeDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SeasonDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ActorsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.WatchedToggleListener;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.YoutubeConnector;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.episode.EpisodeActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.episode.EpisodeItem;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.episode.EpisodesAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.VideoUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.SerieViewModel;

public class SeasonFragment extends Fragment implements ItemInterface, WatchedToggleListener {

    private static final String ARG_SERIE_ID = "serie_id";
    private static final String ARG_SEASON_NUMBER = "season_number";

    private FragmentSeasonBinding binding;
    private SerieViewModel serieViewModel;
    private YoutubeConnector youtubeConnector;
    private EpisodesAdapter episodesAdapter;

    private int serieId;
    private int seasonNumber;

    private boolean serieInLibrary = false;
    private boolean seasonLoaded = false;
    private final List<Episode> apiEpisodes = new ArrayList<>();
    private SeasonDatabase seasonState;
    private final Set<Integer> watchedEpisodeNumbers = new HashSet<>();

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
        setupChip();
        setupYoutubePlayer();
        observeViewModel();
    }

    private void setupChip() {
        binding.chipWatched.setOnClickListener(v ->
                serieViewModel.setSeasonWatched(serieId, seasonNumber, binding.chipWatched.isChecked()));
    }

    private void setupYoutubePlayer() {
        this.youtubeConnector = new YoutubeConnector(binding.youtube.youtubePlayerView, getLifecycle());
    }

    private void setupRecyclerViews() {
        binding.episodesRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.episodesRecycler.setHasFixedSize(false);
        binding.episodesRecycler.setNestedScrollingEnabled(true);

        this.episodesAdapter = new EpisodesAdapter(this, this);
        binding.episodesRecycler.setAdapter(this.episodesAdapter);

        binding.guestStars.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.guestStars.setHasFixedSize(true);
    }

    private void observeViewModel() {
        serieViewModel.getSeason().observe(getViewLifecycleOwner(), this::updateUI);
        serieViewModel.getSeasonState(serieId, seasonNumber).observe(getViewLifecycleOwner(), this::updateSeasonState);
        serieViewModel.getEpisodesBySerieAndSeason(serieId, seasonNumber).observe(getViewLifecycleOwner(), this::updateEpisodeStates);
        serieViewModel.getDatabaseSeries().observe(getViewLifecycleOwner(), series -> {
            serieInLibrary = series.stream().anyMatch(s -> s.getId() == serieId);
            updateControls();
            submitEpisodeItems();
        });
    }

    private void updateUI(Season season) {
        if (season == null || binding == null) return;

        seasonLoaded = true;

        if (season.getAirDate() != null) {
            binding.airDate.setText(DateUtils.toDisplayString(season.getAirDate()));
        } else {
            binding.airDate.setText(R.string.emptyField);
        }

        binding.numberOfSeason.setText(season.getSeasonNumber() != null ? String.valueOf(season.getSeasonNumber()) : "0");

        int runtime = season.getEpisodes().size() * Season.computeEpisodesAverageRuntime(season);
        if (runtime != 0) {
            binding.runtime.setText(DateUtils.displayDuration(Duration.ofMinutes(runtime)));
        } else {
            binding.runtime.setText(getString(R.string.emptyField));
        }

        apiEpisodes.clear();
        if (season.getEpisodes() != null) {
            apiEpisodes.addAll(season.getEpisodes());
        }
        submitEpisodeItems();

        // Keep the season episode count up to date (never stores unwatched episodes)
        serieViewModel.ensureSeasonMetadata(serieId, seasonNumber, apiEpisodes.size());

        if (season.getOverview() != null && !season.getOverview().isEmpty()) {
            binding.overview.setText(season.getOverview());
        } else {
            binding.overview.setVisibility(View.GONE);
        }

        // YouTube Trailer
        Video trailer = VideoUtils.getYoutubeTrailer(season.getVideos());
        if (trailer != null) {
            String newVideoId = trailer.getKey();
            binding.youtube.teaserTxt.setVisibility(View.VISIBLE);
            binding.youtube.youtubeCard.setVisibility(View.VISIBLE);
            this.youtubeConnector.loadVideo(newVideoId);
        } else {
            binding.youtube.teaserTxt.setVisibility(View.GONE);
            binding.youtube.youtubeCard.setVisibility(View.GONE);
        }

        if (season.getCredits() != null && season.getCredits().getCast() != null) {
            binding.guestStars.setAdapter(new ActorsAdapter(requireContext(), this, season.getCredits().getCast()));
        }

        updateControls();
    }

    private void updateSeasonState(SeasonDatabase seasonState) {
        this.seasonState = seasonState;
        updateControls();
        submitEpisodeItems();
    }

    private void updateEpisodeStates(List<EpisodeDatabase> episodes) {
        watchedEpisodeNumbers.clear();
        if (episodes != null) {
            // Every stored row means the episode has been watched
            for (EpisodeDatabase episode : episodes) {
                watchedEpisodeNumbers.add(episode.getEpisodeNumber());
            }
        }
        submitEpisodeItems();
    }

    private boolean isEpisodeWatched(Episode episode) {
        if (episode.getEpisodeNumber() == null) {
            return false;
        }
        if (seasonState != null && seasonState.isWatched()) {
            return true;
        }
        return watchedEpisodeNumbers.contains(episode.getEpisodeNumber());
    }

    private void submitEpisodeItems() {
        List<EpisodeItem> items = new ArrayList<>();
        for (Episode episode : apiEpisodes) {
            items.add(new EpisodeItem(episode, isEpisodeWatched(episode), serieInLibrary));
        }
        this.episodesAdapter.submitList(items);
    }

    private void updateControls() {
        if (binding == null) return;
        boolean visible = serieInLibrary && seasonLoaded;
        binding.chipWatched.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.chipWatched.setChecked(seasonState != null && seasonState.isWatched());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(Integer id, FragmentType type) {
        if (type == null) return;

        if (type == FragmentType.ARTIST) {
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
    public void onWatchedToggle(int id, boolean watched) {
        serieViewModel.setEpisodeWatched(serieId, seasonNumber, id, watched);
    }

    @Override
    public void onRegisterContextMenu(View view, Integer id) {
        // unused
    }
}
