package fr.eseo.dis.amiaudluc.spinoffapp.ui.networks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ItemWatchProviderBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter.WatchProviderAdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.WatchProviderUtils;

public class WatchProviderAdapter extends ListAdapter<WatchProviderAdapterData, WatchProviderAdapter.WatchProviderViewHolder> {

    private final String baseImageUrl;

    private WatchProviderAdapter(String baseImageUrl) {
        super(WatchProviderAdapterData.DIFF_CALLBACK);
        this.baseImageUrl = baseImageUrl;
        submitList(new ArrayList<>());
    }

    public static WatchProviderAdapter newInstance(String baseImageUrl) {
        return new WatchProviderAdapter(baseImageUrl);
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
        WatchProviderAdapterData item = getItem(position);
        holder.bind(item, baseImageUrl);
    }

    public static class WatchProviderViewHolder extends RecyclerView.ViewHolder {

        private final ItemWatchProviderBinding binding;

        public WatchProviderViewHolder(@NonNull ItemWatchProviderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(WatchProviderAdapterData item, String baseImageUrl) {
            if (item.posterPath() == null) {
                binding.watchProviderContainer.setVisibility(View.GONE);
                return;
            } else {
                binding.watchProviderContainer.setVisibility(View.VISIBLE);
            }

            String link = item.posterPath().startsWith("http")
                    ? item.posterPath()
                    : baseImageUrl + item.posterPath();
            Picasso.get()
                    .load(link)
                    .fit()
                    .centerInside()
                    .error(R.drawable.ic_launcher_foreground)
                    .into(binding.watchProviderLogo);

            if (item.externalUrl() != null) {
                binding.watchProviderContainer.setClickable(true);
                binding.watchProviderContainer.setFocusable(true);
                binding.watchProviderContainer.setOnClickListener((view) -> WatchProviderUtils.openProvider(view.getContext(), item));
            }
        }
    }
}
