package fr.eseo.dis.amiaudluc.spinoffapp.ui.episode;

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
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.WatchedToggleListener;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;

/**
 * Adapter for displaying a list of episodes using View Binding.
 */
public class EpisodesAdapter extends ListAdapter<EpisodeItem, EpisodesAdapter.EpisodesViewHolder> {

    private final ItemInterface listener;
    private final WatchedToggleListener watchedToggleListener;

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
            };

    public EpisodesAdapter(ItemInterface listener, WatchedToggleListener watchedToggleListener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        this.watchedToggleListener = watchedToggleListener;
    }

    @NonNull
    @Override
    public EpisodesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSeasonBinding binding = ItemSeasonBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new EpisodesViewHolder(binding, listener, watchedToggleListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class EpisodesViewHolder extends RecyclerView.ViewHolder {

        private final ItemSeasonBinding binding;
        private final ItemInterface listener;
        private final WatchedToggleListener watchedToggleListener;

        EpisodesViewHolder(ItemSeasonBinding binding, ItemInterface listener, WatchedToggleListener watchedToggleListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
            this.watchedToggleListener = watchedToggleListener;
        }

        void bind(EpisodeItem item) {
            if (item == null) return;
            Episode episode = item.getEpisode();
            if (episode == null) return;

            // Update labels to match Episode context
            binding.textNumberSeason.setText(episode.getName() != null ? episode.getName() : "-");
            binding.season.setVisibility(View.GONE);

            String episodeText = "S" + episode.getSeasonNumber() + ":E" + episode.getEpisodeNumber();
            binding.textNumberEpisodes.setText(episodeText);
            binding.episodes.setVisibility(View.GONE);

            binding.textAirDate.setText(episode.getAirDate() != null ?
                    DateUtils.toDisplayString(episode.getAirDate()) : "-");
            binding.airDate.setVisibility(View.GONE);

            binding.posterIc.setImageResource(R.drawable.ic_launcher_foreground);
            if (episode.getStillPath() != null) {
                String baseUrl = itemView.getContext().getString(R.string.base_url_poster_500);
                String link = baseUrl + episode.getStillPath();
                Picasso.get().load(link)
                        .fit()
                        .centerCrop()
                        .error(R.drawable.ic_launcher_foreground)
                        .into(binding.posterIc);
            }

            if (item.isControlsEnabled()) {
                binding.watchedIcon.setVisibility(View.VISIBLE);
                binding.watchedIcon.setImageResource(item.isWatched() ? R.drawable.ic_watched : R.drawable.ic_unwatched);
                binding.watchedIcon.setOnClickListener(v -> {
                    if (watchedToggleListener != null && episode.getEpisodeNumber() != null) {
                        watchedToggleListener.onWatchedToggle(episode.getEpisodeNumber(), !item.isWatched());
                    }
                });
            } else {
                binding.watchedIcon.setVisibility(View.GONE);
                binding.watchedIcon.setOnClickListener(null);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(episode.getEpisodeNumber(), FragmentType.EPISODE);
                }
            });
        }
    }
}
