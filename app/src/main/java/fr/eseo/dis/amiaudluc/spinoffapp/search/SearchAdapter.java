package fr.eseo.dis.amiaudluc.spinoffapp.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Media;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Movie;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Content} and makes a call to the
 *
 */
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Media> mValues;
    private final ItemInterface mListener;
    private List<Movie> mMovies = new ArrayList<>();
    private List<Serie> mSerie = new ArrayList<>();
    private Context ctx;

    public SearchAdapter(List<Media> items, ItemInterface listener) {
        this.mValues = items;
        this.mListener = listener;
        setMedias(items);
    }

    public void setMedias(List<Media> medias){
        this.mValues = medias;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_search_main, parent, false);

        ctx = parent.getContext();

        switch (viewType){
            case 0:
                return new ViewHolder(view);
            case 1:
                return new ViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ViewHolder viewHolder0 = (ViewHolder)holder;
                break;

            case 1:
                ViewHolder viewHolder2 = (ViewHolder)holder;
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final View mView;
        public final ImageView mImageView;
        public Media mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.poster_ic);

            view.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        public void onClick(View view) {
            mListener.onItemClick(getAdapterPosition());
        }
    }
}
