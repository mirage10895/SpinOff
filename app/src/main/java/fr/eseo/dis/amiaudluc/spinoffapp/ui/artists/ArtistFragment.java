package fr.eseo.dis.amiaudluc.spinoffapp.ui.artists;

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

import com.squareup.picasso.Picasso;

import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.FragmentArtistBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MoviesAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SeriesAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.ArtistViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistFragment extends Fragment implements ItemInterface {

    private static final String ARG_ARTIST_ID = "artist_id";

    private FragmentArtistBinding binding;
    private ArtistViewModel artistViewModel;

    private ArtistFragment() {
        // Required empty public constructor
    }

    public static ArtistFragment newInstance(int artistId) {
        ArtistFragment fragment = new ArtistFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ARTIST_ID, artistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentArtistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        artistViewModel = new ViewModelProvider(requireActivity()).get(ArtistViewModel.class);
        
        setupRecyclerViews();
        observeViewModel();
    }

    private void setupRecyclerViews() {
        binding.mediaMain.recyclerMovies.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.mediaMain.recyclerMovies.setHasFixedSize(true);

        binding.mediaMain.recyclerSeries.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.mediaMain.recyclerSeries.setHasFixedSize(true);
        
        binding.mediaMain.artistsLayer.setVisibility(View.GONE);
    }

    private void observeViewModel() {
        artistViewModel.getArtist().observe(getViewLifecycleOwner(), this::updateUI);
    }

    private void updateUI(Artist artist) {
        if (artist == null || binding == null) return;

        String link = getString(R.string.base_url_poster_500) + artist.getProfilePath();
        Picasso.get()
                .load(link)
                .fit()
                .centerCrop()
                .error(R.drawable.ic_unknown)
                .into(binding.posterIc);

        binding.name.setText(artist.getName());

        if (artist.getMovies() != null && artist.getMovies().getCast() != null && !artist.getMovies().getCast().isEmpty()) {
            binding.mediaMain.moviesLayer.setVisibility(View.VISIBLE);
            binding.mediaMain.recyclerMovies.setAdapter(new MoviesAdapter(
                    requireContext(),
                    this,
                    artist.getMovies().getCast().stream()
                            .map(Movie::toAdapterFormat)
                            .collect(Collectors.toList()),
                    true
            ));
        } else {
            binding.mediaMain.moviesLayer.setVisibility(View.GONE);
        }

        if (artist.getSeries() != null && artist.getSeries().getCast() != null && !artist.getSeries().getCast().isEmpty()) {
            binding.mediaMain.seriesLayer.setVisibility(View.VISIBLE);
            binding.mediaMain.recyclerSeries.setAdapter(new SeriesAdapter(
                    requireContext(),
                    this,
                    artist.getSeries().getCast().stream()
                            .map(Serie::toAdapterFormat)
                            .collect(Collectors.toList()),
                    true
            ));
        } else {
            binding.mediaMain.seriesLayer.setVisibility(View.GONE);
        }

        binding.whoAreYou.setText(artist.getBiography() != null ? artist.getBiography() : getString(R.string.emptyField));

        if (artist.getBirthday() != null) {
            String birthDate = DateUtils.toDisplayString(artist.getBirthday());
            String birthPlace = artist.getPlaceOfBirth() != null ? artist.getPlaceOfBirth() : "";
            String stringBuilder = birthDate + (birthPlace.isEmpty() ? "" : " (" + birthPlace + ")");
            binding.birthday.setText(stringBuilder);
        } else {
            binding.birthday.setText(R.string.emptyField);
        }

        binding.seeMore.setText(artist.getHomepage() != null ? artist.getHomepage() : getString(R.string.emptyField));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(Integer id, FragmentType type) {
        if (type == null) return;
        
        if (type == FragmentType.MOVIE) {
            Intent intent = new Intent(requireContext(), MovieActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        } else if (type == FragmentType.SERIE) {
            Intent intent = new Intent(requireContext(), SerieActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }

    @Override
    public void onRegisterContextMenu(View view, Integer id) {
        // unused
    }
}
