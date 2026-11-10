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

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ItemLetterHeaderBinding;
import fr.eseo.dis.amiaudluc.databinding.ItemLibraryMediaBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.LibraryTileListener;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.MediaTileProgressBinder;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.MediaTileTagBinder;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.MediaTileText;
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

    public LibraryAdapter(Context context, LibraryTileListener listener) {
        super(LibraryRow.DIFF_CALLBACK);
        this.context = context;
        this.listener = listener;
        this.posterBaseUrl = context.getString(R.string.base_url_poster_185);
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
                return MediaTileText.join(context,
                        context.getString(R.string.media_tile_type_series),
                        seasonCountLabel(item.seasonCount()));
            }
            return MediaTileText.join(context,
                    context.getString(R.string.media_tile_type_movie),
                    runtimeLabel(item.runtimeMinutes()));
        }

        private void bindCreators(LibraryItem item) {
            String subtitle = MediaTileText.join(context, item.creators(),
                    item.releaseYear() != null ? String.valueOf(item.releaseYear()) : null);
            binding.creators.setText(subtitle);
            binding.creators.setVisibility(subtitle.isEmpty() ? View.GONE : View.VISIBLE);
        }

        private boolean bindRating(LibraryItem item) {
            return MediaTileTagBinder.bindRating(context, binding.rating, item.voteAverage());
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
            MediaTileProgressBinder.bind(
                    binding.progressBlock,
                    item.showsEpisodeProgress(),
                    item.watchedEpisodes(),
                    item.totalEpisodes());
        }

        private String seasonCountLabel(Integer seasonCount) {
            if (seasonCount == null || seasonCount <= 0) {
                return null;
            }
            return context.getResources()
                    .getQuantityString(R.plurals.media_tile_seasons_count, seasonCount, seasonCount);
        }

        private String runtimeLabel(Integer runtimeMinutes) {
            if (runtimeMinutes == null || runtimeMinutes <= 0) {
                return null;
            }
            return DateUtils.displayDuration(Duration.ofMinutes(runtimeMinutes));
        }
    }
}
