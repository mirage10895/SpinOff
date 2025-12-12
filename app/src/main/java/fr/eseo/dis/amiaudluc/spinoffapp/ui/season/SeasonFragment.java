package fr.eseo.dis.amiaudluc.spinoffapp.ui.season;

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

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.CircularImageBar;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.common.youtube.YoutubeFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ActorsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.episode.EpisodeActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.episode.EpisodesAdapter;

public class SeasonFragment extends Fragment implements SearchInterface {

    private Context ctx;
    private Season season;
    private FragmentType type;
    private View seasonView;

    // recyclers
    private RecyclerView recyclerEpisodes;
    private RecyclerView recyclerGuest;

    // views
    private ImageView airDate;
    private TextView seasonNumber;
    private TextView episode;
    private TextView overview;

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
        super.onCreateView(inflater, container, savedInstanceState);
        this.seasonView = inflater.inflate(R.layout.fragment_season, container, false);
        this.ctx = seasonView.getContext();

        this.recyclerEpisodes = seasonView.findViewById(R.id.episodes_recycler);
        this.recyclerGuest = seasonView.findViewById(R.id.guest_stars);

        this.airDate = seasonView.findViewById(R.id.air_date);
        this.seasonNumber = seasonView.findViewById(R.id.number_of_season);
        this.episode = seasonView.findViewById(R.id.episodes);
        this.overview = seasonView.findViewById(R.id.overview);

        return seasonView;
    }

    @Override
    public void onStart() {
        super.onStart();

        this.recyclerEpisodes.setHasFixedSize(true);
        this.recyclerEpisodes.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false));
        this.recyclerEpisodes.setNestedScrollingEnabled(false);
        this.recyclerEpisodes.setAdapter(new EpisodesAdapter(this.seasonView.getContext(), this, season.getEpisodes()));

        this.recyclerGuest.setHasFixedSize(true);
        this.recyclerGuest.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));
        ActorsAdapter artistsAdapter = new ActorsAdapter(ctx, this, this.season.getCredits().getCast());
        this.recyclerGuest.setAdapter(artistsAdapter);

        this.airDate.setImageBitmap(CircularImageBar.buildNumber(0, ctx.getColor(R.color.colorAccent)));
        if (this.season.getAirDate() != null) {
            this.airDate.setImageBitmap(
                    CircularImageBar.buildNumber(
                            this.season.getAirDate().getYear(),
                            this.ctx.getColor(R.color.white)
                    )
            );
        }
        this.seasonNumber.setText("0");
        if (this.season.getSeasonNumber() != -1) {
            this.seasonNumber.setText(season.getSeasonNumber().toString());
        }
        this.episode.setText("0");
        if (!this.season.getEpisodes().isEmpty()) {
            this.episode.setText(String.valueOf(this.season.getEpisodes().size()));
        }
        this.overview.setText(R.string.emptyField);
        if (!"".equals(this.season.getOverview())) {
            this.overview.setTextColor(ctx.getColor(R.color.white));
            this.overview.setText(season.getOverview());
        }
        if (this.season.getRightVideo() != null) {
            FragmentManager manager = getFragmentManager();
            if (manager != null) {
                YoutubeFragment fragment = new YoutubeFragment();
                fragment.instanciate(this.season.getRightVideo().getKey());
                manager.beginTransaction()
                        .replace(R.id.youtube_content, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    @Override
    public void onItemClick(Integer id) {
        if (this.type.equals(FragmentType.ACTOR)) {
            Intent intent = new Intent(ctx, ArtistActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        } else if (this.type.equals(FragmentType.EPISODE)) {
            Intent intent = new Intent(ctx, EpisodeActivity.class);
            intent.putExtra("serieId", season.getSerieId());
            intent.putExtra("seasonNumber", season.getSeasonNumber());
            intent.putExtra("episodeNumber", id);
            startActivity(intent);
        }
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer position) {

    }

    @Override
    public void setType(FragmentType type) {
        this.type = type;
    }


}
