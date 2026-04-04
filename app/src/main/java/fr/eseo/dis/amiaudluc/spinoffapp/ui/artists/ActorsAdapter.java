package fr.eseo.dis.amiaudluc.spinoffapp.ui.artists;

import android.content.Context;
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
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Artist;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;

/**
 * Created by lucasamiaud on 21/03/2018.
 */

public class ActorsAdapter extends ListAdapter<Artist, ActorsAdapter.ArtistViewHolder> {
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

    public ActorsAdapter(
            Context ctx,
            ItemInterface listener,
            List<Artist> initialData
    ) {
        super(DIFF_CALLBACK);
        this.mListener = listener;
        this.ctx = ctx;
        if (initialData != null) {
            submitList(new ArrayList<>(initialData));
        }
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View seasonView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_actor, parent, false);
        return new ArtistViewHolder(seasonView, mListener, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artist = getItem(position);
        if (artist != null) {
            holder.name.setText(ctx.getResources().getString(R.string.emptyField));
            if (artist.getName() != null) {
                holder.name.setText(artist.getName());
                holder.characterName.setText(artist.getCharacter());
            }

            String link = ctx.getResources().getString(R.string.base_url_poster_500) + artist.getProfilePath();
            Picasso.get()
                    .load(link)
                    .error(R.drawable.ic_unknown)
                    .into(holder.avatar);
        } else {
            Picasso.get()
                    .load(R.drawable.ic_unknown)
                    .into(holder.avatar);
            holder.name.setText(ctx.getResources().getString(R.string.emptyField));
        }
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        final ImageView avatar;
        final TextView name;
        final TextView characterName;
        final ItemInterface fragment;
        final ActorsAdapter adapter;

        ArtistViewHolder(
                View view,
                ItemInterface fragment,
                ActorsAdapter adapter
        ) {
            super(view);

            this.fragment = fragment;
            this.adapter = adapter;

            avatar = view.findViewById(R.id.avatar);
            name = view.findViewById(R.id.name);
            characterName = view.findViewById(R.id.character_name);

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
    }
}
