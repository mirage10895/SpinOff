package fr.eseo.dis.amiaudluc.spinoffapp.ui.episode;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ActorsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class EpisodeFragment extends Fragment implements SearchInterface {

    protected View episodeView;
    private Context ctx;
    private Episode episode;
    private FragmentType type;

    // layout
    private LinearLayout guestLayer;

    // view
    private RecyclerView recyclerGuest;
    private TextView airDate;
    private TextView seasonNumber;
    private TextView episodeVV;
    private TextView overview;

    public EpisodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        this.episodeView = inflater.inflate(R.layout.fragment_episode, container, false);
        this.ctx = episodeView.getContext();

        this.guestLayer = this.episodeView.findViewById(R.id.layer_guest);
        this.recyclerGuest = episodeView.findViewById(R.id.guest_stars);

        this.airDate = episodeView.findViewById(R.id.air_date);
        this.seasonNumber = episodeView.findViewById(R.id.number_of_season);
        this.episodeVV = this.episodeView.findViewById(R.id.episodes);
        this.overview = episodeView.findViewById(R.id.overview);

        return episodeView;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.airDate.setText("0");
        if (this.episode.getAirDate() != null) {
            this.airDate.setText(
                    DateUtils.toDisplayString(this.episode.getAirDate())
            );
        }
        this.seasonNumber.setText("0");
        if (this.episode.getSeasonNumber() != -1) {
            this.seasonNumber.setText(this.episode.getSeasonNumber().toString());
        }
        this.episodeVV.setText("0");
        if (this.episode.getEpisodeNumber() != -1) {
            this.episodeVV.setText(DateUtils.hoursFromMinutes(this.episode.getRuntime()));
        }
        this.overview.setText(R.string.emptyField);
        if (!"".equals(this.episode.getOverview())) {
            this.overview.setTextColor(ctx.getColor(R.color.white));
            this.overview.setText(this.episode.getOverview());
        }
        this.guestLayer.setVisibility(View.GONE);
        if (!this.episode.getGuestStars().isEmpty()) {
            this.guestLayer.setVisibility(View.VISIBLE);
            this.recyclerGuest.setHasFixedSize(true);
            this.recyclerGuest.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));
            ActorsAdapter artistsAdapter = new ActorsAdapter(ctx, this, this.episode.getGuestStars());
            this.recyclerGuest.setAdapter(artistsAdapter);
        }
    }

    public void setEpisode(Episode episode) {
        this.episode = episode;
    }

    @Override
    public void setType(FragmentType type) {
        this.type = type;
    }

    @Override
    public void onItemClick(Integer id) {
        if (this.type.equals(FragmentType.ACTOR)) {
            Intent intent = new Intent(ctx, ArtistActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer position) {

    }
}
