package fr.eseo.dis.amiaudluc.spinoffapp.ui.movies;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;

/**
 * Created by lucasamiaud on 28/02/2018.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private final SearchInterface fragment;
    private final Context ctx;
    private final boolean isHorizontal;
    private List<MovieAdapterData> movies;

    public MoviesAdapter(
            Context ctx,
            SearchInterface fragment,
            List<MovieAdapterData> movies,
            boolean isHorizontal
    ) {
        this.ctx = ctx;
        this.fragment = fragment;
        this.movies = movies;
        this.isHorizontal = isHorizontal;
    }

    public void setMovies(List<MovieAdapterData> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (!isHorizontal) {
            View myMovieView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_media, parent, false);
            return new MoviesViewHolder(myMovieView);
        }
        View myMovieView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_media_horizontal, parent, false);
        return new MoviesViewHolder(myMovieView);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
        if (getItemCount() != 0) {
            MovieAdapterData movie = movies.get(position);

            holder.moviePoster.setImageResource(R.drawable.ic_launcher_foreground);
            if (movie.getPosterPath() != null) {
                String link = ctx.getResources().getString(R.string.base_url_poster_500) + movie.getPosterPath();
                Picasso.get()
                        .load(link)
                        .fit()
                        .error(R.drawable.ic_launcher_foreground)
                        .into(holder.moviePoster);
            }
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MoviesViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnCreateContextMenuListener
    {
        private final ImageView moviePoster;

        MoviesViewHolder(View view) {
            super(view);

            this.moviePoster = view.findViewById(R.id.poster_ic);

            view.setOnClickListener(this);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            final MovieAdapterData movieDatabase = movies.get(getAbsoluteAdapterPosition());
            fragment.setType(SearchInterface.FragmentType.MOVIE);
            fragment.onItemClick(movieDatabase.getId());
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            final MovieAdapterData movieDatabase = movies.get(getAbsoluteAdapterPosition());
            fragment.onCreateCtxMenu(contextMenu, view, contextMenuInfo, movieDatabase.getId());
        }
    }
}
