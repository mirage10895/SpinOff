package fr.eseo.dis.amiaudluc.spinoffapp.episode;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.artists.ActorsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.artists.ArtistActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.common.CircularImageBar;
import fr.eseo.dis.amiaudluc.spinoffapp.common.GrayscaleTransformation;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Episode;

/**
 * A simple {@link Fragment} subclass.
 */
public class EpisodeFragment extends Fragment implements SearchInterface{

    View episodeView;
    private Context ctx;
    private Episode episode = Content.currentEpisode;
    private String type;

    public EpisodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        episodeView = inflater.inflate(R.layout.fragment_episode, container, false);
        ctx = episodeView.getContext();

        String link = this.getResources().getString(R.string.base_url_poster_original) + Content.currentSeason.getPosterPath();

        setBackground(link);

        ImageView air_date = episodeView.findViewById(R.id.air_date);
        air_date.setImageBitmap(CircularImageBar.BuildNumber(0,R.color.colorAccent,ctx));
        if(episode.getAirDate()!= null){
            Calendar cal = Calendar.getInstance(Locale.US);
            cal.setTime(episode.getAirDate());
            air_date.setImageBitmap(CircularImageBar.BuildString(DateUtils.getStringFromDate(episode.getAirDate()),R.color.colorAccent,ctx));
        }

        ImageView seasonNumber = episodeView.findViewById(R.id.number_of_season);
        seasonNumber.setImageBitmap(CircularImageBar.BuildNumber(0,R.color.colorAccent,ctx));
        if(episode.getSeasonNumber() != -1){
            seasonNumber.setImageBitmap(CircularImageBar.BuildNumber(episode.getSeasonNumber(),R.color.colorAccent,ctx));
        }

        ImageView episodeVV = episodeView.findViewById(R.id.episodes);
        episodeVV.setImageBitmap(CircularImageBar.BuildNumber(0,R.color.colorAccent,ctx));
        if(episode.getEpisodeNumber() != -1){
            episodeVV.setImageBitmap(CircularImageBar.BuildNumber(episode.getEpisodeNumber(),R.color.colorAccent,ctx));
        }

        TextView overview = episodeView.findViewById(R.id.overview);
        overview.setText(R.string.emptyField);
        if (!episode.getOverview().equals("")){
            overview.setTextColor(ctx.getColor(R.color.white));
            overview.setText(episode.getOverview());
        }

        if (this.episode.getGuestStars().isEmpty()){
            episodeView.findViewById(R.id.layer_guest).setVisibility(View.GONE);
        }else{
            RecyclerView recyclerGuest = episodeView.findViewById(R.id.guest_stars);
            recyclerGuest.setHasFixedSize(true);
            recyclerGuest.setLayoutManager(new LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL,false));
            ActorsAdapter artistsAdapter = new ActorsAdapter(ctx,this, this.episode.getGuestStars());
            recyclerGuest.setAdapter(artistsAdapter);
        }

        return episodeView;
    }

    private void setBackground(String link){
        final NestedScrollView nestedScrollView = episodeView.findViewById(R.id.nested_view);
        final Target target = new Target(){

            final ProgressBar progressBar = episodeView.findViewById(R.id.progressBarBody);
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                progressBar.setVisibility(View.GONE);
                nestedScrollView.setBackground(new BitmapDrawable(getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(final Drawable errorDrawable) {
                progressBar.setVisibility(View.GONE);
                nestedScrollView.setBackground(ctx.getDrawable(R.drawable.ic_loading));
                Log.d("TAG", "FAILED");
            }

            @Override
            public void onPrepareLoad(final Drawable placeHolderDrawable) {
                progressBar.setVisibility(View.VISIBLE);
            }
        };
        nestedScrollView.setTag(target);
        Picasso.with(ctx).load(link).placeholder(R.drawable.ic_holder_poster).error(R.drawable.ic_holder_poster)
                .transform(new GrayscaleTransformation(Picasso.with(ctx))).into(target);
    }

    public void setEpisode(Episode episode) {
        this.episode = episode;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void onItemClick(Integer position) {
        if (this.type.equals("actor")){
            Content.currentArtist = this.episode.getGuestStars().get(position);
            Intent intent = new Intent(ctx, ArtistActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer position) {

    }
}
