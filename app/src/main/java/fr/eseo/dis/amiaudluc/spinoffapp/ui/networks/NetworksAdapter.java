package fr.eseo.dis.amiaudluc.spinoffapp.ui.networks;

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
import fr.eseo.dis.amiaudluc.spinoffapp.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Network;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.ConstUtils;

/**
 * Created by lucasamiaud on 07/03/2018.
 */

public class NetworksAdapter extends RecyclerView.Adapter<NetworksAdapter.NetworksViewHolder>{


    private List<Network> networks;
    private final ItemInterface mListener;
    private final Context ctx;

    public NetworksAdapter(Context ctx, ItemInterface listener, List<Network> data){
        this.mListener = listener;
        this.ctx = ctx;
        this.setNetworks(data);
    }

    private void setNetworks(List<Network> networks){
        this.networks = networks;
    }

    @NonNull
    @Override
    public NetworksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View seasonView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_realisator, parent, false);
        return new NetworksViewHolder(seasonView);
    }

    @Override
    public void onBindViewHolder(@NonNull NetworksViewHolder holder, int position) {
        if (getItemCount() != 0) {
            holder.name.setText(ctx.getResources().getString(R.string.emptyField));
            if (this.networks.get(position).getName() != null) {
                holder.name.setText(this.networks.get(position).getName());
            }

            String link = ctx.getResources().getString(R.string.base_url_poster_500) + this.networks.get(position).getLogoPath();
            Picasso.with(ctx)
                    .load(link)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return this.networks.size();
    }

    class NetworksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private SearchInterface frag = (SearchInterface) mListener;

        final ImageView avatar;
        final TextView name;

        NetworksViewHolder(View view) {
            super(view);

            avatar = view.findViewById(R.id.avatar);
            name = view.findViewById(R.id.name);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            frag.setType(SearchInterface.FragmentType.NETWORK);
            mListener.onItemClick(getAdapterPosition());
        }

    }
}
