package fr.eseo.dis.amiaudluc.spinoffapp.library;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.DatabaseTransactionManager;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.series.SeriesAdapter;

/**
 * Created by lucasamiaud on 08/03/2018.
 */

public class MySeriesFragment extends Fragment implements SearchInterface {

    private SeriesAdapter seriesAdapter;
    private Context ctx;
    private ArrayList<Serie> series;
    private AppDatabase db;

    public MySeriesFragment(){
        //NOthing
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_medias, container, false);
        ctx = view.getContext();
        db = AppDatabase.getAppDatabase(ctx);

        setDbSeries(DatabaseTransactionManager.getAllSeries(db));

        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        RecyclerView recycler = view.findViewById(R.id.cardList);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(ctx));

        seriesAdapter = new SeriesAdapter(ctx,this, series);
        recycler.setAdapter(seriesAdapter);

        return view;
    }

    private void setDbSeries(List<Serie> series){
        this.series = new ArrayList<>(series);
        if (seriesAdapter != null) {
            seriesAdapter.setSeries(this.series);
            seriesAdapter.notifyDataSetChanged();
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
        switch (item.getItemId()) {
            case R.id.context_menu_delete:
                DatabaseTransactionManager.deleteSerie(db, Content.currentSerie);
                setDbSeries(this.series.stream()
                        .filter(serie -> serie.getId() != Content.currentSerie.getId())
                        .collect(Collectors.toList()));
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onItemClick(int position) {
        Content.currentSerie = this.series.get(position);
        Intent intent = new Intent(getContext(),SerieActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, int position) {
        Content.currentSerie = series.get(position);
        onCreateContextMenu(contextMenu,v,menuInfo);
    }

    @Override
    public void setType(String type) {

    }
}
