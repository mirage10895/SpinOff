package fr.eseo.dis.amiaudluc.spinoffapp.ui.artists;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MoviesAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SeriesAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistFragment extends Fragment implements SearchInterface {

    private Artist artist;
    private Context ctx;
    private MoviesAdapter moviesAdapter;
    private SeriesAdapter seriesAdapter;
    private SearchInterface mListener;
    private FragmentType type;

    public ArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View artistView = inflater.inflate(R.layout.fragment_artist, container, false);
        ctx = artistView.getContext();
        this.mListener = this;

        ImageView imageView = artistView.findViewById(R.id.poster_ic);
        String link = getResources().getString(R.string.base_url_poster_500) + this.artist.getProfilePath();
        Picasso.with(ctx)
                .load(link)
                .fit()
                .centerInside()
                .placeholder(R.drawable.ic_unknown)
                .error(R.drawable.ic_unknown)
                .into(imageView);

        TextView textView = artistView.findViewById(R.id.name);
        textView.setText(this.artist.getName());


        if (!artist.getMovies().getCast().isEmpty()) {
            RecyclerView recyclerView = (RecyclerView) artistView.findViewById(R.id.recycler_movies);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL,
                    false));
            moviesAdapter = new MoviesAdapter(ctx, mListener, artist.getMovies().getCast().stream().map(Movie::toDatabaseFormat).collect(Collectors.toList()));
            recyclerView.setAdapter(moviesAdapter);
        } else {
            artistView.findViewById(R.id.movies_layer).setVisibility(View.GONE);
        }
        if (!artist.getSeries().getCast().isEmpty()) {
            RecyclerView recyclerViewSerie = artistView.findViewById(R.id.recycler_series);
            recyclerViewSerie.setHasFixedSize(true);
            recyclerViewSerie.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL,
                    false));
            seriesAdapter = new SeriesAdapter(ctx, mListener, artist.getSeries().getCast().stream().map(Serie::toDataBaseFormat).collect(Collectors.toList()));
            recyclerViewSerie.setAdapter(seriesAdapter);
        } else {
            artistView.findViewById(R.id.series_layer).setVisibility(View.GONE);
        }
        artistView.findViewById(R.id.artists_layer).setVisibility(View.GONE);

        TextView whoAreYou = artistView.findViewById(R.id.who_are_you);
        whoAreYou.setText(R.string.emptyField);
        if (artist.getBiography() != null) {
            whoAreYou.setText(artist.getBiography());
        }

        TextView birthday = artistView.findViewById(R.id.birthday);
        birthday.setText(R.string.emptyField);
        if (artist.getBirthday() != null) {
            String birthDate = DateUtils.toDisplayString(artist.getBirthday());
            String birthPlace = artist.getPlaceOfBirth();
            String stringBuilder = birthDate + " (" + birthPlace + ")";
            birthday.setText(stringBuilder);
        }

        TextView seeMore = artistView.findViewById(R.id.see_more);
        seeMore.setText(R.string.emptyField);
        if (artist.getHomepage() != null) {
            seeMore.setText(artist.getHomepage());
        }

        return artistView;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    @Override
    public void setType(FragmentType type) {
        this.type = type;
    }

    @Override
    public void onItemClick(Integer id) {
        if (FragmentType.MOVIE.equals(this.type)) {
            Intent intent = new Intent(getContext(), MovieActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        } else if (FragmentType.SERIE.equals(this.type)) {
            Intent intent = new Intent(getContext(), SerieActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer position) {

    }
}
