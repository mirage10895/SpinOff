package fr.eseo.dis.amiaudluc.spinoffapp.ui.episode;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Episode;

/**
 * Created by lucasamiaud on 09/03/2018.
 */

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.EpisodesViewHolder>{


    private SearchInterface fragment;
    private List<Episode> episodes;
    private Context ctx;

    public EpisodesAdapter(Context ctx, SearchInterface fragment, List<Episode> data){
        this.ctx = ctx;
        this.fragment = fragment;
        setEpisodes(data);
    }

    public void setEpisodes(List<Episode> episodes){
        this.episodes = episodes;
    }

    @Override
    public EpisodesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View episodeView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_episode, parent, false);
        return new EpisodesAdapter.EpisodesViewHolder(episodeView);
    }

    @Override
    public void onBindViewHolder(EpisodesViewHolder holder, int position) {
        if (getItemCount() != 0) {
            Episode episode = episodes.get(position);

            holder.episodePoster.setImageResource(R.drawable.ic_launcher_foreground);
            if(episode.getStillPath() != null){
                String link = ctx.getResources().getString(R.string.base_url_poster_500) + episode.getStillPath();
                Picasso.with(ctx).load(link).fit().centerCrop().error(R.drawable.ic_launcher_foreground)
                        .into(holder.episodePoster);
            }

            holder.episodeNumber.setText(R.string.emptyField);
            if (episode.getEpisodeNumber() != -1){
                holder.episodeNumber.setText(String.valueOf(episode.getEpisodeNumber()));
            }

            holder.episodeAirDate.setText(R.string.emptyField);
            if (episode.getAirDate() != null){
                holder.episodeAirDate.setText(DateUtils.getStringFromDate(episode.getAirDate()));
            }

            holder.episodeName.setText(R.string.emptyField);
            if (episode.getName() != null){
                holder.episodeName.setText(episode.getName());
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.episodes.size();
    }

    class EpisodesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final View view;

        private final ImageView episodePoster;
        private final TextView episodeName;
        private final TextView episodeAirDate;
        private final TextView episodeNumber;

        public EpisodesViewHolder(View view) {
            super(view);
            this.view = view;

            episodePoster = view.findViewById(R.id.poster_ic);
            episodeName = view.findViewById(R.id.name);
            episodeAirDate = view.findViewById(R.id.air_date);
            episodeNumber = view.findViewById(R.id.episode_number);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            fragment.setType("episode");
            fragment.onItemClick(getAdapterPosition());
        }
    }
}
