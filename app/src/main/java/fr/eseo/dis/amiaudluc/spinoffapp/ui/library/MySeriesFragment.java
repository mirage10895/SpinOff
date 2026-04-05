package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;

import java.util.List;
import java.util.stream.Collectors;

import androidx.lifecycle.ViewModelProvider;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.AdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SerieViewModel;

public class MySeriesFragment extends BaseLibraryFragment {

    private SerieViewModel serieViewModel;

    public MySeriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.serieViewModel = new ViewModelProvider(requireActivity()).get(SerieViewModel.class);
    }

    @Override
    protected int getTitleResId() {
        return R.string.library_series;
    }

    @Override
    protected FragmentType getFragmentType() {
        return FragmentType.SERIE;
    }

    @Override
    protected void setupViewModel() {
        this.serieViewModel.getDatabaseSeries().observe(
                getViewLifecycleOwner(),
                serieDatabases -> {
                    List<SerieDatabase> seenRaw = serieDatabases.stream()
                            .filter(SerieDatabase::isWatched)
                            .collect(Collectors.toList());

                    List<AdapterData> seen = seenRaw.stream()
                            .map(m -> new AdapterData(
                                    m.getId(),
                                    m.getName(),
                                    m.getPosterPath(),
                                    FragmentType.SERIE
                            ))
                            .collect(Collectors.toList());

                    List<AdapterData> toSee = serieDatabases.stream()
                            .filter(serieDatabase -> !serieDatabase.isWatched())
                            .map(m -> new AdapterData(
                                    m.getId(),
                                    m.getName(),
                                    m.getPosterPath(),
                                    FragmentType.SERIE
                            ))
                            .collect(Collectors.toList());

                    int seenRuntime = seenRaw.stream()
                            .mapToInt(SerieDatabase::getRuntime)
                            .sum();

                    updateUI(seen, toSee, serieDatabases.size(), seen.size(), DateUtils.displayDuration(seenRuntime));
                }
        );
    }

    @Override
    protected void updateContextMenu(ContextMenu menu) {
        List<SerieDatabase> series = this.serieViewModel.getDatabaseSeries().getValue();
        if (series != null) {
            boolean hasBeenWatched = series.stream()
                    .anyMatch(i -> i.isWatched() && i.getId().equals(this.selectedId));
            if (hasBeenWatched) {
                menu.removeItem(R.id.context_menu_mark_as);
            } else {
                menu.removeItem(R.id.context_menu_mark_as_not);
            }
        }
    }

    @Override
    protected void deleteMedia(Integer id) {
        this.serieViewModel.deleteById(id);
    }

    @Override
    protected void toggleWatched(Integer id) {
        this.serieViewModel.toggleSerieIsWatched(id);
    }

    @Override
    public void onItemClick(Integer id, FragmentType type) {
        Intent intent = new Intent(getContext(), SerieActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
