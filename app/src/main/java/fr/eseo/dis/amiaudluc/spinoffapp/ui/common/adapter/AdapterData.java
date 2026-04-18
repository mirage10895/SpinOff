package fr.eseo.dis.amiaudluc.spinoffapp.ui.common.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;

public record AdapterData(
        int id,
        String banner,
        String posterPath,
        FragmentType fragmentType
) {

    public static final DiffUtil.ItemCallback<AdapterData> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull AdapterData oldItem, @NonNull AdapterData newItem) {
                    return oldItem.id() == newItem.id();
                }

                @Override
                public boolean areContentsTheSame(@NonNull AdapterData oldItem, @NonNull AdapterData newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
