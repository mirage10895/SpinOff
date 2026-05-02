package fr.eseo.dis.amiaudluc.spinoffapp.ui.common.filters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import fr.eseo.dis.amiaudluc.R;

public class GenreChipAdapter extends ListAdapter<FilterItem, GenreChipAdapter.GenreViewHolder> {

    private final OnFilterChangeListener listener;

    public interface OnFilterChangeListener {
        void onFilterChanged(List<Integer> selectedIds);
    }

    public GenreChipAdapter(OnFilterChangeListener listener) {
        super(new DiffUtil.ItemCallback<>() {
            @Override
            public boolean areItemsTheSame(@NonNull FilterItem oldItem, @NonNull FilterItem newItem) {
                return oldItem.id() == newItem.id();
            }

            @Override
            public boolean areContentsTheSame(@NonNull FilterItem oldItem, @NonNull FilterItem newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre_chip, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        FilterItem item = getItem(position);
        holder.chip.setText(item.name());
        holder.chip.setOnCheckedChangeListener(null);
        holder.chip.setChecked(item.isChecked());

        holder.chip.setOnCheckedChangeListener((
                buttonView,
                isChecked
        ) -> toggleSelection(holder.getBindingAdapterPosition()));
    }

    private void toggleSelection(int position) {
        if (position == RecyclerView.NO_POSITION) return;

        List<FilterItem> currentList = new ArrayList<>(getCurrentList());
        FilterItem item = currentList.get(position);
        currentList.set(position, new FilterItem(item.id(), item.name(), !item.isChecked()));

        submitList(currentList, () -> {
            if (listener != null) {
                List<Integer> selectedIds = currentList.stream()
                        .filter(FilterItem::isChecked)
                        .map(FilterItem::id)
                        .collect(Collectors.toList());
                listener.onFilterChanged(selectedIds);
            }
        });
    }

    public static class GenreViewHolder extends RecyclerView.ViewHolder {
        Chip chip;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.chip_genre);
        }
    }
}
