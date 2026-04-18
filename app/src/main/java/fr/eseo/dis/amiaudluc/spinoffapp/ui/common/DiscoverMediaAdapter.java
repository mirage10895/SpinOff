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

public class DiscoverMediaAdapter extends ListAdapter<AdapterData, DiscoverMediaAdapter.MediaViewHolder> {
    private final String posterBaseUrl;
    private final ItemInterface fragment;

    public DiscoverMediaAdapter(
            ItemInterface fragment,
            String posterBaseUrl,
            List<AdapterData> initialData
    ) {
        super(AdapterData.DIFF_CALLBACK);
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
        AdapterData media = getItem(position);
        holder.bind(media, posterBaseUrl);
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

        public void bind(AdapterData media, String baseImageUrl) {
            binding.imagePoster.setImageResource(R.drawable.ic_launcher_foreground);
            if (media.posterPath() != null) {
                String link = baseImageUrl + media.posterPath();
                Picasso.get()
                        .load(link)
                        .fit()
                        .error(R.drawable.ic_launcher_foreground)
                        .into(binding.imagePoster);
            }
            binding.textTitle.setText(media.name());

            fragment.onRegisterContextMenu(itemView, media.id());
        }

        @Override
        public void onClick(View v) {
            int pos = getAbsoluteAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                AdapterData media = adapter.getItem(pos);
                fragment.onItemClick(media.id(), media.fragmentType());
            }
        }
    }
}
