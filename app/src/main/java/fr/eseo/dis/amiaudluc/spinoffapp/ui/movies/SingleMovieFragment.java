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

import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.CircularImageBar;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.common.youtube.YoutubeFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Genre;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Language;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ActorsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistsAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleMovieFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleMovieFragment extends Fragment implements SearchInterface {

    private View singleMovieView;
    private Context ctx;
    private Movie movie;
    private String type;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        singleMovieView = inflater.inflate(R.layout.fragment_single_movie, container, false);
        ctx = singleMovieView.getContext();

        ImageView rate = singleMovieView.findViewById(R.id.rate);
        rate.setImageBitmap(CircularImageBar.BuildNote(0));
        if (movie.getVoteAverage() != null) {
            rate.setImageBitmap(CircularImageBar.BuildNote((movie.getVoteAverage())));
        }

        ImageView flag = singleMovieView.findViewById(R.id.flag);
        flag.setImageResource(R.drawable.ic_launcher_foreground);
        if (!Language.DEFAULT.equals(movie.getOriginalLanguage())) {
            int imageResource = getResources().getIdentifier("@drawable/" + movie.getOriginalLanguage().getName() + "_icon", null, ctx.getPackageName());
            flag.setImageResource(imageResource);
        }

        Calendar cal = Calendar.getInstance(Locale.US);
        ImageView year = singleMovieView.findViewById(R.id.year);
        year.setImageBitmap(CircularImageBar.BuildSeasons(0));
        if (movie.getReleaseDate() != null) {
            cal.setTime(movie.getReleaseDate());
            year.setImageBitmap(CircularImageBar.BuildSeasons(cal.get(Calendar.YEAR)));
        }

        TextView textGenre = singleMovieView.findViewById(R.id.genres);
        textGenre.setText(R.string.emptyField);
        textGenre.setText(movie.getGenres().stream().map(Genre::getName).collect(Collectors.joining(", ")));

        TextView overview = singleMovieView.findViewById(R.id.overview);
        overview.setText(getResources().getString(R.string.emptyField));
        if (movie.getOverview() != null) {
            overview.setText(movie.getOverview());
        }

        TextView budget = singleMovieView.findViewById(R.id.budget);
        budget.setText(getResources().getString(R.string.emptyField));
        if (movie.getBudget() != 0) {
            budget.setText(String.valueOf(movie.getBudget()));
        }

        RecyclerView recyclerReal = singleMovieView.findViewById(R.id.realisators);
        recyclerReal.setHasFixedSize(true);
        recyclerReal.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));
        recyclerReal.setAdapter(new ArtistsAdapter(singleMovieView.getContext(), this, movie.getDirectors()));

        RecyclerView recyclerCast = singleMovieView.findViewById(R.id.cast);
        recyclerCast.setHasFixedSize(true);
        recyclerCast.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));
        recyclerCast.setAdapter(new ActorsAdapter(singleMovieView.getContext(), this, movie.getCredits().getCast()));

        if (movie.getRightVideo().getId() != null) {
            YoutubeFragment fragment = new YoutubeFragment();
            fragment.instanciate(movie.getRightVideo().getKey());
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.youtube_content, fragment)
                    .addToBackStack(null)
                    .commit();
        }

        return singleMovieView;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    @Override
    public void onItemClick(Integer id) {
        switch (this.type) {
            case "network":
                //Content.currentNetwork = this.movie.getProductionCompanies().get(position);
                break;
            case Media.ARTIST: {
                Intent intent = new Intent(ctx, ArtistActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                break;
            }
            case "actor": {
                Intent intent = new Intent(ctx, ArtistActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer position) {

    }

    @Override
    public void setType(String type) {
        this.type = type;
    }
}