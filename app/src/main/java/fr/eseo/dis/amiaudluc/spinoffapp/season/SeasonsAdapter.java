package fr.eseo.dis.amiaudluc.spinoffapp.season;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;

/**
 * Created by lucasamiaud on 07/03/2018.
 */

public class SeasonsAdapter extends RecyclerView.Adapter<SeasonsAdapter.SeasonViewHolder>{

    private ArrayList<Season> seasons;
    private final ItemInterface mListener;
    private final Context ctx;

    public SeasonsAdapter(Context ctx, ItemInterface listener, ArrayList<Season> data){
        this.mListener = listener;
        this.ctx = ctx;
        this.setSeasons(data);
    }

    public void setSeasons(ArrayList<Season> seasons){
        this.seasons = seasons;
    }

    @Override
    public SeasonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View seasonView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_season, parent, false);
        return new SeasonViewHolder(seasonView);
    }

    @Override
    public void onBindViewHolder(SeasonViewHolder holder, int position) {
        if (getItemCount() != 0) {
            Season season = seasons.get(position);

            holder.seasonPoster.setImageResource(R.drawable.ic_loading);
            if(season.getPosterPath() != null){
                String link = ctx.getResources().getString(R.string.base_url_poster_500) + season.getPosterPath();
                Picasso.with(ctx).load(link).fit().error(R.drawable.ic_cam_iris)
                        .into(holder.seasonPoster);
            }

            holder.seasonNumber.setText(R.string.emptyField);
            if (season.getSeasonNumber() != -1){
                holder.seasonNumber.setText(String.valueOf(season.getSeasonNumber()));
            }

            holder.seasonNumberEpisodes.setText(R.string.emptyField);
            if (season.getEpisodeCount() != -1){
                holder.seasonNumberEpisodes.setText(String.valueOf(season.getEpisodeCount()));
            }

            Calendar cal = Calendar.getInstance(Locale.US);
            holder.seasonAirDate.setText(R.string.emptyField);
            if (season.getAirDate() != null){
                cal.setTime(season.getAirDate());
                String date = String.valueOf(cal.get(Calendar.DATE))
                        .concat(" - "+String.valueOf(cal.get(Calendar.MONTH))
                                .concat(" - "+String.valueOf(cal.get(Calendar.YEAR))));
                holder.seasonAirDate.setText(date);
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.seasons.size();
    }

    class SeasonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final View view;
        private SearchInterface frag = (SearchInterface) mListener;

        private final ImageView seasonPoster;
        private final TextView seasonAirDate;
        private final TextView seasonNumberEpisodes;
        private final TextView seasonNumber;

        public SeasonViewHolder(View view) {
            super(view);
            this.view = view;

            seasonPoster = view.findViewById(R.id.poster_ic);
            seasonAirDate = view.findViewById(R.id.air_date);
            seasonNumberEpisodes = view.findViewById(R.id.episodes);
            seasonNumber = view.findViewById(R.id.season);


            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            frag.setType("season");
            mListener.onItemClick(getAdapterPosition());
        }

    }
}
