package fr.eseo.dis.amiaudluc.spinoffapp.ui.series;


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

import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Genre;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.artists.ArtistsAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.common.CircularImageBar;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Language;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.networks.NetworksAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.season.SeasonActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.season.SeasonsAdapter;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SingleSerieFragment extends Fragment implements SearchInterface {

    View singleSerieView;
    private Context ctx;
    private Serie serie;
    private String type;

    public SingleSerieFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        singleSerieView = inflater.inflate(R.layout.fragment_single_serie, container, false);
        ctx = singleSerieView.getContext();

        singleSerieView.setBackgroundColor(ctx.getColor(R.color.color_primary_semi_opaq));

        ImageView rate = singleSerieView.findViewById(R.id.rate);
        rate.setImageBitmap(CircularImageBar.BuildNote(0));
        if(serie.getVoteAverage() != -1){
            rate.setImageBitmap(CircularImageBar.BuildNote((serie.getVoteAverage())));
        }

        ImageView flag = singleSerieView.findViewById(R.id.flag);
        flag.setImageResource(R.drawable.ic_cam_iris);
        if (!Language.DEFAULT.equals(serie.getOriginalLanguage())){
            int imageResource = getResources().getIdentifier("@drawable/" + serie.getOriginalLanguage().getName() + "_icon",null,ctx.getPackageName());
            flag.setImageResource(imageResource);
        }

        ImageView season = singleSerieView.findViewById(R.id.number_of_season);
        season.setImageBitmap(CircularImageBar.BuildSeasons(0));
        if (serie.getNumberOfSeasons() != -1){
            season.setImageBitmap(CircularImageBar.BuildSeasons(serie.getNumberOfSeasons()));
        }

        TextView textGenre = singleSerieView.findViewById(R.id.genres);
        textGenre.setText(R.string.emptyField);
        if(!serie.getGenres().isEmpty()){
            textGenre.setText(serie.getGenres().stream().map(Genre::getName).collect(Collectors.joining(", ")));
        }

        TextView overview = singleSerieView.findViewById(R.id.overview);
        overview.setText(getResources().getString(R.string.emptyField));
        if (serie.getOverview() != null){
            overview.setText(serie.getOverview());
        }

        RecyclerView recyclerSeason = singleSerieView.findViewById(R.id.seasons);
        recyclerSeason.setHasFixedSize(true);
        recyclerSeason.setLayoutManager(new LinearLayoutManager(ctx,LinearLayoutManager.VERTICAL,false));
        recyclerSeason.setNestedScrollingEnabled(false);
        recyclerSeason.setAdapter(new SeasonsAdapter(singleSerieView.getContext(),this, serie.getSeasons()));

        RecyclerView recyclerReal = singleSerieView.findViewById(R.id.realisators);
        recyclerReal.setHasFixedSize(true);
        recyclerReal.setLayoutManager(new LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL,false));
        recyclerReal.setAdapter(new ArtistsAdapter(singleSerieView.getContext(),this, serie.getCreatedBy()));

        RecyclerView recyclerNetwork = singleSerieView.findViewById(R.id.networks);
        recyclerNetwork.setHasFixedSize(true);
        recyclerNetwork.setLayoutManager(new LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL,false));
        recyclerNetwork.setAdapter(new NetworksAdapter(singleSerieView.getContext(),this, serie.getNetworks()));

        return singleSerieView;
    }

    public void setSerie(Serie serie){
        this.serie = serie;
    }

    @Override
    public void onItemClick(Integer id) {
        switch (this.type) {
            case "network":
                break;
            case "season": {
                Intent intent = new Intent(ctx, SeasonActivity.class);
                intent.putExtra("serieId", serie.getId());
                intent.putExtra("seasonNumber", id);
                startActivity(intent);
                break;
            }
            case "artist": {
                Intent intent = new Intent(ctx, ArtistActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer position) {

    }

    @Override
    public void setType(String type) {
        this.type = type;
    }
}