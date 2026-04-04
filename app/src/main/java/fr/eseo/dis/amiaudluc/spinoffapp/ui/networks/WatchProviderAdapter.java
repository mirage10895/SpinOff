package fr.eseo.dis.amiaudluc.spinoffapp.ui.networks;

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
import fr.eseo.dis.amiaudluc.databinding.ItemWatchProviderBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.AdapterData;

public class WatchProviderAdapter extends ListAdapter<AdapterData, WatchProviderAdapter.WatchProviderViewHolder> {

    private final String baseImageUrl;

    private WatchProviderAdapter(String baseImageUrl) {
        super(AdapterData.DIFF_CALLBACK);
        this.baseImageUrl = baseImageUrl;
        submitList(new ArrayList<>());
    }

    public static WatchProviderAdapter newInstance(String baseImageUrl) {
        return new WatchProviderAdapter(baseImageUrl);
    }

    public void setData(List<AdapterData> data) {
        submitList(data != null ? new ArrayList<>(data) : null);
    }

    @NonNull
    @Override
    public WatchProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWatchProviderBinding binding = ItemWatchProviderBinding.inflate(
                LayoutInflater.from(parent.getContext()), 
                parent, 
                false
        );
        return new WatchProviderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchProviderViewHolder holder, int position) {
        AdapterData item = getItem(position);
        holder.bind(item, baseImageUrl);
    }

    public static class WatchProviderViewHolder extends RecyclerView.ViewHolder {

        private final ItemWatchProviderBinding binding;

        public WatchProviderViewHolder(@NonNull ItemWatchProviderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AdapterData item, String baseImageUrl) {
            if (item.posterPath() == null) {
                binding.watchProviderContainer.setVisibility(View.GONE);
                return;
            } else {
                binding.watchProviderContainer.setVisibility(View.VISIBLE);
            }
            
            String link = baseImageUrl + item.posterPath();
            Picasso.get()
                    .load(link)
                    .fit()
                    .centerInside()
                    .error(R.drawable.ic_launcher_foreground)
                    .into(binding.watchProviderLogo);
        }
    }
}
