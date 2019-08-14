package fr.eseo.dis.amiaudluc.spinoffapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import fr.eseo.dis.amiaudluc.spinoffapp.action.DeleteMovieActionListener;
import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.DatabaseTransactionManager;
import fr.eseo.dis.amiaudluc.spinoffapp.movies.MovieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.movies.MoviesAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.repository.ApiRepository;
import fr.eseo.dis.amiaudluc.spinoffapp.view_model.MovieViewModel;

/**
 * Created by lucasamiaud on 04/04/2018.
 */

public abstract class BaseMovieFragment extends BaseFragment {

    public MovieViewModel movieViewModel;
    protected MoviesAdapter moviesAdapter;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.layout_main, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.view = view;
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.context_menu_add) {
            //TODO refacto
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
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer position) {
        onCreateContextMenu(contextMenu,v,menuInfo);
    }
}
