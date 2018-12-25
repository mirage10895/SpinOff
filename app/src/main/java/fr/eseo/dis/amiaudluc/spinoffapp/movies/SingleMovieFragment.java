package fr.eseo.dis.amiaudluc.spinoffapp.movies;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.Calendar;
import java.util.Locale;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.artists.ActorsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.artists.ArtistActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.artists.ArtistsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.common.CircularImageBar;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.common.youtube.YoutubeFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Language;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleMovieFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleMovieFragment extends Fragment implements SearchInterface {

    private View singleMovieView;
    private Context ctx;
    private Movie movie = Content.currentMovie;
    private String type ="";
    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerSupportFragment youTubePlayerView;

    public SingleMovieFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SingleMovieFragment.
     */
    public static SingleMovieFragment newInstance(String param1, String param2) {
        SingleMovieFragment fragment = new SingleMovieFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        singleMovieView = inflater.inflate(R.layout.fragment_single_movie, container, false);
        ctx = singleMovieView.getContext();

        ImageView rate = singleMovieView.findViewById(R.id.rate);
        rate.setImageBitmap(CircularImageBar.BuildNote(0));
        if(movie.getVoteAvg() != -1){
            rate.setImageBitmap(CircularImageBar.BuildNote((movie.getVoteAvg())));
        }

        ImageView flag = singleMovieView.findViewById(R.id.flag);
        flag.setImageResource(R.drawable.ic_loading);
        if (!movie.getOriginalLanguage().equals(Language.unknown)){
            int imageResource = getResources().getIdentifier("@drawable/"+movie.getOriginalLanguage().name()+"_icon",null,ctx.getPackageName());
            flag.setImageResource(imageResource);
        }

        Calendar cal = Calendar.getInstance(Locale.US);
        ImageView year = singleMovieView.findViewById(R.id.year);
        year.setImageBitmap(CircularImageBar.BuildSeasons(0));
        if (movie.getReleaseDate() != null){
            cal.setTime(movie.getReleaseDate());
            year.setImageBitmap(CircularImageBar.BuildSeasons(cal.get(Calendar.YEAR)));
        }

        TextView textGenre = singleMovieView.findViewById(R.id.genres);
        textGenre.setText(R.string.emptyField);
        if(!movie.getGenres().isEmpty()){
            StringBuilder s = new StringBuilder(movie.getGenres().get(0).getName());
            for (int i = 1;i<movie.getGenres().size();i++){
                s.append(", ").append(movie.getGenres().get(i).getName());
            }
            textGenre.setText(s.toString());
        }

        TextView overview = singleMovieView.findViewById(R.id.overview);
        overview.setText(getResources().getString(R.string.emptyField));
        if (movie.getOverview() != null){
            overview.setText(movie.getOverview());
        }

        TextView budget = singleMovieView.findViewById(R.id.budget);
        budget.setText(getResources().getString(R.string.emptyField));
        if (movie.getBudget() != 0){
            budget.setText(String.valueOf(movie.getBudget()));
        }

        RecyclerView recyclerReal = singleMovieView.findViewById(R.id.realisators);
        recyclerReal.setHasFixedSize(true);
        recyclerReal.setLayoutManager(new LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL,false));
        recyclerReal.setAdapter(new ArtistsAdapter(singleMovieView.getContext(),this,movie.getDirectors()));

        RecyclerView recyclerCast = singleMovieView.findViewById(R.id.cast);
        recyclerCast.setHasFixedSize(true);
        recyclerCast.setLayoutManager(new LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL,false));
        recyclerCast.setAdapter(new ActorsAdapter(singleMovieView.getContext(),this,movie.getCast()));

        if (movie.getRightVideo().getId() != null){
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

    public void setMovie(Movie movie){
        this.movie = movie;
    }

    @Override
    public void onItemClick(int position) {
        if(this.type.equals("network")){
            //Content.currentNetwork = this.movie.getProductionCompanies().get(position);
        }else if (this.type.equals("artist")){
            Content.currentArtist = this.movie.getDirectors().get(position);
            Intent intent = new Intent(ctx, ArtistActivity.class);
            startActivity(intent);
        }else if (this.type.equals("actor")){
            Content.currentArtist = this.movie.getCast().get(position);
            Intent intent = new Intent(ctx, ArtistActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, int position) {

    }

    @Override
    public void setType(String type) {
        this.type = type;
    }
}
