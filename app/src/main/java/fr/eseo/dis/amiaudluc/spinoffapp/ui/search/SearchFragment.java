package fr.eseo.dis.amiaudluc.spinoffapp.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.databinding.FragmentSearchMainBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MoviesAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SeriesAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SearchViewModel;

/**
 * A fragment representing a search result list.
 */
public class SearchFragment extends Fragment implements SearchInterface {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private FragmentSearchMainBinding binding;
    private SearchViewModel searchViewModel;
    private MoviesAdapter moviesAdapter;
    private SeriesAdapter seriesAdapter;
    private ArtistsAdapter artistsAdapter;
    private FragmentType type;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(int columnCount) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
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

        moviesAdapter = new MoviesAdapter(requireContext(), this, new ArrayList<>(), true);
        seriesAdapter = new SeriesAdapter(requireContext(), this, new ArrayList<>(), true);
        artistsAdapter = new ArtistsAdapter(requireContext(), this, new ArrayList<>());

        binding.recyclerMovies.setAdapter(moviesAdapter);
        binding.recyclerSeries.setAdapter(seriesAdapter);
        binding.recyclerArtists.setAdapter(artistsAdapter);
    }

    private void setupLayoutManager(androidx.recyclerview.widget.RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), mColumnCount));
        }
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

        moviesAdapter.setMovies(movies.stream().map(Movie::toAdapterFormat).collect(Collectors.toList()));
        seriesAdapter.setSeries(series.stream().map(Serie::toAdapterFormat).collect(Collectors.toList()));
        artistsAdapter.setArtist(artists);
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
        if (this.type == null) return;
        
        Intent intent = null;
        switch (this.type) {
            case MOVIE:
                intent = new Intent(requireContext(), MovieActivity.class);
                break;
            case SERIE:
                intent = new Intent(requireContext(), SerieActivity.class);
                break;
            case ARTIST:
                intent = new Intent(requireContext(), ArtistActivity.class);
                break;
        }

        if (intent != null) {
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer position) {
    }
}
