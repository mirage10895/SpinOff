package fr.eseo.dis.amiaudluc.spinoffapp;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.movies.MoviesAdapter;

/**
 * Created by lucasamiaud on 04/04/2018.
 */

public abstract class BaseMovieFragment extends BaseFragment {


    protected MoviesAdapter moviesAdapter;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu_add:
                if (db.moviesDAO().getAllIds().contains(Content.currentMovie.getId())){
                    Snackbar.make(getView(),"Movie is already in your library",Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }else{
                    db.moviesDAO().insertMovie(Content.currentMovie);
                    Snackbar.make(getView(),"Movie added to your library",Snackbar.LENGTH_LONG)
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
