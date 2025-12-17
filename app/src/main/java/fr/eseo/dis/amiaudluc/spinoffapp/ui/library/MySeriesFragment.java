package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieAdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SeriesAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SerieViewModel;

/**
 * Created by lucasamiaud on 08/03/2018.
 */

public class MySeriesFragment extends Fragment implements SearchInterface {

    private SerieViewModel serieViewModel;
    private SeriesAdapter seriesAdapter;
    private Integer selectedSerieId;

    public MySeriesFragment() {
        //Empty constructor required
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.serieViewModel = new ViewModelProvider(requireActivity()).get(SerieViewModel.class);
        this.serieViewModel.initDatabaseSeries();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_my_medias, container, false);
        Context ctx = view.getContext();

        this.serieViewModel.getDatabaseSeries().observe(
                requireActivity(),
                serieDatabases -> this.setSeries(
                        serieDatabases.stream()
                                .map(ss -> SerieAdapterData.of(
                                        ss.getId(),
                                        ss.getPosterPath()
                                ))
                                .collect(Collectors.toList())
                )
        );

        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        RecyclerView recycler = view.findViewById(R.id.cardList);
        recycler.setHasFixedSize(true);
        int columns = getResources().getInteger(R.integer.scripts_columns);
        recycler.setLayoutManager(new GridLayoutManager(ctx, columns));


        seriesAdapter = new SeriesAdapter(ctx, this, new ArrayList<>(), false);
        recycler.setAdapter(seriesAdapter);

        return view;
    }

    private void setSeries(List<SerieAdapterData> series) {
        if (seriesAdapter != null) {
            seriesAdapter.setSeries(series);
            seriesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_library, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.context_menu_delete) {
            this.serieViewModel.deleteById(this.selectedSerieId);
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
        // stub
    }
}
