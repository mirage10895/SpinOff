package fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

public record WatchProviderAdapterData(
        int id,
        String name,
        String posterPath,
        String externalUrl,
        @Nullable String fallbackPackageName
) {
    public static final DiffUtil.ItemCallback<WatchProviderAdapterData> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull WatchProviderAdapterData oldItem, @NonNull WatchProviderAdapterData newItem) {
                    return oldItem.id() == newItem.id();
                }

                @Override
                public boolean areContentsTheSame(@NonNull WatchProviderAdapterData oldItem, @NonNull WatchProviderAdapterData newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
