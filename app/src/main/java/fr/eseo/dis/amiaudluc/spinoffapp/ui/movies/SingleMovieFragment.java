package fr.eseo.dis.amiaudluc.spinoffapp.ui.movies;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;
import java.util.Optional;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.FragmentSingleMovieBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Genre;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ActorsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.CircularImageBar;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.MovieViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleMovieFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleMovieFragment extends Fragment implements ItemInterface {

    private static final String ARG_MOVIE_ID = "movie_id";

    private FragmentSingleMovieBinding binding;
    private MovieViewModel movieViewModel;

    private int movieId;

    public SingleMovieFragment() {
        // Required empty public constructor
    }

    public static SingleMovieFragment newInstance(int movieId) {
        SingleMovieFragment fragment = new SingleMovieFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MOVIE_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getInt(ARG_MOVIE_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSingleMovieBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel shared with Activity
        movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);

        setupRecyclerViews();
        setupChips();
        observeViewModel();
    }

    private void setupChips() {
        binding.layoutChips.chipInLibrary.setOnClickListener(v -> {
            if (binding.layoutChips.chipInLibrary.isChecked()) {
                movieViewModel.insert(movieId);
            } else {
                movieViewModel.deleteMovieById(movieId);
            }
        });

        binding.layoutChips.chipWatched.setOnClickListener(v -> {
            movieViewModel.toggleMovieIsWatched(movieId);
        });
    }


    private void setupRecyclerViews() {
        binding.realisators.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.realisators.setHasFixedSize(true);
        binding.realisators.setNestedScrollingEnabled(false);

        binding.cast.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.cast.setHasFixedSize(true);
        binding.cast.setNestedScrollingEnabled(false);

        binding.recyclerRecommendations.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerRecommendations.setHasFixedSize(true);
        binding.recyclerRecommendations.setNestedScrollingEnabled(false);
    }

    private void observeViewModel() {
        movieViewModel.getMovie().observe(getViewLifecycleOwner(), this::updateUI);
        movieViewModel.getDatabaseMovies().observe(getViewLifecycleOwner(), movies -> {
            Optional<MovieDatabase> movieDb = movies.stream()
                    .filter(m -> m.getId() == movieId)
                    .findFirst();

            if (movieDb.isPresent()) {
                binding.layoutChips.chipInLibrary.setChecked(true);
                binding.layoutChips.chipWatched.setVisibility(View.VISIBLE);
                binding.layoutChips.chipWatched.setChecked(movieDb.get().isWatched());
            } else {
                binding.layoutChips.chipInLibrary.setChecked(false);
                binding.layoutChips.chipWatched.setVisibility(View.GONE);
                binding.layoutChips.chipWatched.setChecked(false);
            }
        });
    }

    private void updateUI(Movie movie) {
        if (movie == null) return;

        binding.getRoot().setBackgroundColor(requireContext().getColor(R.color.color_primary_semi_opaq));

        // texts
        if (movie.getVoteAverage() != null) {
            binding.rate.setImageBitmap(CircularImageBar.buildNote(movie.getVoteAverage()));
        } else {
            binding.rate.setImageBitmap(CircularImageBar.buildNote(0));
        }

        if (movie.getRuntime() != null) {
            binding.runtime.setText(
                    DateUtils.displayDuration(movie.getRuntime())
            );
        } else {
            binding.runtime.setText(R.string.emptyField);
        }

        if (movie.getReleaseDate() != null) {
            binding.releaseDate.setText(DateUtils.toDisplayString(movie.getReleaseDate()));
        } else {
            binding.releaseDate.setText(R.string.emptyField);
        }

        if (!movie.getGenres().isEmpty()) {
            binding.genres.setText(
                    movie.getGenres()
                            .stream()
                            .map(Genre::getName)
                            .collect(Collectors.joining(", "))
            );
        } else {
            binding.genres.setText(R.string.emptyField);
        }

        if (movie.getOverview() != null) {
            binding.overview.setText(movie.getOverview());
        } else {
            binding.overview.setText(R.string.emptyField);
        }

        if (movie.getBudget() != 0) {
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String numberAsString = decimalFormat.format(movie.getBudget()) + "€";
            binding.budget.setText(numberAsString);
        } else {
            binding.budget.setText(R.string.emptyField);
        }

        // recyclers
        binding.realisators.setAdapter(
                new ArtistsAdapter(requireContext(), this, movie.getDirectors())
        );
        binding.cast.setAdapter(
                new ActorsAdapter(requireContext(), this, movie.getCredits().getCast())
        );
        binding.recyclerRecommendations.setAdapter(
                new MoviesAdapter(
                        requireContext(),
                        this,
                        movie.getRecommendations()
                                .getResults()
                                .stream()
                                .map(Movie::toAdapterFormat)
                                .collect(Collectors.toList()),
                        true
                )
        );

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(Integer id, FragmentType type) {
        if (type == null) {
            return;
        }
        switch (type) {
            case ARTIST:
            case ACTOR: {
                Intent intent = new Intent(requireContext(), ArtistActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                break;
            }
            case MOVIE: {
                Intent intent = new Intent(requireContext(), MovieActivity.class);
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
        view.setTag(id);
        registerForContextMenu(view);
    }
}
