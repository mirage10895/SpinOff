package fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ItemLetterHeaderBinding;
import fr.eseo.dis.amiaudluc.databinding.ItemLibraryMediaBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.LibraryTileListener;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.library.LibraryItem;

/**
 * Renders the library list screen: letter dividers interleaved with media tiles.
 * <p>
 * A tile shows only what the local library stores. There is no watched date, no watch count and
 * no media format in the model, so the tile is built from the title, the poster, the rating, the
 * release year, the first credited creator and the genres. Series that have been started
 * additionally show their global episode progress. Actions live behind the overflow button: the
 * tile itself has no long press menu.
 */
public class LibraryAdapter extends ListAdapter<LibraryRow, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LETTER_HEADER = 0;
    private static final int VIEW_TYPE_MEDIA_TILE = 1;

    private final Context context;
    private final LibraryTileListener listener;
    private final String posterBaseUrl;
    private final String separator;

    public LibraryAdapter(Context context, LibraryTileListener listener) {
        super(LibraryRow.DIFF_CALLBACK);
        this.context = context;
        this.listener = listener;
        this.posterBaseUrl = context.getString(R.string.base_url_poster_185);
        this.separator = context.getString(R.string.library_list_separator);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position) instanceof LibraryRow.LetterHeader
                ? VIEW_TYPE_LETTER_HEADER
                : VIEW_TYPE_MEDIA_TILE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_LETTER_HEADER) {
            return new LetterHeaderViewHolder(ItemLetterHeaderBinding.inflate(inflater, parent, false));
        }
        return new MediaTileViewHolder(
                ItemLibraryMediaBinding.inflate(inflater, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LibraryRow row = getItem(position);
        if (holder instanceof LetterHeaderViewHolder headerHolder
                && row instanceof LibraryRow.LetterHeader header) {
            headerHolder.bind(header);
        } else if (holder instanceof MediaTileViewHolder tileHolder
                && row instanceof LibraryRow.MediaTile tile) {
            tileHolder.bind(tile.item());
        }
    }

    /** Joins the non blank parts with the shared separator, skipping the missing ones. */
    private String join(String... parts) {
        List<String> kept = new ArrayList<>();
        for (String part : parts) {
            if (part != null && !part.isBlank()) {
                kept.add(part);
            }
        }
        return String.join(separator, kept);
    }

    private class LetterHeaderViewHolder extends RecyclerView.ViewHolder {

        private final ItemLetterHeaderBinding binding;

        LetterHeaderViewHolder(ItemLetterHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(LibraryRow.LetterHeader header) {
            String letter = String.valueOf(header.letter());
            binding.letter.setText(letter);
            binding.count.setText(context.getResources()
                    .getQuantityString(R.plurals.library_list_letter_count, header.count(), header.count()));
            binding.indexLabel.setText(context.getString(R.string.library_list_index_label, letter));
        }
    }

    private class MediaTileViewHolder extends RecyclerView.ViewHolder {

        private final ItemLibraryMediaBinding binding;

        MediaTileViewHolder(ItemLibraryMediaBinding binding, LibraryTileListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(view -> {
                int position = getAbsoluteAdapterPosition();
                if (position != RecyclerView.NO_POSITION
                        && getItem(position) instanceof LibraryRow.MediaTile tile) {
                    listener.onTileClick(tile.item());
                }
            });
            binding.overflow.setOnClickListener(view -> {
                int position = getAbsoluteAdapterPosition();
                if (position != RecyclerView.NO_POSITION
                        && getItem(position) instanceof LibraryRow.MediaTile tile) {
                    listener.onTileMenu(view, tile.item());
                }
            });
        }

        void bind(LibraryItem item) {
            binding.title.setText(item.title());
            binding.posterIc.setImageResource(R.drawable.ic_launcher_foreground);
            if (item.posterPath() != null) {
                Picasso.get()
                        .load(posterBaseUrl + item.posterPath())
                        .fit()
                        .centerCrop()
                        .error(R.drawable.ic_launcher_foreground)
                        .into(binding.posterIc);
            }

            binding.meta.setText(buildMeta(item));
            bindCreators(item);
            bindProgress(item);

            boolean hasRating = bindRating(item);
            boolean hasGenre = bindGenre(item);
            binding.tagsRow.setVisibility(hasRating || hasGenre ? View.VISIBLE : View.GONE);

            binding.inProgressBadge.setVisibility(item.isInProgress() ? View.VISIBLE : View.GONE);
            binding.getRoot().setContentDescription(item.title());
        }

        private String buildMeta(LibraryItem item) {
            if (item.isSerie()) {
                return join(
                        context.getString(R.string.library_list_type_series),
                        seasonCountLabel(item.seasonCount()));
            }
            return join(
                    context.getString(R.string.library_list_type_movie),
                    runtimeLabel(item.runtimeMinutes()));
        }

        private void bindCreators(LibraryItem item) {
            String subtitle = join(item.creators(), item.releaseYear() != null
                    ? String.valueOf(item.releaseYear())
                    : null);
            binding.creators.setText(subtitle);
            binding.creators.setVisibility(subtitle.isEmpty() ? View.GONE : View.VISIBLE);
        }

        private boolean bindRating(LibraryItem item) {
            Double voteAverage = item.voteAverage();
            if (voteAverage == null || voteAverage <= 0) {
                binding.rating.setVisibility(View.GONE);
                return false;
            }
            binding.rating.setVisibility(View.VISIBLE);
            binding.rating.setText(context.getString(
                    R.string.library_list_rating, String.format(Locale.getDefault(), "%.1f", voteAverage)));
            return true;
        }

        private boolean bindGenre(LibraryItem item) {
            String genres = item.genres();
            if (genres == null || genres.isBlank()) {
                binding.genre.setVisibility(View.GONE);
                return false;
            }
            int comma = genres.indexOf(',');
            binding.genre.setText((comma >= 0 ? genres.substring(0, comma) : genres).trim());
            binding.genre.setVisibility(View.VISIBLE);
            return true;
        }

        private void bindProgress(LibraryItem item) {
            if (!item.showsEpisodeProgress()) {
                binding.progressBlock.setVisibility(View.GONE);
                return;
            }
            binding.progressBlock.setVisibility(View.VISIBLE);
            binding.episodesProgress.setText(context.getString(
                    R.string.library_list_episodes_progress, item.watchedEpisodes(), item.totalEpisodes()));
            binding.percent.setText(context.getString(
                    R.string.library_list_percent, item.progressPercent()));
            binding.progress.setMax(item.totalEpisodes());
            binding.progress.setProgress(item.watchedEpisodes());
        }

        private String seasonCountLabel(Integer seasonCount) {
            if (seasonCount == null || seasonCount <= 0) {
                return null;
            }
            return context.getResources()
                    .getQuantityString(R.plurals.library_list_seasons_count, seasonCount, seasonCount);
        }

        private String runtimeLabel(Integer runtimeMinutes) {
            if (runtimeMinutes == null || runtimeMinutes <= 0) {
                return null;
            }
            return DateUtils.displayDuration(Duration.ofMinutes(runtimeMinutes));
        }
    }
}
