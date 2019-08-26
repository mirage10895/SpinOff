package fr.eseo.dis.amiaudluc.spinoffapp.ui.artists;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Artist;

/**
 * Created by lucasamiaud on 07/03/2018.
 */

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ArtistViewHolder> {

    private List<Artist> artists;
    private final ItemInterface mListener;
    private final Context ctx;

    public ArtistsAdapter(Context ctx, ItemInterface listener, List<Artist> data){
        this.mListener = listener;
        this.ctx = ctx;
        this.setArtist(data);
    }

    public void setArtist(List<Artist> artists){
        this.artists = artists;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View seasonView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_realisator, parent, false);
        return new ArtistViewHolder(seasonView);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        if (getItemCount() != 0) {
            holder.name.setText(ctx.getResources().getString(R.string.emptyField));
            if (this.artists.get(position).getName() != null) {
                holder.name.setTextColor(ctx.getColor(R.color.colorAccent));
                holder.name.setText(this.artists.get(position).getName());
            }

            String link = ctx.getResources().getString(R.string.base_url_poster_500) + this.artists.get(position).getProfilePath();
            Picasso.with(ctx)
                    .load(link)
                    .error(R.drawable.ic_unknown)
                    .into(holder.avatar);
        }else {
            Picasso.with(ctx).load(R.drawable.ic_unknown).into(holder.avatar);
            holder.name.setText(ctx.getResources().getString(R.string.emptyField));
        }
    }

    public String getType() {
        return Media.ARTIST;
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final View view;

        final ImageView avatar;
        final TextView name;

        private SearchInterface frag = (SearchInterface) mListener;

        ArtistViewHolder(View view) {
            super(view);
            this.view = view;

            avatar = view.findViewById(R.id.avatar);
            name = view.findViewById(R.id.name);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            frag.setType(Media.ARTIST);
            Artist artist = artists.get(getAdapterPosition());
            mListener.onItemClick(artist.getId());
        }

    }
}