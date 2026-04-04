package fr.eseo.dis.amiaudluc.spinoffapp.ui.episode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;

/**
 * Created by lucasamiaud on 09/03/2018.
 */
public class EpisodesAdapter extends ListAdapter<Episode, EpisodesAdapter.EpisodesViewHolder> {
    private final ItemInterface fragment;
    private final Context ctx;

    private static final DiffUtil.ItemCallback<Episode> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull Episode oldItem, @NonNull Episode newItem) {
                    return Objects.equals(oldItem.getEpisodeNumber(), newItem.getEpisodeNumber());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Episode oldItem, @NonNull Episode newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public EpisodesAdapter(
            Context ctx,
            ItemInterface fragment,
            List<Episode> episodes
    ) {
        super(DIFF_CALLBACK);
        this.ctx = ctx;
        this.fragment = fragment;
        if (episodes != null) {
            submitList(new ArrayList<>(episodes));
        }
    }

    @NonNull
    @Override
    public EpisodesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View episodeView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_season, parent, false);
        return new EpisodesAdapter.EpisodesViewHolder(episodeView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesViewHolder holder, int position) {
        Episode episode = getItem(position);

        if (episode != null) {
            holder.episodePoster.setImageResource(R.drawable.ic_launcher_foreground);
            if (episode.getStillPath() != null) {
                String link = ctx.getResources().getString(R.string.base_url_poster_500) + episode.getStillPath();
                Picasso.get().load(link).fit().centerCrop().error(R.drawable.ic_launcher_foreground)
                        .into(holder.episodePoster);
            }

            holder.episodeNumber.setText(R.string.emptyField);
            if (episode.getEpisodeNumber() != null) {
                holder.episodeNumber.setText(String.valueOf(episode.getEpisodeNumber()));
            }

            holder.episodeAirDate.setText(R.string.emptyField);
            if (episode.getAirDate() != null) {
                holder.episodeAirDate.setText(DateUtils.toDisplayString(episode.getAirDate()));
            }

            holder.episodeName.setText(R.string.emptyField);
            if (episode.getName() != null) {
                holder.episodeName.setText(episode.getName());
            }
        }
    }

    public class EpisodesViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final EpisodesAdapter adapter;

        private final ImageView episodePoster;
        private final TextView episodeName;
        private final TextView episodeAirDate;
        private final TextView episodeNumber;

        EpisodesViewHolder(
                View view,
                EpisodesAdapter adapter
        ) {
            super(view);

            this.adapter = adapter;

            episodePoster = view.findViewById(R.id.poster_ic);
            episodeName = view.findViewById(R.id.season);
            episodeAirDate = view.findViewById(R.id.air_date);
            episodeNumber = view.findViewById(R.id.episodes);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAbsoluteAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Episode episode = adapter.getItem(pos);
                fragment.onItemClick(episode.getEpisodeNumber(), FragmentType.EPISODE);
            }
        }
    }
}
