package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DBInitializer.DatabaseTransactionManager;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SeriesAdapter;

/**
 * Created by lucasamiaud on 08/03/2018.
 */

public class MySeriesFragment extends Fragment implements SearchInterface {

    private SeriesAdapter seriesAdapter;
    private List<SerieDatabase> series;
    private AppDatabase db;
    private FragmentType type;
    private Integer selectedSerieId;

    public MySeriesFragment(){
        //Empty constructor required
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_medias, container, false);
        Context ctx = view.getContext();
        db = AppDatabase.getAppDatabase(ctx);

        db.serieDAO().getAll().observe(this, this::setDbSeries);

        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        RecyclerView recycler = view.findViewById(R.id.cardList);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(ctx));

        seriesAdapter = new SeriesAdapter(ctx,this, new ArrayList<>());
        recycler.setAdapter(seriesAdapter);

        return view;
    }

    private void setDbSeries(List<SerieDatabase> series){
        this.series = series;
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
        if (item.getItemId() == R.id.context_menu_delete) {
            DatabaseTransactionManager.executeAsync(() -> db.serieDAO().deleteSerieById(this.selectedSerieId));
            db.serieDAO().getAll().observe(this, this::setDbSeries);
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(Integer id) {
        Intent intent = new Intent(getContext(), SerieActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onCreateCtxMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo, Integer serieId) {
        this.selectedSerieId = serieId;
        onCreateContextMenu(contextMenu, v, menuInfo);
    }

    @Override
    public void setType(FragmentType type) {
        this.type = type;
    }
}
