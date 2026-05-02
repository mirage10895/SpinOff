package fr.eseo.dis.amiaudluc.spinoffapp.ui.common.filters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.strategy.RuntimeFilter;

public class RuntimeChipAdapter extends ListAdapter<RuntimeFilter, RuntimeChipAdapter.RuntimeViewHolder> {

    private final OnRuntimeFilterChangeListener listener;
    private RuntimeFilter selectedFilter;

    public interface OnRuntimeFilterChangeListener {
        void onRuntimeFilterChanged(RuntimeFilter filter);
    }

    public RuntimeChipAdapter(OnRuntimeFilterChangeListener listener, RuntimeFilter initialSelection) {
        super(new DiffUtil.ItemCallback<>() {
            @Override
            public boolean areItemsTheSame(@NonNull RuntimeFilter oldItem, @NonNull RuntimeFilter newItem) {
                return oldItem.labelResId() == newItem.labelResId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull RuntimeFilter oldItem, @NonNull RuntimeFilter newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.listener = listener;
        this.selectedFilter = initialSelection;
    }

    @NonNull
    @Override
    public RuntimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre_chip, parent, false);
        return new RuntimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RuntimeViewHolder holder, int position) {
        RuntimeFilter filter = getItem(position);
        holder.bind(filter, filter.equals(selectedFilter));
    }

    public void clearSelection() {
        this.selectedFilter = null;
        notifyDataSetChanged();
    }

    class RuntimeViewHolder extends RecyclerView.ViewHolder {
        private final Chip chip;

        public RuntimeViewHolder(@NonNull View itemView) {
            super(itemView);
            this.chip = (Chip) itemView;
        }

        public void bind(RuntimeFilter filter, boolean isSelected) {
            chip.setText(filter.labelResId());
            chip.setChecked(isSelected);
            chip.setOnClickListener(v -> {
                if (filter.equals(selectedFilter)) {
                    selectedFilter = null;
                } else {
                    selectedFilter = filter;
                }
                listener.onRuntimeFilterChanged(selectedFilter);
                notifyDataSetChanged();
            });
        }
    }
}
