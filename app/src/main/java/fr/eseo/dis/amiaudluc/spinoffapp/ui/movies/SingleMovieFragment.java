package fr.eseo.dis.amiaudluc.spinoffapp.ui.movies;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.CircularImageBar;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.common.youtube.YoutubeFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Genre;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ActorsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleMovieFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleMovieFragment extends Fragment implements SearchInterface {

    private View singleMovieView;
    private Context ctx;
    private Movie movie;
    private FragmentType type;

    // recyclers
    private RecyclerView directorRecycler;
    private RecyclerView castRecycler;
    private RecyclerView recommendationRecycler;

    // views
    private ImageView rate;
    private TextView runtime;
    private TextView releaseDate;
    private TextView textGenre;
    private TextView overview;
    private TextView budget;

    public SingleMovieFragment() {
        // Required empty public constructor
    }

    public static SingleMovieFragment newInstance() {
        SingleMovieFragment fragment = new SingleMovieFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.singleMovieView = inflater.inflate(R.layout.fragment_single_movie, container, false);
        this.ctx = singleMovieView.getContext();

        this.directorRecycler = singleMovieView.findViewById(R.id.realisators);
        this.castRecycler = singleMovieView.findViewById(R.id.cast);
        this.recommendationRecycler = singleMovieView.findViewById(R.id.recycler_recommendations);

        this.rate = singleMovieView.findViewById(R.id.rate);
        this.runtime = singleMovieView.findViewById(R.id.runtime);
        this.releaseDate = singleMovieView.findViewById(R.id.release_date);
        this.textGenre = singleMovieView.findViewById(R.id.genres);
        this.overview = singleMovieView.findViewById(R.id.overview);
        this.budget = singleMovieView.findViewById(R.id.budget);

        return singleMovieView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (movie == null) {
            this.onDestroy();
            return;
        }

        this.directorRecycler.setHasFixedSize(true);
        this.directorRecycler.setLayoutManager(
                new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        );
        this.directorRecycler.setAdapter(
                new ArtistsAdapter(singleMovieView.getContext(), this, movie.getDirectors())
        );
        this.castRecycler.setHasFixedSize(true);
        this.castRecycler.setLayoutManager(
                new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        );
        this.castRecycler.setAdapter(
                new ActorsAdapter(singleMovieView.getContext(), this, movie.getCredits().getCast())
        );
        this.recommendationRecycler.setHasFixedSize(true);
        this.recommendationRecycler.setLayoutManager(
                new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        );
        this.recommendationRecycler.setAdapter(
                new MoviesAdapter(
                        this.singleMovieView.getContext(),
                        this,
                        this.movie.getRecommendations()
                                .getResults()
                                .stream()
                                .map(Movie::toDatabaseFormat)
                                .collect(Collectors.toList())
                )
        );
        this.rate.setImageBitmap(CircularImageBar.buildNote(0));
        if (this.movie.getVoteAverage() != null) {
            this.rate.setImageBitmap(CircularImageBar.buildNote(this.movie.getVoteAverage()));
        }
        this.runtime.setText(getString(R.string.emptyField));
        if (this.movie.getRuntime() != null) {
            this.runtime.setText(
                    DateUtils.hoursFromMinutes(this.movie.getRuntime())
            );
        }
        this.releaseDate.setText(R.string.emptyField);
        if (this.movie.getReleaseDate() != null) {
            this.releaseDate.setText(DateUtils.toDisplayString(this.movie.getReleaseDate()));
        }
        this.textGenre.setText(R.string.emptyField);
        this.textGenre.setText(
                this.movie.getGenres()
                        .stream()
                        .map(Genre::getName)
                        .collect(Collectors.joining(", "))
        );
        this.overview.setText(getResources().getString(R.string.emptyField));
        if (this.movie.getOverview() != null) {
            this.overview.setText(this.movie.getOverview());
        }
        this.budget.setText(getResources().getString(R.string.emptyField));
        if (this.movie.getBudget() != 0) {
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String numberAsString = decimalFormat.format(movie.getBudget()) + "€";
            this.budget.setText(numberAsString);
        }

        if (this.movie.getRightVideo() != null) {
            FragmentManager manager = getFragmentManager();
            if (manager != null) {
                YoutubeFragment fragment = new YoutubeFragment();
                fragment.instanciate(this.movie.getRightVideo().getKey());
                manager.beginTransaction()
                        .replace(R.id.youtube_content, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    @Override
    public void onItemClick(Integer id) {
        switch (this.type) {
            case NETWORK:
                //Content.currentNetwork = this.movie.getProductionCompanies().get(position);
                break;
            case ARTIST:
            case ACTOR: {
                Intent intent = new Intent(ctx, ArtistActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                break;
            }
            case MOVIE: {
                Intent intent = new Intent(ctx, MovieActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                break;
            }
            case SERIE:
                break;
            case DEFAULT:
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer position) {

    }

    @Override
    public void setType(FragmentType type) {
        this.type = type;
    }
}
