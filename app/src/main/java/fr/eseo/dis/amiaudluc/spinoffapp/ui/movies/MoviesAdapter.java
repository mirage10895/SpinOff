package fr.eseo.dis.amiaudluc.spinoffapp.ui.movies;

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
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private final String TAG = MoviesAdapter.class.getSimpleName();

    private SearchInterface fragment;
    private List<MovieDatabase> movies;
    private Context ctx;

    public MoviesAdapter(Context ctx, SearchInterface fragment, List<MovieDatabase> data) {
        this.ctx = ctx;
        this.fragment = fragment;
        setMovies(data);
    }

    public void setMovies(List<MovieDatabase> movies){this.movies = movies;}

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myMovieView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_media, parent, false);
        return new MoviesViewHolder(myMovieView);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
        if (getItemCount() != 0) {
            MovieDatabase movie = movies.get(position);

            holder.moviePoster.setImageResource(R.drawable.ic_launcher_foreground);
            if(movie.getPosterPath() != null){
                String link = ctx.getResources().getString(R.string.base_url_poster_500) + movie.getPosterPath();
                Picasso.with(ctx)
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

    class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {
        private final ImageView moviePoster;

        MoviesViewHolder(View view) {
            super(view);

            this.moviePoster = view.findViewById(R.id.poster_ic);

            view.setOnClickListener(this);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            final MovieDatabase movieDatabase = movies.get(getAdapterPosition());
            fragment.setType(SearchInterface.FragmentType.MOVIE);
            fragment.onItemClick(movieDatabase.getId());
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            final MovieDatabase movieDatabase = movies.get(getAdapterPosition());
            fragment.onCreateCtxMenu(contextMenu, view, contextMenuInfo, movieDatabase.getId());
        }
    }
}
