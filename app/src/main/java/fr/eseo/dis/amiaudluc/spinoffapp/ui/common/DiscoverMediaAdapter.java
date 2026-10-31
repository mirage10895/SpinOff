package fr.eseo.dis.amiaudluc.spinoffapp.ui.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ItemMediaCardBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter.AdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter.DiscoverItem;

/**
 * Grid adapter of the discovery screens. It renders a poster per title and flags the titles that
 * are already stored in the local library: a bookmark badge sits at the top-end corner and the
 * card gets an accent outline.
 * <p>
 * Library membership is part of {@link DiscoverItem}, so updates go through
 * {@link #submitList(List)} and DiffUtil only rebinds the rows whose membership changed.
 */
public class DiscoverMediaAdapter extends ListAdapter<DiscoverItem, DiscoverMediaAdapter.MediaViewHolder> {
    private final String posterBaseUrl;
    private final ItemInterface fragment;

    public DiscoverMediaAdapter(
            ItemInterface fragment,
            String posterBaseUrl,
            List<DiscoverItem> initialData
    ) {
        super(DiscoverItem.DIFF_CALLBACK);
        this.fragment = fragment;
        this.posterBaseUrl = posterBaseUrl;
        if (initialData != null) {
            submitList(new ArrayList<>(initialData));
        }
    }

    @NonNull
    @Override
    public DiscoverMediaAdapter.MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMediaCardBinding binding = ItemMediaCardBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new DiscoverMediaAdapter.MediaViewHolder(binding, this.fragment, this);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscoverMediaAdapter.MediaViewHolder holder, int position) {
        DiscoverItem item = getItem(position);
        holder.bind(item.media(), posterBaseUrl, item.inLibrary());
    }

    public static class MediaViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final ItemMediaCardBinding binding;
        private final ItemInterface fragment;
        private final DiscoverMediaAdapter adapter;

        MediaViewHolder(
                ItemMediaCardBinding binding,
                ItemInterface fragment,
                DiscoverMediaAdapter adapter
        ) {
            super(binding.getRoot());
            this.binding = binding;
            this.fragment = fragment;
            this.adapter = adapter;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(AdapterData media, String baseImageUrl, boolean inLibrary) {
            binding.imagePoster.setImageResource(R.drawable.ic_launcher_foreground);
            if (media.posterPath() != null) {
                String link = baseImageUrl + media.posterPath();
                Picasso.get()
                        .load(link)
                        .fit()
                        .error(R.drawable.ic_launcher_foreground)
                        .into(binding.imagePoster);
            }
            // binding.bannerInfo is used as an optional light information banner
            if (media.banner() != null && !media.banner().isEmpty()) {
                binding.bannerInfo.setText(media.banner());
                binding.bannerInfo.setVisibility(View.VISIBLE);
            } else {
                binding.bannerInfo.setVisibility(View.GONE);
            }

            // A title already in the library gets a bookmark badge and an accent outline, so it
            // can be spotted at a glance without reading anything.
            binding.libraryBadge.setVisibility(inLibrary ? View.VISIBLE : View.GONE);
            int strokeWidth = inLibrary
                    ? itemView.getResources().getDimensionPixelSize(R.dimen.discovery_in_library_stroke_width)
                    : 0;
            binding.getRoot().setStrokeWidth(strokeWidth);

            fragment.onRegisterContextMenu(itemView, media.id());
        }

        @Override
        public void onClick(View v) {
            int pos = getAbsoluteAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                DiscoverItem item = adapter.getItem(pos);
                fragment.onItemClick(item.media().id(), item.media().fragmentType());
            }
        }
    }
}
