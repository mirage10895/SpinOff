package fr.eseo.dis.amiaudluc.spinoffapp.ui.episode;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
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
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;

/**
 * Adapter for displaying a list of episodes using View Binding.
 */
public class EpisodesAdapter extends ListAdapter<Episode, EpisodesAdapter.EpisodesViewHolder> {

    private final ItemInterface listener;

    private static final DiffUtil.ItemCallback<Episode> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull Episode oldItem, @NonNull Episode newItem) {
                    return Objects.equals(oldItem.getEpisodeNumber(), newItem.getEpisodeNumber());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Episode oldItem, @NonNull Episode newItem) {
                    return Objects.equals(oldItem, newItem);
                }
            };

    public EpisodesAdapter(ItemInterface listener, List<Episode> episodes) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        if (episodes != null) {
            submitList(new ArrayList<>(episodes));
        }
    }

    @NonNull
    @Override
    public EpisodesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSeasonBinding binding = ItemSeasonBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new EpisodesViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class EpisodesViewHolder extends RecyclerView.ViewHolder {

        private final ItemSeasonBinding binding;
        private final ItemInterface listener;

        EpisodesViewHolder(ItemSeasonBinding binding, ItemInterface listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        void bind(Episode episode) {
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

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(episode.getEpisodeNumber(), FragmentType.EPISODE);
                }
            });
        }
    }
}
