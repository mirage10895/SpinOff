package fr.eseo.dis.amiaudluc.spinoffapp.ui.series;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;

/**
 * Created by lucasamiaud on 01/03/2018.
 */
public class SeriesAdapter extends ListAdapter<SerieAdapterData, SeriesAdapter.SeriesViewHolder> {

    private final ItemInterface fragment;
    private final Context ctx;
    private final boolean isHorizontal;

    private static final DiffUtil.ItemCallback<SerieAdapterData> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull SerieAdapterData oldItem, @NonNull SerieAdapterData newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull SerieAdapterData oldItem, @NonNull SerieAdapterData newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public SeriesAdapter(
            Context ctx,
            ItemInterface fragment,
            List<SerieAdapterData> initialData,
            boolean isHorizontal
    ) {
        super(DIFF_CALLBACK);
        this.ctx = ctx;
        this.fragment = fragment;
        this.isHorizontal = isHorizontal;
        if (initialData != null) {
            submitList(new ArrayList<>(initialData));
        }
    }

    public void setSeries(List<SerieAdapterData> series) {
        submitList(series != null ? new ArrayList<>(series) : null);
    }

    @NonNull
    @Override
    public SeriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isHorizontal ? R.layout.item_media_horizontal : R.layout.item_media;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new SeriesViewHolder(view, fragment, this);
    }

    @Override
    public void onBindViewHolder(@NonNull SeriesViewHolder holder, int position) {
        SerieAdapterData serie = getItem(position);

        holder.seriePoster.setImageResource(R.drawable.ic_launcher_foreground);
        if (serie.getPosterPath() != null) {
            String link = ctx.getResources().getString(R.string.base_url_poster_500) + serie.getPosterPath();
            Picasso.get().load(link).fit().error(R.drawable.ic_launcher_foreground)
                    .into(holder.seriePoster);
        }

        holder.fragment.onRegisterContextMenu(holder.itemView, serie.getId());
    }

    public static class SeriesViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final ImageView seriePoster;
        private final ItemInterface fragment;
        private final SeriesAdapter adapter;

        SeriesViewHolder(View view, ItemInterface fragment, SeriesAdapter adapter) {
            super(view);
            this.seriePoster = view.findViewById(R.id.poster_ic);
            this.fragment = fragment;
            this.adapter = adapter;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAbsoluteAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                SerieAdapterData serie = adapter.getItem(pos);
                fragment.onItemClick(serie.getId(), FragmentType.SERIE);
            }
        }
    }
}
