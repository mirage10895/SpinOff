package fr.eseo.dis.amiaudluc.spinoffapp.ui.season;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Calendar;
import java.util.Locale;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ActorsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.common.CircularImageBar;
import fr.eseo.dis.amiaudluc.spinoffapp.common.GrayscaleTransformation;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.common.youtube.YoutubeFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.episode.EpisodeActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.episode.EpisodesAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class SeasonFragment extends Fragment implements SearchInterface {

    View seasonView;
    private Context ctx;
    private Season season = Content.currentSeason;
    private String type;

    public SeasonFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        seasonView = inflater.inflate(R.layout.fragment_season, container, false);
        ctx = seasonView.getContext();

        String link = this.getResources().getString(R.string.base_url_poster_original) + season.getPosterPath();

        setBackground(link);

        ImageView air_date = seasonView.findViewById(R.id.air_date);
        air_date.setImageBitmap(CircularImageBar.BuildNumber(0,R.color.colorAccent,ctx));
        Calendar cal = Calendar.getInstance(Locale.US);
        if(season.getAirDate()!= null){
            cal.setTime(season.getAirDate());
            air_date.setImageBitmap(CircularImageBar.BuildNumber(cal.get(Calendar.YEAR),R.color.colorAccent,ctx));
        }

        ImageView seasonNumber = seasonView.findViewById(R.id.number_of_season);
        seasonNumber.setImageBitmap(CircularImageBar.BuildNumber(0,R.color.colorAccent,ctx));
        if(season.getSeasonNumber() != -1){
            seasonNumber.setImageBitmap(CircularImageBar.BuildNumber(season.getSeasonNumber(),R.color.colorAccent,ctx));
        }

        ImageView episode = seasonView.findViewById(R.id.episodes);
        episode.setImageBitmap(CircularImageBar.BuildNumber(0,R.color.colorAccent,ctx));
        if(season.getEpisodes().size() != -1){
            episode.setImageBitmap(CircularImageBar.BuildNumber(season.getEpisodes().size(),R.color.colorAccent,ctx));
        }


        TextView overview = seasonView.findViewById(R.id.overview);
        overview.setText(R.string.emptyField);
        if (!season.getOverview().equals("")){
            overview.setTextColor(ctx.getColor(R.color.white));
            overview.setText(season.getOverview());
        }

        RecyclerView recyclerEpisodes = seasonView.findViewById(R.id.episodes_recycler);
        recyclerEpisodes.setHasFixedSize(true);
        recyclerEpisodes.setLayoutManager(new LinearLayoutManager(ctx,LinearLayoutManager.VERTICAL,false));
        recyclerEpisodes.setNestedScrollingEnabled(false);
        recyclerEpisodes.setAdapter(new EpisodesAdapter(seasonView.getContext(),this,season.getEpisodes()));

        RecyclerView recyclerGuest = seasonView.findViewById(R.id.guest_stars);
        recyclerGuest.setHasFixedSize(true);
        recyclerGuest.setLayoutManager(new LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL,false));
        ActorsAdapter artistsAdapter = new ActorsAdapter(ctx,this, this.season.getCredits().getCast());
        recyclerGuest.setAdapter(artistsAdapter);

        if (season.getRightVideo().getId() != null){
            YoutubeFragment fragment = new YoutubeFragment();
            fragment.instanciate(season.getRightVideo().getKey());
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.youtube_content, fragment)
                    .addToBackStack(null)
                    .commit();
        }

        return seasonView;
    }

    private void setBackground(String link){
        final NestedScrollView nestedScrollView = seasonView.findViewById(R.id.nested_view);
        final Target target = new Target(){

            final ProgressBar progressBar = seasonView.findViewById(R.id.progressBarBody);
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                progressBar.setVisibility(View.GONE);
                nestedScrollView.setBackground(new BitmapDrawable(getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(final Drawable errorDrawable) {
                progressBar.setVisibility(View.GONE);
                nestedScrollView.setBackground(ctx.getDrawable(R.drawable.ic_launcher_foreground));
                Log.d("TAG", "FAILED");
            }

            @Override
            public void onPrepareLoad(final Drawable placeHolderDrawable) {
                progressBar.setVisibility(View.VISIBLE);
            }
        };
        nestedScrollView.setTag(target);
        Picasso.with(ctx)
                .load(link)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .transform(new GrayscaleTransformation(Picasso.with(ctx))).into(target);
    }

    public void setSeason(Season season){
        this.season = season;
    }

    @Override
    public void onItemClick(Integer id) {
        if (this.getType().equals("actor")){
            Intent intent = new Intent(ctx, ArtistActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        } else if (this.getType().equals("episode")){
            Intent intent = new Intent(ctx, EpisodeActivity.class);
            intent.putExtra("serieId", id);
            intent.putExtra("seasonNumber", id);
            intent.putExtra("episodeNumber", id);
            startActivity(intent);
        }
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer position) {

    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    public String getType(){
        return this.type;
    }


}
