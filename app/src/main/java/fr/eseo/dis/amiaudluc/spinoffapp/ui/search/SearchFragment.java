package fr.eseo.dis.amiaudluc.spinoffapp.ui.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MoviesAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SeriesAdapter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class SearchFragment extends android.support.v4.app.Fragment implements SearchInterface {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private SearchInterface mListener;
    private MoviesAdapter moviesAdapter;
    private SeriesAdapter seriesAdapter;
    private ArtistsAdapter artistsAdapter;
    private List<Media> medias;
    private FragmentType type;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchFragment() {
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

        this.mListener = this;
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_main, container, false);

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_movies);
        RecyclerView recyclerView_serie = view.findViewById(R.id.recycler_series);
        RecyclerView recyclerView_parson = view.findViewById(R.id.recycler_artists);
        recyclerView.setHasFixedSize(true);
        recyclerView_serie.setHasFixedSize(true);
        recyclerView_parson.setHasFixedSize(true);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,
                    false));
            recyclerView_serie.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,
                    false));
            recyclerView_parson.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,
                    false));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount, LinearLayoutManager.VERTICAL,false));
            recyclerView_serie.setLayoutManager(new GridLayoutManager(context, mColumnCount, LinearLayoutManager.VERTICAL,false));
            recyclerView_parson.setLayoutManager(new GridLayoutManager(context, mColumnCount, LinearLayoutManager.VERTICAL,false));
        }
        moviesAdapter = new MoviesAdapter(context, mListener, new ArrayList<>());
        seriesAdapter = new SeriesAdapter(context, mListener, new ArrayList<>());
        artistsAdapter = new ArtistsAdapter(context, mListener, new ArrayList<>());

        recyclerView.setAdapter(moviesAdapter);
        recyclerView_serie.setAdapter(seriesAdapter);
        recyclerView_parson.setAdapter(artistsAdapter);

        return view;
    }

    public void loadData(List<MovieDatabase> movies, List<SerieDatabase> series, List<Artist> artists){
        moviesAdapter.setMovies(movies);
        seriesAdapter.setSeries(series);
        artistsAdapter.setArtist(artists);
        moviesAdapter.notifyDataSetChanged();
        seriesAdapter.notifyDataSetChanged();
        artistsAdapter.notifyDataSetChanged();
    }

    public void setMedias(List<Media> medias){
        List<Movie> movies = new ArrayList<>();
        List<Serie> series = new ArrayList<>();
        List<Artist> artists = new ArrayList<>();
        this.getView().findViewById(R.id.movies_layer).setVisibility(View.VISIBLE);
        this.getView().findViewById(R.id.series_layer).setVisibility(View.VISIBLE);
        this.getView().findViewById(R.id.artists_layer).setVisibility(View.VISIBLE);
        this.medias = medias;
        for(Media media : this.medias){
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
        if (movies.isEmpty()){
            this.getView().findViewById(R.id.movies_layer).setVisibility(View.GONE);
        }
        if(series.isEmpty()){
            this.getView().findViewById(R.id.series_layer).setVisibility(View.GONE);
        }
        if (artists.isEmpty()){
            this.getView().findViewById(R.id.artists_layer).setVisibility(View.GONE);
        }

        loadData(movies.stream().map(Movie::toDatabaseFormat).collect(Collectors.toList()),
                series.stream().map(Serie::toDataBaseFormat).collect(Collectors.toList()),
                artists);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setType(FragmentType type) {
        this.type = type;
    }

    @Override
    public void onItemClick(Integer id) {
        if (this.type.equals(FragmentType.MOVIE)){
            Intent intent = new Intent(getContext(), MovieActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        } else if (this.type.equals(FragmentType.SERIE)){
            Intent intent = new Intent(getContext(), SerieActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        } else if (this.type.equals(FragmentType.ARTIST)){
            Intent intent = new Intent(getContext(), ArtistActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer position) {

    }
}
