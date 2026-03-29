package fr.eseo.dis.amiaudluc.spinoffapp.ui.artists;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;

/**
 * Created by lucasamiaud on 07/03/2018.
 */

public class ArtistsAdapter extends ListAdapter<Artist, ArtistsAdapter.ArtistViewHolder> {

    private final ItemInterface mListener;
    private final Context ctx;

    private static final DiffUtil.ItemCallback<Artist> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull Artist oldItem, @NonNull Artist newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Artist oldItem, @NonNull Artist newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public ArtistsAdapter(
            Context ctx,
            ItemInterface listener,
            List<Artist> data
    ) {
        super(DIFF_CALLBACK);
        this.mListener = listener;
        this.ctx = ctx;
        if (data != null) {
            submitList(new ArrayList<>(data));
        }
    }

    public void setArtist(List<Artist> artists) {
        submitList(artists != null ? new ArrayList<>(artists) : null);
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View seasonView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_realisator, parent, false);
        return new ArtistViewHolder(seasonView, mListener, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artist = getItem(position);
        if (artist != null) {
            holder.name.setText(ctx.getResources().getString(R.string.emptyField));
            if (artist.getName() != null) {
                holder.name.setText(artist.getName());
            }

            String link = ctx.getResources().getString(R.string.base_url_poster_500) + artist.getProfilePath();
            Picasso.get()
                    .load(link)
                    .error(R.drawable.ic_unknown)
                    .into(holder.avatar);
        } else {
            Picasso.get().load(R.drawable.ic_unknown).into(holder.avatar);
            holder.name.setText(ctx.getResources().getString(R.string.emptyField));
        }
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnCreateContextMenuListener {

        final ImageView avatar;
        final TextView name;

        private final ItemInterface fragment;
        private final ArtistsAdapter adapter;

        ArtistViewHolder(
                View view,
                ItemInterface fragment,
                ArtistsAdapter adapter
        ) {
            super(view);

            this.fragment = fragment;
            this.adapter = adapter;


            avatar = view.findViewById(R.id.avatar);
            name = view.findViewById(R.id.name);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAbsoluteAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Artist artist = adapter.getItem(pos);
                fragment.onItemClick(artist.getId(), FragmentType.ARTIST);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            int pos = getAbsoluteAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Artist artiste = adapter.getItem(pos);
                fragment.onRegisterContextMenu(view, artiste.getId());
            }
        }
    }
}
