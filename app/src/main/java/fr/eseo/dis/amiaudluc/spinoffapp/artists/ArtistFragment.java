package fr.eseo.dis.amiaudluc.spinoffapp.artists;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.movies.MoviesAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.series.SeriesAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistFragment extends Fragment implements SearchInterface {

    private Artist artist;
    private Context ctx;
    private MoviesAdapter moviesAdapter;
    private SeriesAdapter seriesAdapter;
    private SearchInterface mListener;
    private String type = "";

    public ArtistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View artistView = inflater.inflate(R.layout.fragment_artist, container, false);
        ctx = artistView.getContext();
        this.mListener = this;

        setArtist(Content.currentArtist);

        ImageView imageView = artistView.findViewById(R.id.poster_ic);
        String link = getResources().getString(R.string.base_url_poster_500) + this.artist.getProfilePath();
        Picasso.with(ctx).load(link).fit().centerInside().placeholder(R.drawable.ic_unknown).error(R.drawable.ic_unknown)
                .into(imageView);

        TextView textView = artistView.findViewById(R.id.name);
        textView.setText(this.artist.getName());


        if (!artist.getMovies().isEmpty()) {
            RecyclerView recyclerView = (RecyclerView) artistView.findViewById(R.id.recycler_movies);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL,
                    false));
            moviesAdapter = new MoviesAdapter(ctx, mListener, artist.getMovies().stream().map(movie -> new MovieDatabase()).collect(Collectors.toList()));
            recyclerView.setAdapter(moviesAdapter);
        } else {
            artistView.findViewById(R.id.movies_layer).setVisibility(View.GONE);
        }
        if (!artist.getSeries().isEmpty()) {
            RecyclerView recyclerView_serie = (RecyclerView) artistView.findViewById(R.id.recycler_series);
            recyclerView_serie.setHasFixedSize(true);
            recyclerView_serie.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL,
                    false));
            seriesAdapter = new SeriesAdapter(ctx, mListener, artist.getSeries().stream().map(serie -> new SerieDatabase()).collect(Collectors.toList()));
            recyclerView_serie.setAdapter(seriesAdapter);
        } else {
            artistView.findViewById(R.id.series_layer).setVisibility(View.GONE);
        }
        artistView.findViewById(R.id.artists_layer).setVisibility(View.GONE);

        TextView whoAreYou = (TextView) artistView.findViewById(R.id.who_are_you);
        whoAreYou.setText(R.string.emptyField);
        if (artist.getBiography() != null) {
            whoAreYou.setText(artist.getBiography());
        }

        return artistView;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public void onItemClick(Integer id) {
        if (Media.MOVIE.equals(getType())) {
            Intent intent = new Intent(getContext(), MovieActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        } else if (Media.SERIE.equals(getType())) {
            Intent intent = new Intent(getContext(), SerieActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        } else {
            Log.e("NOT", "WORKING");
        }
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer position) {

    }
}
