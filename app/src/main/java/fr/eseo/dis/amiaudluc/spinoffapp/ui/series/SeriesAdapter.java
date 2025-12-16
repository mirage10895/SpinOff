package fr.eseo.dis.amiaudluc.spinoffapp.ui.series;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;

/**
 * Created by lucasamiaud on 01/03/2018.
 */

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder> {

    private final SearchInterface fragment;
    private final Context ctx;
    private final boolean isHorizontal;
    private List<SerieAdapterData> series;

    public SeriesAdapter(
            Context ctx,
            SearchInterface fragment,
            List<SerieAdapterData> data,
            boolean isHorizontal
    ) {
        this.ctx = ctx;
        this.fragment = fragment;
        this.series = data;
        this.isHorizontal = isHorizontal;
    }

    public void setSeries(List<SerieAdapterData> series) {
        this.series = series;
    }

    @NonNull
    @Override
    public SeriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isHorizontal) {
            View mySerieView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_media_horizontal, parent, false);
            return new SeriesAdapter.SeriesViewHolder(mySerieView);
        }
        View mySerieView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_media, parent, false);
        return new SeriesAdapter.SeriesViewHolder(mySerieView);
    }

    @Override
    public void onBindViewHolder(@NonNull SeriesViewHolder holder, int position) {
        if (getItemCount() != 0) {
            SerieAdapterData serie = series.get(position);

            holder.seriePoster.setImageResource(R.drawable.ic_launcher_foreground);
            if (serie.getPosterPath() != null) {
                String link = ctx.getResources().getString(R.string.base_url_poster_500) + serie.getPosterPath();
                Picasso.get().load(link).fit().error(R.drawable.ic_launcher_foreground)
                        .into(holder.seriePoster);
            }
        }
    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    class SeriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        private final ImageView seriePoster;

        SeriesViewHolder(View view) {
            super(view);

            seriePoster = view.findViewById(R.id.poster_ic);

            view.setOnClickListener(this);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            final SerieAdapterData serieDatabase = series.get(getAbsoluteAdapterPosition());
            fragment.setType(SearchInterface.FragmentType.SERIE);
            fragment.onItemClick(serieDatabase.getId());
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            final SerieAdapterData serieDatabase = series.get(getAbsoluteAdapterPosition());
            fragment.onCreateCtxMenu(contextMenu, view, contextMenuInfo, serieDatabase.getId());
        }
    }
}
