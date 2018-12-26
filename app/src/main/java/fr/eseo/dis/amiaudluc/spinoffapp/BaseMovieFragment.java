package fr.eseo.dis.amiaudluc.spinoffapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.DatabaseTransactionManager;
import fr.eseo.dis.amiaudluc.spinoffapp.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.movies.MoviesAdapter;

/**
 * Created by lucasamiaud on 04/04/2018.
 */

public abstract class BaseMovieFragment extends BaseFragment {

    protected MoviesAdapter moviesAdapter;
    private View view;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.view = view;
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu_add:
                if (DatabaseTransactionManager.getAllMovieIds(db).contains(Content.currentMovie.getId())){
                    Snackbar.make(this.view, "This movie is already in your library", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    DatabaseTransactionManager.addMovie(db, Content.currentMovie);
                    Snackbar.make(this.view,"Movie added to your library",Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onItemClick(int position) {
        Content.currentMovie = Content.movies.get(position);

        Intent intent = new Intent(getContext(), MovieActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, int position) {
        Content.currentMovie = Content.movies.get(position);
        onCreateContextMenu(contextMenu,v,menuInfo);
    }
}
