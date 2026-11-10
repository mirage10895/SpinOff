package fr.eseo.dis.amiaudluc.spinoffapp.ui.episode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ItemEpisodeBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.MediaTileTagBinder;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.MediaTileText;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.WatchedToggleBinder;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.WatchedToggleListener;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;

/**
 * Adapter for displaying a list of episodes using View Binding.
 * <p>
 * The row follows the shared media tile grammar: the metadata column carries the episode position
 * and runtime, the episode name, the air date and the rating, while the watched toggle sits in
 * the trailing 48dp slot. An episode is watched or not, so there is no progress block.
 */
public class EpisodesAdapter extends ListAdapter<EpisodeItem, EpisodesAdapter.EpisodesViewHolder> {

    private final ItemInterface listener;
    private final WatchedToggleListener watchedToggleListener;

    /** Payload emitted when only the watched state of an item changed. */
    private static final Object PAYLOAD_WATCHED = new Object();

    private static final DiffUtil.ItemCallback<EpisodeItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull EpisodeItem oldItem, @NonNull EpisodeItem newItem) {
                    return Objects.equals(oldItem.getEpisode().getEpisodeNumber(), newItem.getEpisode().getEpisodeNumber());
                }

                @Override
                public boolean areContentsTheSame(@NonNull EpisodeItem oldItem, @NonNull EpisodeItem newItem) {
                    return Objects.equals(oldItem, newItem);
                }

                @Override
                public Object getChangePayload(@NonNull EpisodeItem oldItem, @NonNull EpisodeItem newItem) {
                    if (oldItem.isWatched() != newItem.isWatched()
                            || oldItem.isControlsEnabled() != newItem.isControlsEnabled()) {
                        return PAYLOAD_WATCHED;
                    }
                    return null;
                }
            };

    public EpisodesAdapter(ItemInterface listener, WatchedToggleListener watchedToggleListener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        this.watchedToggleListener = watchedToggleListener;
    }

    @NonNull
    @Override
    public EpisodesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEpisodeBinding binding = ItemEpisodeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new EpisodesViewHolder(binding, listener, watchedToggleListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && payloads.contains(PAYLOAD_WATCHED)) {
            holder.bindWatchedState(getItem(position));
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    public static class EpisodesViewHolder extends RecyclerView.ViewHolder {

        private final ItemEpisodeBinding binding;
        private final ItemInterface listener;
        private final WatchedToggleListener watchedToggleListener;

        EpisodesViewHolder(ItemEpisodeBinding binding, ItemInterface listener, WatchedToggleListener watchedToggleListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
            this.watchedToggleListener = watchedToggleListener;
        }

        void bind(EpisodeItem item) {
            if (item == null || item.getEpisode() == null) return;
            Episode episode = item.getEpisode();
            Context context = itemView.getContext();

            binding.meta.setText(buildMeta(context, episode));
            binding.title.setText(buildTitle(context, episode));
            bindAirDate(episode);
            bindStill(context, episode);
            bindRating(context, episode);
            bindWatchedState(item);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(episode.getEpisodeNumber(), FragmentType.EPISODE);
                }
            });
        }

        /** Updates only the watched toggle, used for partial binds driven by DiffUtil payloads. */
        void bindWatchedState(EpisodeItem item) {
            if (item == null || item.getEpisode() == null) return;
            Integer episodeNumber = item.getEpisode().getEpisodeNumber();

            WatchedToggleBinder.bind(
                    binding.watchedToggle.getRoot(),
                    binding.watchedToggle.toggleIcon,
                    item.isControlsEnabled(),
                    item.isWatched(),
                    () -> {
                        if (watchedToggleListener != null && episodeNumber != null) {
                            watchedToggleListener.onWatchedToggle(episodeNumber, !item.isWatched());
                        }
                    });
        }

        private String buildMeta(Context context, Episode episode) {
            Integer seasonNumber = episode.getSeasonNumber();
            Integer episodeNumber = episode.getEpisodeNumber();
            return MediaTileText.join(context,
                    seasonNumber != null && episodeNumber != null
                            ? context.getString(R.string.media_tile_episode_meta, seasonNumber, episodeNumber)
                            : null,
                    runtimeLabel(episode.getRuntime()));
        }

        private String buildTitle(Context context, Episode episode) {
            String name = episode.getName();
            return name != null && !name.isBlank() ? name : context.getString(R.string.emptyField);
        }

        private void bindAirDate(Episode episode) {
            String airDate = episode.getAirDate() != null
                    ? DateUtils.toDisplayString(episode.getAirDate())
                    : null;
            binding.airDate.setText(airDate);
            binding.airDate.setVisibility(airDate == null ? View.GONE : View.VISIBLE);
        }

        private void bindStill(Context context, Episode episode) {
            binding.stillIc.setImageResource(R.drawable.ic_launcher_foreground);
            if (episode.getStillPath() != null) {
                Picasso.get()
                        .load(context.getString(R.string.base_url_poster_300) + episode.getStillPath())
                        .fit()
                        .centerCrop()
                        .error(R.drawable.ic_launcher_foreground)
                        .into(binding.stillIc);
            }
        }

        private void bindRating(Context context, Episode episode) {
            boolean hasRating = MediaTileTagBinder.bindRating(
                    context, binding.rating, episode.getVoteAverage());
            binding.tagsRow.setVisibility(hasRating ? View.VISIBLE : View.GONE);
        }

        private String runtimeLabel(Integer runtimeMinutes) {
            if (runtimeMinutes == null || runtimeMinutes <= 0) {
                return null;
            }
            return DateUtils.displayDuration(Duration.ofMinutes(runtimeMinutes));
        }
    }
}
