package fr.eseo.dis.amiaudluc.spinoffapp.ui.episode;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.CircularImageBar;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ActorsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class EpisodeFragment extends Fragment implements SearchInterface{

    View episodeView;
    private Context ctx;
    private Episode episode;
    private String type;

    public EpisodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.episodeView = inflater.inflate(R.layout.fragment_episode, container, false);
        ctx = episodeView.getContext();

        ImageView air_date = episodeView.findViewById(R.id.air_date);
        air_date.setImageBitmap(CircularImageBar.BuildNumber(0, ctx.getColor(R.color.colorAccent)));
        if(episode.getAirDate()!= null){
            Calendar cal = Calendar.getInstance(Locale.US);
            cal.setTime(episode.getAirDate());
            air_date.setImageBitmap(CircularImageBar.BuildString(DateUtils.getStringFromDate(episode.getAirDate()),R.color.colorAccent,ctx));
        }

        ImageView seasonNumber = episodeView.findViewById(R.id.number_of_season);
        seasonNumber.setImageBitmap(CircularImageBar.BuildNumber(0, ctx.getColor(R.color.colorAccent)));
        if(episode.getSeasonNumber() != -1){
            seasonNumber.setImageBitmap(CircularImageBar.BuildNumber(episode.getSeasonNumber(), ctx.getColor(R.color.colorAccent)));
        }

        ImageView episodeVV = episodeView.findViewById(R.id.episodes);
        episodeVV.setImageBitmap(CircularImageBar.BuildNumber(0, ctx.getColor(R.color.colorAccent)));
        if(episode.getEpisodeNumber() != -1){
            episodeVV.setImageBitmap(CircularImageBar.BuildNumber(episode.getEpisodeNumber(), ctx.getColor(R.color.colorAccent)));
        }

        TextView overview = episodeView.findViewById(R.id.overview);
        overview.setText(R.string.emptyField);
        if (!"".equals(episode.getOverview())){
            overview.setTextColor(ctx.getColor(R.color.white));
            overview.setText(episode.getOverview());
        }

        if (this.episode.getGuestStars().isEmpty()){
            episodeView.findViewById(R.id.layer_guest).setVisibility(View.GONE);
        } else {
            RecyclerView recyclerGuest = episodeView.findViewById(R.id.guest_stars);
            recyclerGuest.setHasFixedSize(true);
            recyclerGuest.setLayoutManager(new LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL,false));
            ActorsAdapter artistsAdapter = new ActorsAdapter(ctx,this, this.episode.getGuestStars());
            recyclerGuest.setAdapter(artistsAdapter);
        }

        return episodeView;
    }

    public void setEpisode(Episode episode) {
        this.episode = episode;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void onItemClick(Integer id) {
        if (this.type.equals("actor")){
            Intent intent = new Intent(ctx, ArtistActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer position) {

    }
}
