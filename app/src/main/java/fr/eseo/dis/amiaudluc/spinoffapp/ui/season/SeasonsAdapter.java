package fr.eseo.dis.amiaudluc.spinoffapp.ui.season;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ItemSeasonBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.WatchedToggleListener;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;

/**
 * Adapter for displaying a list of seasons using View Binding.
 */
public class SeasonsAdapter extends ListAdapter<SeasonItem, SeasonsAdapter.SeasonViewHolder> {

    private final ItemInterface listener;
    private final WatchedToggleListener watchedToggleListener;

    private static final DiffUtil.ItemCallback<SeasonItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull SeasonItem oldItem, @NonNull SeasonItem newItem) {
                    return Objects.equals(oldItem.getSeason().getSeasonNumber(), newItem.getSeason().getSeasonNumber());
                }

                @Override
                public boolean areContentsTheSame(@NonNull SeasonItem oldItem, @NonNull SeasonItem newItem) {
                    return Objects.equals(oldItem, newItem);
                }
            };

    public SeasonsAdapter(ItemInterface listener, WatchedToggleListener watchedToggleListener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        this.watchedToggleListener = watchedToggleListener;
    }

    @NonNull
    @Override
    public SeasonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSeasonBinding binding = ItemSeasonBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SeasonViewHolder(binding, listener, watchedToggleListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class SeasonViewHolder extends RecyclerView.ViewHolder {

        private final ItemSeasonBinding binding;
        private final ItemInterface listener;
        private final WatchedToggleListener watchedToggleListener;

        SeasonViewHolder(ItemSeasonBinding binding, ItemInterface listener, WatchedToggleListener watchedToggleListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
            this.watchedToggleListener = watchedToggleListener;
        }

        void bind(SeasonItem item) {
            if (item == null) return;
            Season season = item.getSeason();
            if (season == null) return;

            binding.season.setText(season.getSeasonNumber() != null ?
                    String.valueOf(season.getSeasonNumber()) : "-");

            binding.episodes.setText(season.getEpisodeCount() != -1 ?
                    String.valueOf(season.getEpisodeCount()) : "-");

            binding.airDate.setText(season.getAirDate() != null ?
                    DateUtils.toDisplayString(season.getAirDate()) : "-");

            binding.posterIc.setImageResource(R.drawable.ic_launcher_foreground);
            if (season.getPosterPath() != null) {
                String baseUrl = itemView.getContext().getString(R.string.base_url_poster_500);
                String link = baseUrl + season.getPosterPath();
                Picasso.get()
                        .load(link)
                        .fit()
                        .centerCrop()
                        .error(R.drawable.ic_launcher_foreground)
                        .into(binding.posterIc);
            }

            if (item.isControlsEnabled()) {
                binding.watchedIcon.setVisibility(View.VISIBLE);
                binding.watchedIcon.setImageResource(item.isWatched() ? R.drawable.ic_watched : R.drawable.ic_unwatched);
                binding.watchedIcon.setOnClickListener(v -> {
                    if (watchedToggleListener != null && season.getSeasonNumber() != null) {
                        watchedToggleListener.onWatchedToggle(season.getSeasonNumber(), !item.isWatched());
                    }
                });

                if (season.getEpisodeCount() > 0) {
                    binding.seasonProgress.setVisibility(View.VISIBLE);
                    binding.seasonProgress.setMax(season.getEpisodeCount());
                    binding.seasonProgress.setProgress(item.isWatched() ? season.getEpisodeCount() : item.getWatchedCount());
                } else {
                    binding.seasonProgress.setVisibility(View.GONE);
                }
            } else {
                binding.watchedIcon.setVisibility(View.GONE);
                binding.watchedIcon.setOnClickListener(null);
                binding.seasonProgress.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(season.getSeasonNumber(), FragmentType.SEASON);
                }
            });
        }
    }
}
