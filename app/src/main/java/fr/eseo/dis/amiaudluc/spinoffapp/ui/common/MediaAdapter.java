package fr.eseo.dis.amiaudluc.spinoffapp.ui.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import fr.eseo.dis.amiaudluc.R;

public class MediaAdapter extends ListAdapter<AdapterData, MediaAdapter.MediaViewHolder> {
    private final ItemInterface fragment;
    private final Context ctx;
    private final boolean isHorizontal;

    public MediaAdapter(
            Context ctx,
            ItemInterface fragment,
            List<AdapterData> initialData,
            boolean isHorizontal
    ) {
        super(AdapterData.DIFF_CALLBACK);
        this.ctx = ctx;
        this.fragment = fragment;
        this.isHorizontal = isHorizontal;
        if (initialData != null) {
            submitList(new ArrayList<>(initialData));
        }
    }

    public void setMedias(List<AdapterData> series) {
        submitList(series != null ? new ArrayList<>(series) : null);
    }

    @NonNull
    @Override
    public MediaAdapter.MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isHorizontal ? R.layout.item_media_horizontal : R.layout.item_media;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new MediaViewHolder(view, fragment, this);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaAdapter.MediaViewHolder holder, int position) {
        AdapterData media = getItem(position);
        holder.bind(media, ctx.getResources().getString(R.string.base_url_poster_500));
    }

    public static class MediaViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final ImageView poster;
        private final ItemInterface fragment;
        private final MediaAdapter adapter;

        MediaViewHolder(View view, ItemInterface fragment, MediaAdapter adapter) {
            super(view);
            this.poster = view.findViewById(R.id.poster_ic);
            this.fragment = fragment;
            this.adapter = adapter;
            view.setOnClickListener(this);
        }

        public void bind(AdapterData media, String baseImageUrl) {
            poster.setImageResource(R.drawable.ic_launcher_foreground);
            if (media.posterPath() != null) {
                String link = baseImageUrl + media.posterPath();
                Picasso.get().load(link).fit().error(R.drawable.ic_launcher_foreground)
                        .into(poster);
            }

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
