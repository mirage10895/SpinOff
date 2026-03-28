package fr.eseo.dis.amiaudluc.spinoffapp.ui.movies;

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
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.SearchInterface;

/**
 * Modernized MoviesAdapter using ListAdapter and DiffUtil.
 */
public class MoviesAdapter extends ListAdapter<MovieAdapterData, MoviesAdapter.MoviesViewHolder> {

    private final SearchInterface fragment;
    private final Context ctx;
    private final boolean isHorizontal;

    private static final DiffUtil.ItemCallback<MovieAdapterData> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull MovieAdapterData oldItem, @NonNull MovieAdapterData newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull MovieAdapterData oldItem, @NonNull MovieAdapterData newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public MoviesAdapter(
            Context ctx,
            SearchInterface fragment,
            List<MovieAdapterData> initialData,
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

    public void setMovies(List<MovieAdapterData> movies) {
        submitList(movies != null ? new ArrayList<>(movies) : null);
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isHorizontal ? R.layout.item_media_horizontal : R.layout.item_media;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new MoviesViewHolder(view, fragment, this);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
        MovieAdapterData movie = getItem(position);

        holder.moviePoster.setImageResource(R.drawable.ic_launcher_foreground);
        if (movie.getPosterPath() != null) {
            String link = ctx.getResources().getString(R.string.base_url_poster_500) + movie.getPosterPath();
            Picasso.get()
                    .load(link)
                    .fit()
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.moviePoster);
        }
        
        holder.fragment.onRegisterContextMenu(holder.itemView, movie.getId());
    }

    public static class MoviesViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final ImageView moviePoster;
        private final SearchInterface fragment;
        private final MoviesAdapter adapter;

        MoviesViewHolder(View view, SearchInterface fragment, MoviesAdapter adapter) {
            super(view);
            this.moviePoster = view.findViewById(R.id.poster_ic);
            this.fragment = fragment;
            this.adapter = adapter;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAbsoluteAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                MovieAdapterData movie = adapter.getItem(pos);
                fragment.setType(SearchInterface.FragmentType.MOVIE);
                fragment.onItemClick(movie.getId());
            }
        }
    }
}
