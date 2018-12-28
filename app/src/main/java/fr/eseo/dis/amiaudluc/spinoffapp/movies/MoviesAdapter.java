package fr.eseo.dis.amiaudluc.spinoffapp.movies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Genre;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;

/**
 * Created by lucasamiaud on 28/02/2018.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> implements Filterable{

    private final String TAG = MoviesAdapter.class.getSimpleName();

    private SearchInterface fragment;
    private ArrayList<Movie> movies;
    private Context ctx;

    public MoviesAdapter(Context ctx, SearchInterface fragment, ArrayList<Movie> data) {
        this.ctx = ctx;
        this.fragment = fragment;
        setMovies(data);
    }

    public void setMovies(ArrayList<Movie> movies){this.movies = movies;}

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myMovieView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new MoviesViewHolder(myMovieView);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
        if (getItemCount() != 0) {
            Movie movie = movies.get(position);

            holder.moviePoster.setImageResource(R.drawable.ic_loading);
            if(movie.getPosterPath() != null){
                String link = ctx.getResources().getString(R.string.base_url_poster_500) + movie.getPosterPath();
                Picasso.with(ctx).load(link).fit().error(R.drawable.ic_cam_iris)
                        .into(holder.moviePoster);
            }
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                ArrayList<Movie> tempList;
                if (constraint != null && constraint.length() > 0) {
                    tempList = movies.stream()
                            .filter(movie -> movie.getGenres()
                                    .stream()
                                    .anyMatch(genre -> String.valueOf(genre.getId()).equals(constraint.toString())))
                            .collect(Collectors.toCollection(ArrayList::new));
                    filterResults.count = tempList.size();
                    filterResults.values = tempList;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults.count > 0) {
                    setMovies((ArrayList<Movie>)filterResults.values);
                    notifyDataSetChanged();
                }
            }
        };
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {

        private final View view;

        private final ImageView moviePoster;

        MoviesViewHolder(View view) {
            super(view);
            this.view = view;

            moviePoster = view.findViewById(R.id.poster_ic);

            view.setOnClickListener(this);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            fragment.setType(Media.MOVIE);
            fragment.onItemClick(getAdapterPosition());
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            fragment.onCreateCtxMenu(contextMenu,view,contextMenuInfo, getAdapterPosition());
        }
    }
}
