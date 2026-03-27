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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.SearchInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieAdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SeriesAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SerieViewModel;

/**
 * Created by lucasamiaud on 08/03/2018.
 */

public class MySeriesFragment extends Fragment implements SearchInterface {

    private SerieViewModel serieViewModel;
    private SeriesAdapter toSeeSeriesAdapter;
    private SeriesAdapter seenSeriesAdapter;
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

        View toSeeContainer = view.findViewById(R.id.media_to_see_container);
        View seenContainer = view.findViewById(R.id.media_seen_container);
        TextView title = view.findViewById(R.id.title);
        TextView mediaNumber = view.findViewById(R.id.media_number);
        TextView seenNumber = view.findViewById(R.id.media_seen_number);
        TextView runtime = view.findViewById(R.id.media_runtime);

        title.setText(R.string.library_series);

        RecyclerView recyclerToSee = view.findViewById(R.id.media_to_see);
        recyclerToSee.setHasFixedSize(true);
        recyclerToSee.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));

        this.toSeeSeriesAdapter = new SeriesAdapter(
                ctx,
                this,
                new ArrayList<>(),
                true
        );
        recyclerToSee.setAdapter(toSeeSeriesAdapter);

        RecyclerView recyclerSeen = view.findViewById(R.id.media_seen);
        recyclerSeen.setHasFixedSize(true);
        recyclerSeen.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));

        this.seenSeriesAdapter = new SeriesAdapter(
                ctx,
                this,
                new ArrayList<>(),
                true
        );
        recyclerSeen.setAdapter(seenSeriesAdapter);

        this.serieViewModel.getDatabaseSeries().observe(
                requireActivity(),
                serieDatabases -> {
                    List<SerieDatabase> seen = serieDatabases.stream()
                            .filter(SerieDatabase::isWatched)
                            .collect(Collectors.toList());

                    List<SerieAdapterData> toSee = serieDatabases.stream()
                            .filter(serieDatabase -> !serieDatabase.isWatched())
                            .map(m -> SerieAdapterData.of(
                                    m.getId(),
                                    m.getPosterPath()
                            ))
                            .collect(Collectors.toList());

                    mediaNumber.setText(String.valueOf(serieDatabases.size()));
                    seenNumber.setText(String.valueOf(seen.size()));
                    int seenRuntime = seen.stream()
                            .mapToInt(SerieDatabase::getRuntime)
                            .sum();
                    runtime.setText(DateUtils.displayDuration(seenRuntime));

                    seenContainer.setVisibility(seen.isEmpty() ? View.GONE : View.VISIBLE);
                    toSeeContainer.setVisibility(toSee.isEmpty() ? View.GONE : View.VISIBLE);

                    this.setSeries(
                            seen
                                    .stream()
                                    .map(m -> SerieAdapterData.of(
                                            m.getId(),
                                            m.getPosterPath()
                                    ))
                                    .collect(Collectors.toList()),
                            toSee
                    );
                }
        );

        return view;
    }

    private void setSeries(
            List<SerieAdapterData> seenSeries,
            List<SerieAdapterData> toSeeSeries
    ) {
        toSeeSeriesAdapter.setSeries(toSeeSeries);
        seenSeriesAdapter.setSeries(seenSeries);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_library, menu);

        boolean hasBeenWatched = this.serieViewModel.getDatabaseSeries().getValue()
                .stream()
                .anyMatch(i -> i.isWatched() && i.getId().equals(this.selectedSerieId));
        if (hasBeenWatched) {
            menu.removeItem(R.id.context_menu_mark_as);
        } else {
            menu.removeItem(R.id.context_menu_mark_as_not);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.context_menu_delete) {
            this.serieViewModel.deleteById(this.selectedSerieId);
            return true;
        }
        if (
                item.getItemId() == R.id.context_menu_mark_as
                        || item.getItemId() == R.id.context_menu_mark_as_not
        ) {
            this.serieViewModel.toggleSerieIsWatched(this.selectedSerieId);
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
    public void onCreateCtxMenu(
            ContextMenu contextMenu,
            View v,
            ContextMenu.ContextMenuInfo menuInfo,
            Integer serieId
    ) {
        this.selectedSerieId = serieId;
        onCreateContextMenu(contextMenu, v, menuInfo);
    }

    @Override
    public void setType(FragmentType type) {
        // stub
    }
}
