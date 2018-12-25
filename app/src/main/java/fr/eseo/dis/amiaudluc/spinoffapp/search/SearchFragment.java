package fr.eseo.dis.amiaudluc.spinoffapp.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.artists.ArtistActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.artists.ArtistsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.movies.MoviesAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.series.SeriesAdapter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class SearchFragment extends android.support.v4.app.Fragment implements SearchInterface {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_ROW_COUNT = "row-count";
    private int mColumnCount = 1;
    private int mRowCount = 2;
    private SearchInterface mListener;
    private MoviesAdapter moviesAdapter;
    private SeriesAdapter seriesAdapter;
    private ArtistsAdapter artistsAdapter;
    private ArrayList<Media> medias = new ArrayList<>();
    private String type;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchFragment() {
    }

    public static SearchFragment newInstance(int columnCount,int rowCount) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_ROW_COUNT,rowCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mListener = this;
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mRowCount = getArguments().getInt(ARG_ROW_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_main, container, false);

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_movies);
        RecyclerView recyclerView_serie = (RecyclerView) view.findViewById(R.id.recycler_series);
        RecyclerView recyclerView_parson = (RecyclerView) view.findViewById(R.id.recycler_artists);
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
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount,LinearLayoutManager.VERTICAL,false));
            recyclerView_serie.setLayoutManager(new GridLayoutManager(context, mColumnCount,LinearLayoutManager.VERTICAL,false));
            recyclerView_parson.setLayoutManager(new GridLayoutManager(context, mColumnCount,LinearLayoutManager.VERTICAL,false));
        }
        moviesAdapter = new MoviesAdapter(context,mListener,Content.searchedMovies);
        seriesAdapter = new SeriesAdapter(context,mListener,Content.searchedSeries);
        artistsAdapter = new ArtistsAdapter(context,mListener,Content.searchedArtists);

        recyclerView.setAdapter(moviesAdapter);
        recyclerView_serie.setAdapter(seriesAdapter);
        recyclerView_parson.setAdapter(artistsAdapter);

        return view;
    }

    public void loadData(){
        moviesAdapter.setMovies(Content.searchedMovies);
        seriesAdapter.setSeries(Content.searchedSeries);
        artistsAdapter.setArtist(Content.searchedArtists);
        moviesAdapter.notifyDataSetChanged();
        seriesAdapter.notifyDataSetChanged();
        artistsAdapter.notifyDataSetChanged();
    }

    public void setMedias(ArrayList<Media> medias){
        this.getView().findViewById(R.id.movies_layer).setVisibility(View.VISIBLE);
        this.getView().findViewById(R.id.series_layer).setVisibility(View.VISIBLE);
        this.getView().findViewById(R.id.artists_layer).setVisibility(View.VISIBLE);
        this.medias = medias;
        for(Media media : this.medias){
            if(media.getMediaType().equals(Media.MOVIE)){
                Content.searchedMovies.add((Movie) media);
            }else if(media.getMediaType().equals(Media.SERIE)){
                Content.searchedSeries.add((Serie) media);
            }else if (media.getMediaType().equals(Media.ARTIST)){
                Content.searchedArtists.add((Artist) media);
            }
        }
        if (Content.searchedMovies.isEmpty()){
            this.getView().findViewById(R.id.movies_layer).setVisibility(View.GONE);
        }
        if(Content.searchedSeries.isEmpty()){
            this.getView().findViewById(R.id.series_layer).setVisibility(View.GONE);
        }
        if (Content.searchedArtists.isEmpty()){
            this.getView().findViewById(R.id.artists_layer).setVisibility(View.GONE);
        }

        loadData();
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

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public void onItemClick(int position) {
        Log.e("TAG","You clicked on "+position+ " and it was ");
        if (Media.MOVIE.equals(getType())){
            Content.currentMovie = Content.searchedMovies.get(position);
            Log.e("TAG","A MOVIE");
            Intent intent = new Intent(getContext(), MovieActivity.class);
            startActivity(intent);
        }else if (Media.SERIE.equals(getType())){
            Content.currentSerie = Content.searchedSeries.get(position);
            Log.e("TAG","A TV");
            Intent intent = new Intent(getContext(), SerieActivity.class);
            startActivity(intent);
        }else if (Media.ARTIST.equals(getType())){
            Content.currentArtist= Content.searchedArtists.get(position);
            Log.e("TAG","A PERSON");
            Intent intent = new Intent(getContext(), ArtistActivity.class);
            startActivity(intent);
        }else{
            Log.e("NOT","WORKING");
        }
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, int position) {

    }
}
