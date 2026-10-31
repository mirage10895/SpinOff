package fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

/**
 * Row of the discovery poster grids: the media to display plus whether it is already stored in
 * the local library.
 * <p>
 * Encoding the membership in the item lets {@link androidx.recyclerview.widget.ListAdapter}
 * compute the minimal update through {@link DiffUtil} instead of notifying rows manually.
 */
public record DiscoverItem(AdapterData media, boolean inLibrary) {

    public static final DiffUtil.ItemCallback<DiscoverItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull DiscoverItem oldItem, @NonNull DiscoverItem newItem) {
                    return AdapterData.DIFF_CALLBACK.areItemsTheSame(oldItem.media(), newItem.media());
                }

                @Override
                public boolean areContentsTheSame(@NonNull DiscoverItem oldItem, @NonNull DiscoverItem newItem) {
                    return oldItem.inLibrary() == newItem.inLibrary()
                            && AdapterData.DIFF_CALLBACK.areContentsTheSame(oldItem.media(), newItem.media());
                }
            };
}
