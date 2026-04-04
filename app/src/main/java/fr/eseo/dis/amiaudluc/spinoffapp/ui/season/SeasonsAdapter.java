package fr.eseo.dis.amiaudluc.spinoffapp.ui.season;

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
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;

/**
 * Created by lucasamiaud on 07/03/2018.
 */

public class SeasonsAdapter extends ListAdapter<Season, SeasonsAdapter.SeasonViewHolder> {
    private final ItemInterface mListener;
    private final Context ctx;

    private static final DiffUtil.ItemCallback<Season> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull Season oldItem, @NonNull Season newItem) {
                    return Objects.equals(oldItem.getSeasonNumber(), newItem.getSeasonNumber());

                }

                @Override
                public boolean areContentsTheSame(@NonNull Season oldItem, @NonNull Season newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public SeasonsAdapter(Context ctx, ItemInterface listener, List<Season> data) {
        super(DIFF_CALLBACK);
        this.mListener = listener;
        this.ctx = ctx;

        if (data != null) {
            submitList(new ArrayList<>(data));
        }
    }

    @NonNull
    @Override
    public SeasonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View seasonView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_season, parent, false);
        return new SeasonViewHolder(seasonView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonViewHolder holder, int position) {
        Season season = getItem(position);
        if (season != null) {
            holder.seasonPoster.setImageResource(R.drawable.ic_launcher_foreground);
            if (season.getPosterPath() != null) {
                String link = ctx.getResources().getString(R.string.base_url_poster_500) + season.getPosterPath();
                Picasso.get()
                        .load(link)
                        .fit()
                        .error(R.drawable.ic_launcher_foreground)
                        .into(holder.seasonPoster);
            }

            holder.seasonNumber.setText(R.string.emptyField);
            if (season.getSeasonNumber() != null) {
                holder.seasonNumber.setText(String.valueOf(season.getSeasonNumber()));
            }

            holder.seasonNumberEpisodes.setText(R.string.emptyField);
            if (season.getEpisodeCount() != -1) {
                holder.seasonNumberEpisodes.setText(String.valueOf(season.getEpisodeCount()));
            }
            holder.seasonAirDate.setText(R.string.emptyField);
            if (season.getAirDate() != null) {
                holder.seasonAirDate.setText(DateUtils.toDisplayString(season.getAirDate()));
            }
        }
    }

    public class SeasonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final SeasonsAdapter adapter;

        private final ImageView seasonPoster;
        private final TextView seasonAirDate;
        private final TextView seasonNumberEpisodes;
        private final TextView seasonNumber;

        SeasonViewHolder(
                View view,
                SeasonsAdapter adapter
        ) {
            super(view);

            this.adapter = adapter;

            seasonPoster = view.findViewById(R.id.poster_ic);
            seasonAirDate = view.findViewById(R.id.air_date);
            seasonNumberEpisodes = view.findViewById(R.id.episodes);
            seasonNumber = view.findViewById(R.id.season);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAbsoluteAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Season season = adapter.getItem(pos);
                mListener.onItemClick(season.getSeasonNumber(), FragmentType.SEASON);
            }
        }

    }
}
