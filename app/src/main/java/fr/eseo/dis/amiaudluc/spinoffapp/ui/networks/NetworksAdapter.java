package fr.eseo.dis.amiaudluc.spinoffapp.ui.networks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Network;

/**
 * Created by lucasamiaud on 07/03/2018.
 */

public class NetworksAdapter extends RecyclerView.Adapter<NetworksAdapter.NetworksViewHolder>{


    private List<Network> networks;
    private final Context ctx;

    public NetworksAdapter(Context ctx, List<Network> data){
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
                .inflate(R.layout.item_watch_provider, parent, false);
        return new NetworksViewHolder(seasonView);
    }

    @Override
    public void onBindViewHolder(@NonNull NetworksViewHolder holder, int position) {
        if (getItemCount() != 0) {
            String link = ctx.getResources().getString(R.string.base_url_poster_500) + this.networks.get(position).getLogoPath();
            Picasso.get()
                    .load(link)
                    .fit()
                    .centerInside()
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return this.networks.size();
    }

    public class NetworksViewHolder extends RecyclerView.ViewHolder {

        final ImageView avatar;

        NetworksViewHolder(View view) {
            super(view);

            avatar = view.findViewById(R.id.watch_provider_logo);
        }
    }
}
