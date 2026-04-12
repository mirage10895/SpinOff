package fr.eseo.dis.amiaudluc.spinoffapp.ui.series;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.FragmentSingleSerieBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Genre;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Video;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.CircularImageBar;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.MediaAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.YoutubeConnector;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter.WatchProviderAdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.networks.WatchProviderAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.season.SeasonActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.season.SeasonsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.VideoUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.SerieViewModel;

public class SingleSerieFragment extends Fragment implements ItemInterface {

    private static final String ARG_SERIE_ID = "serie_id";

    private FragmentSingleSerieBinding binding;
    private SerieViewModel serieViewModel;
    private SeasonsAdapter seasonsAdapter;
    private ArtistsAdapter artistsAdapter;
    private WatchProviderAdapter networksAdapter;
    private MediaAdapter mediaAdapter;
    private WatchProviderAdapter watchProviderAdapter;
    private YoutubeConnector youtubeConnector;
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
        setupChips();
        setupYoutubePlayer();
        observeViewModel();
    }

    private void setupChips() {
        binding.layoutChips.chipInLibrary.setOnClickListener(v -> {
            if (binding.layoutChips.chipInLibrary.isChecked()) {
                serieViewModel.insert(serieId);
            } else {
                serieViewModel.deleteById(serieId);
            }
        });

        binding.layoutChips.chipWatched.setOnClickListener(v -> serieViewModel.toggleSerieIsWatched(serieId));
    }

    private void setupRecyclerViews() {
        binding.seasons.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.seasons.setHasFixedSize(false);
        binding.seasons.setNestedScrollingEnabled(false);
        this.seasonsAdapter = new SeasonsAdapter(requireContext(), this, new ArrayList<>());
        binding.seasons.setAdapter(this.seasonsAdapter);

        binding.realisators.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.realisators.setHasFixedSize(false);
        this.artistsAdapter = new ArtistsAdapter(requireContext(), this, new ArrayList<>());
        binding.realisators.setAdapter(this.artistsAdapter);


        binding.networks.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.networks.setHasFixedSize(false);
        this.networksAdapter = WatchProviderAdapter.newInstance(getString(R.string.base_url_poster_300));
        binding.networks.setAdapter(this.networksAdapter);

        binding.watchProviders.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.watchProviders.setHasFixedSize(false);
        this.watchProviderAdapter = WatchProviderAdapter.newInstance(getString(R.string.base_url_poster_300));
        binding.watchProviders.setAdapter(this.watchProviderAdapter);


        binding.recyclerRecommendations.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerRecommendations.setHasFixedSize(false);
        this.mediaAdapter = new MediaAdapter(requireContext(), this, new ArrayList<>(), true);
        binding.recyclerRecommendations.setAdapter(this.mediaAdapter);
    }

    private void setupYoutubePlayer() {
        this.youtubeConnector = new YoutubeConnector(binding.youtube.youtubePlayerView, getLifecycle());
    }

    private void observeViewModel() {
        serieViewModel.getSerie().observe(getViewLifecycleOwner(), this::updateUI);
        serieViewModel.getSerieWatchProviders().observe(getViewLifecycleOwner(), this::updateWatchProviderUI);
        serieViewModel.getDatabaseSeries().observe(getViewLifecycleOwner(), series -> {
            Optional<SerieDatabase> serieDb = series.stream()
                    .filter(s -> s.getId() == serieId)
                    .findFirst();

            if (serieDb.isPresent()) {
                binding.layoutChips.chipInLibrary.setChecked(true);
                binding.layoutChips.chipWatched.setVisibility(View.VISIBLE);
                binding.layoutChips.chipWatched.setChecked(serieDb.get().isWatched());
            } else {
                binding.layoutChips.chipInLibrary.setChecked(false);
                binding.layoutChips.chipWatched.setVisibility(View.GONE);
                binding.layoutChips.chipWatched.setChecked(false);
            }
        });
    }

    private void updateUI(Serie serie) {
        if (serie == null) return;

        if (serie.getVoteAverage() != null) {
            binding.rate.setImageBitmap(CircularImageBar.buildNote(serie.getVoteAverage()));
        } else {
            binding.rate.setImageBitmap(CircularImageBar.buildNote(0));
        }

        if (serie.getFirstAirDate() != null) {
            binding.releaseDate.setText(DateUtils.toDisplayString(serie.getFirstAirDate()));
        } else {
            binding.releaseDate.setText(R.string.emptyField);
        }

        int totalRuntime = Serie.computeTotalRuntime(serie);
        if (totalRuntime != 0) {
            binding.runtime.setText(DateUtils.displayDuration(Duration.ofMinutes(totalRuntime)));
        } else {
            binding.runtime.setText(R.string.emptyField);
        }

        String numberOfSeason = serie.getNumberOfSeasons() != -1 ? String.valueOf(serie.getNumberOfSeasons()) : "0";
        binding.seasonText.setText(getString(R.string.number_seasons, numberOfSeason));

        if (serie.getGenres() != null && !serie.getGenres().isEmpty()) {
            binding.genres.setText(serie.getGenres().stream().map(Genre::getName).collect(Collectors.joining(", ")));
        } else {
            binding.genres.setText(R.string.emptyField);
        }

        binding.overview.setText(serie.getOverview() != null ? serie.getOverview() : getString(R.string.emptyField));

        // YouTube Trailer
        Video trailer = VideoUtils.getYoutubeTrailer(serie.getVideos());
        if (trailer != null) {
            String newVideoId = trailer.getKey();
            binding.youtube.teaserTxt.setVisibility(View.VISIBLE);
            binding.youtube.youtubeCard.setVisibility(View.VISIBLE);
            this.youtubeConnector.loadVideo(newVideoId);
        } else {
            binding.youtube.teaserTxt.setVisibility(View.GONE);
            binding.youtube.youtubeCard.setVisibility(View.GONE);
        }

        this.seasonsAdapter.submitList(serie.getSeasons());
        this.artistsAdapter.submitList(serie.getCreatedBy());
        this.networksAdapter.submitList(
                serie.getNetworks()
                        .stream()
                        .map(n -> new WatchProviderAdapterData(
                                n.getId(),
                                n.getName(),
                                n.getLogoPath(),
                                null
                        ))
                        .collect(Collectors.toList())
        );

        if (serie.getRecommendations() != null) {
            this.mediaAdapter.submitList(
                    serie.getRecommendations().getResults().stream()
                            .map(Movie::toAdapterFormat)
                            .collect(Collectors.toList())
            );
        }
    }

    private void updateWatchProviderUI(List<WatchProviderAdapterData> watchProviders) {
        if (watchProviders == null || watchProviders.isEmpty()) {
            binding.watchProviders.setVisibility(View.GONE);
            return;
        }

        binding.watchProviders.setVisibility(View.VISIBLE);
        this.watchProviderAdapter.submitList(
                watchProviders
                        .stream()
                        .limit(3)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(Integer id, FragmentType type) {
        if (type == null) return;

        switch (type) {
            // recommendations are movie, but still they are series at the end
            case MOVIE: {
                Intent intent = new Intent(requireContext(), SerieActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                break;
            }
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
}
