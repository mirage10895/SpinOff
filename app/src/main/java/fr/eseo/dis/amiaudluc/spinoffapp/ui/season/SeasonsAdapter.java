package fr.eseo.dis.amiaudluc.spinoffapp.ui.season;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;
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
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.MediaTileProgressBinder;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.MediaTileTagBinder;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.MediaTileText;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.WatchedToggleBinder;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.WatchedToggleListener;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;

/**
 * Adapter for displaying a list of seasons using View Binding.
 * <p>
 * The row follows the shared media tile grammar: the metadata column carries the season label and
 * episode count, the season name, the air date, the rating, then the watched progress, while the
 * watched toggle sits in the trailing 48dp slot.
 */
public class SeasonsAdapter extends ListAdapter<SeasonItem, SeasonsAdapter.SeasonViewHolder> {

    private final ItemInterface listener;
    private final WatchedToggleListener watchedToggleListener;

    /** Payload emitted when only the watched state or progress of an item changed. */
    private static final Object PAYLOAD_WATCHED = new Object();

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

                @Override
                public Object getChangePayload(@NonNull SeasonItem oldItem, @NonNull SeasonItem newItem) {
                    if (oldItem.isWatched() != newItem.isWatched()
                            || oldItem.getWatchedCount() != newItem.getWatchedCount()
                            || oldItem.isControlsEnabled() != newItem.isControlsEnabled()) {
                        return PAYLOAD_WATCHED;
                    }
                    return null;
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

    @Override
    public void onBindViewHolder(@NonNull SeasonViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && payloads.contains(PAYLOAD_WATCHED)) {
            holder.bindWatchedState(getItem(position));
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
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
            if (item == null || item.getSeason() == null) return;
            Season season = item.getSeason();
            Context context = itemView.getContext();

            binding.meta.setText(buildMeta(context, season));
            binding.title.setText(buildTitle(context, season));
            bindAirDate(season);
            bindPoster(context, season);
            bindRating(context, season);
            bindWatchedState(item);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(season.getSeasonNumber(), FragmentType.SEASON);
                }
            });
        }

        /**
         * Updates only the watched toggle and the progress block, used for DiffUtil payload binds.
         */
        void bindWatchedState(SeasonItem item) {
            if (item == null || item.getSeason() == null) return;
            Season season = item.getSeason();
            Integer seasonNumber = season.getSeasonNumber();
            int totalEpisodes = season.getEpisodeCount();
            int watchedEpisodes = item.isWatched() ? totalEpisodes : item.getWatchedCount();

            WatchedToggleBinder.bind(
                    binding.watchedToggle.getRoot(),
                    binding.watchedToggle.toggleIcon,
                    item.isControlsEnabled(),
                    item.isWatched(),
                    () -> {
                        if (watchedToggleListener != null && seasonNumber != null) {
                            watchedToggleListener.onWatchedToggle(seasonNumber, !item.isWatched());
                        }
                    });

            MediaTileProgressBinder.bind(
                    binding.progressBlock,
                    item.isControlsEnabled() && totalEpisodes > 0,
                    watchedEpisodes,
                    totalEpisodes);
        }

        private String buildMeta(Context context, Season season) {
            Integer seasonNumber = season.getSeasonNumber();
            int episodeCount = season.getEpisodeCount();
            return MediaTileText.join(context,
                    seasonNumber != null
                            ? context.getString(R.string.media_tile_season_meta, seasonNumber)
                            : null,
                    episodeCount > 0
                            ? context.getResources().getQuantityString(
                                    R.plurals.media_tile_episodes_count, episodeCount, episodeCount)
                            : null);
        }

        private String buildTitle(Context context, Season season) {
            String name = season.getName();
            if (name != null && !name.isBlank()) {
                return name;
            }
            Integer seasonNumber = season.getSeasonNumber();
            return seasonNumber != null
                    ? context.getString(R.string.media_tile_season_meta, seasonNumber)
                    : context.getString(R.string.emptyField);
        }

        private void bindAirDate(Season season) {
            String airDate = season.getAirDate() != null
                    ? DateUtils.toDisplayString(season.getAirDate())
                    : null;
            binding.airDate.setText(airDate);
            binding.airDate.setVisibility(airDate == null ? View.GONE : View.VISIBLE);
        }

        private void bindPoster(Context context, Season season) {
            binding.posterIc.setImageResource(R.drawable.ic_launcher_foreground);
            if (season.getPosterPath() != null) {
                Picasso.get()
                        .load(context.getString(R.string.base_url_poster_500) + season.getPosterPath())
                        .fit()
                        .centerCrop()
                        .error(R.drawable.ic_launcher_foreground)
                        .into(binding.posterIc);
            }
        }

        private void bindRating(Context context, Season season) {
            boolean hasRating = MediaTileTagBinder.bindRating(
                    context, binding.rating, season.getVoteAverage());
            binding.tagsRow.setVisibility(hasRating ? View.VISIBLE : View.GONE);
        }
    }
}
