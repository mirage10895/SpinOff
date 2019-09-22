package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.DatabaseTransactionManager;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.MovieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.MoviesAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class MyMoviesFragment extends Fragment implements SearchInterface {

    private MoviesAdapter moviesAdapter;
    private Context ctx;
    private List<MovieDatabase> movies;
    private AppDatabase db;
    private FragmentType type;
    private Integer selectedMovieId;


    public MyMoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_medias, container, false);
        ctx = view.getContext();
        db = AppDatabase.getAppDatabase(ctx);

        db.moviesDAO().getAll().observe(this, this::setDbMovies);

        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        RecyclerView recycler = view.findViewById(R.id.cardList);
        recycler.setHasFixedSize(true);
        int columns = getResources().getInteger(R.integer.scripts_columns);
        recycler.setLayoutManager(new GridLayoutManager(ctx, columns));

        this.moviesAdapter = new MoviesAdapter(ctx,this, new ArrayList<>());
        recycler.setAdapter(moviesAdapter);

        return view;
    }

    private void setDbMovies(List<MovieDatabase> movies){
        this.movies = movies;
        if (moviesAdapter != null) {
            moviesAdapter.setMovies(this.movies);
            moviesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_library, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.context_menu_delete) {
            DatabaseTransactionManager.executeAsync(() -> db.moviesDAO().deleteMovieById(this.selectedMovieId));
            db.moviesDAO().getAll().observe(this, this::setDbMovies);
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(Integer id) {
        Intent intent = new Intent(getContext(), MovieActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer movieId) {
        this.selectedMovieId = movieId;
        onCreateContextMenu(contextMenu,v,menuInfo);
    }

    @Override
    public void setType(FragmentType type) {
        this.type = type;
    }
}
