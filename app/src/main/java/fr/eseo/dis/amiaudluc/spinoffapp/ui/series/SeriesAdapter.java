package fr.eseo.dis.amiaudluc.spinoffapp.ui.series;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;

/**
 * Created by lucasamiaud on 01/03/2018.
 */

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder> {

    private SearchInterface fragment;
    private List<SerieDatabase> series;
    private Context ctx;

    public SeriesAdapter(Context ctx, SearchInterface fragment, List<SerieDatabase> data) {
        this.ctx = ctx;
        this.fragment = fragment;
        setSeries(data);
    }

    public void setSeries(List<SerieDatabase> series) {
        this.series = series;
    }

    @NonNull
    @Override
    public SeriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mySerieView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_media, parent, false);
        return new SeriesAdapter.SeriesViewHolder(mySerieView);
    }

    @Override
    public void onBindViewHolder(@NonNull SeriesViewHolder holder, int position) {
        if (getItemCount() != 0) {
            SerieDatabase serie = series.get(position);

            holder.seriePoster.setImageResource(R.drawable.ic_launcher_foreground);
            if (serie.getPosterPath() != null) {
                String link = ctx.getResources().getString(R.string.base_url_poster_500) + serie.getPosterPath();
                Picasso.with(ctx).load(link).fit().error(R.drawable.ic_launcher_foreground)
                        .into(holder.seriePoster);
            }
        }
    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    class SeriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        private final View view;

        private final ImageView seriePoster;

        SeriesViewHolder(View view) {
            super(view);
            this.view = view;

            seriePoster = view.findViewById(R.id.poster_ic);

            view.setOnClickListener(this);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            final SerieDatabase serieDatabase = series.get(getAdapterPosition());
            fragment.setType(Media.SERIE);
            fragment.onItemClick(serieDatabase.getId());
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            final SerieDatabase serieDatabase = series.get(getAdapterPosition());
            fragment.onCreateCtxMenu(contextMenu, view, contextMenuInfo, serieDatabase.getId());
        }
    }
}