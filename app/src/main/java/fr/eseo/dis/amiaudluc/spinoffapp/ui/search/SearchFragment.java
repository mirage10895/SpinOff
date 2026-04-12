package fr.eseo.dis.amiaudluc.spinoffapp.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import fr.eseo.dis.amiaudluc.databinding.FragmentSearchMainBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.MediaAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SearchViewModel;

/**
 * A fragment representing a search result list.
 */
public class SearchFragment extends Fragment implements ItemInterface {
    private FragmentSearchMainBinding binding;
    private SearchViewModel searchViewModel;
    private MediaAdapter moviesAdapter;
    private MediaAdapter seriesAdapter;
    private ArtistsAdapter artistsAdapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);

        setupRecyclerViews();
        observeViewModel();
    }

    private void setupRecyclerViews() {
        setupLayoutManager(binding.recyclerMovies);
        setupLayoutManager(binding.recyclerSeries);
        setupLayoutManager(binding.recyclerArtists);

        moviesAdapter = new MediaAdapter(requireContext(), this, new ArrayList<>(), true);
        seriesAdapter = new MediaAdapter(requireContext(), this, new ArrayList<>(), true);
        artistsAdapter = new ArtistsAdapter(requireContext(), this, new ArrayList<>());

        binding.recyclerMovies.setAdapter(moviesAdapter);
        binding.recyclerSeries.setAdapter(seriesAdapter);
        binding.recyclerArtists.setAdapter(artistsAdapter);
    }

    private void setupLayoutManager(androidx.recyclerview.widget.RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void observeViewModel() {
        searchViewModel.getMedias().observe(getViewLifecycleOwner(), this::updateMedias);
    }

    private void updateMedias(List<Media> medias) {
        if (medias == null || binding == null) return;

        List<Movie> movies = new ArrayList<>();
        List<Serie> series = new ArrayList<>();
        List<Artist> artists = new ArrayList<>();

        for (Media media : medias) {
            switch (media.getMediaType()) {
                case Media.MOVIE:
                    movies.add((Movie) media);
                    break;
                case Media.SERIE:
                    series.add((Serie) media);
                    break;
                case Media.ARTIST:
                    artists.add((Artist) media);
                    break;
            }
        }

        binding.moviesLayer.setVisibility(movies.isEmpty() ? View.GONE : View.VISIBLE);
        binding.seriesLayer.setVisibility(series.isEmpty() ? View.GONE : View.VISIBLE);
        binding.artistsLayer.setVisibility(artists.isEmpty() ? View.GONE : View.VISIBLE);

        moviesAdapter.submitList(movies.stream().map(Movie::toAdapterFormat).collect(Collectors.toList()));
        seriesAdapter.submitList(series.stream().map(Serie::toAdapterFormat).collect(Collectors.toList()));
        artistsAdapter.setArtist(artists);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(Integer id, FragmentType type) {
        if (type == null) return;
        
        Intent intent = switch (type) {
            case MOVIE -> new Intent(requireContext(), MovieActivity.class);
            case SERIE -> new Intent(requireContext(), SerieActivity.class);
            case ARTIST -> new Intent(requireContext(), ArtistActivity.class);
            default -> null;
        };

        if (intent != null) {
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }
}
