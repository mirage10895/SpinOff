package fr.eseo.dis.amiaudluc.spinoffapp.artists;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.ConstUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Artist;

/**
 * Created by lucasamiaud on 07/03/2018.
 */

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ArtistViewHolder> {

    private ArrayList<Artist> artists;
    private final ItemInterface mListener;
    private final Context ctx;
    private static final String TYPE = ConstUtils.REALISATOR;

    public ArtistsAdapter(Context ctx, ItemInterface listener, ArrayList<Artist> data){
        this.mListener = listener;
        this.ctx = ctx;
        this.setArtist(data);
    }

    public void setArtist(ArrayList<Artist> artists){
        this.artists = artists;
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View seasonView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_realisator, parent, false);
        return new ArtistViewHolder(seasonView);
    }

    @Override
    public void onBindViewHolder(ArtistViewHolder holder, int position) {
        if (getItemCount() != 0) {
            holder.name.setText(ctx.getResources().getString(R.string.emptyField));
            if (this.artists.get(position).getName() != null) {
                holder.name.setTextColor(ctx.getColor(R.color.colorAccent));
                holder.name.setText(this.artists.get(position).getName());
            }

            String link = ctx.getResources().getString(R.string.base_url_poster_500) + this.artists.get(position).getProfilePath();
            Picasso.with(ctx).load(link).placeholder(R.drawable.ic_unknown).error(R.drawable.ic_unknown)
                    .into(holder.avatar);
        }else {
            Picasso.with(ctx).load(R.drawable.ic_unknown).into(holder.avatar);
            holder.name.setText(ctx.getResources().getString(R.string.emptyField));
        }
    }

    public static String getType() {
        return TYPE;
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
            frag.setType(ConstUtils.ARTIST);
            mListener.onItemClick(getAdapterPosition());
        }

    }
}
