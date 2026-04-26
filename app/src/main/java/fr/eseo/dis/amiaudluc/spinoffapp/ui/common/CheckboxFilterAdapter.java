package fr.eseo.dis.amiaudluc.spinoffapp.ui.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.R;

public class CheckboxFilterAdapter extends ListAdapter<FilterItem, CheckboxFilterAdapter.FilterViewHolder> {

    public interface OnFilterChangeListener {
        void onFilterChanged(List<Integer> selectedIds);
    }

    private final OnFilterChangeListener listener;

    public CheckboxFilterAdapter(OnFilterChangeListener listener) {
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
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre_checkbox, parent, false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        FilterItem item = getItem(position);
        holder.checkBox.setText(item.name());
        
        // Unset listener before setting checked state to avoid unwanted callbacks
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(item.isChecked());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleSelection(holder.getBindingAdapterPosition());
        });
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

    public List<Integer> getSelectedIds() {
        return getCurrentList().stream()
                .filter(FilterItem::isChecked)
                .map(FilterItem::id)
                .collect(Collectors.toList());
    }

    public static class FilterViewHolder extends RecyclerView.ViewHolder {
        MaterialCheckBox checkBox;

        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = (MaterialCheckBox) itemView.findViewById(R.id.checkbox_item);
        }
    }
}
