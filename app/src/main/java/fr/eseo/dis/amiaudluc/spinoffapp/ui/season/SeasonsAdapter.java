package fr.eseo.dis.amiaudluc.spinoffapp.ui.season;

import android.view.LayoutInflater;
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
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;

/**
 * Adapter for displaying a list of seasons using View Binding.
 */
public class SeasonsAdapter extends ListAdapter<Season, SeasonsAdapter.SeasonViewHolder> {

    private final ItemInterface listener;

    private static final DiffUtil.ItemCallback<Season> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull Season oldItem, @NonNull Season newItem) {
                    return Objects.equals(oldItem.getSeasonNumber(), newItem.getSeasonNumber());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Season oldItem, @NonNull Season newItem) {
                    return Objects.equals(oldItem, newItem);
                }
            };

    public SeasonsAdapter(ItemInterface listener, List<Season> data) {
        super(DIFF_CALLBACK);
        this.listener = listener;

        if (data != null) {
            submitList(new ArrayList<>(data));
        }
    }

    @NonNull
    @Override
    public SeasonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSeasonBinding binding = ItemSeasonBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SeasonViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class SeasonViewHolder extends RecyclerView.ViewHolder {

        private final ItemSeasonBinding binding;
        private final ItemInterface listener;

        SeasonViewHolder(ItemSeasonBinding binding, ItemInterface listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        void bind(Season season) {
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

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(season.getSeasonNumber(), FragmentType.SEASON);
                }
            });
        }
    }
}
