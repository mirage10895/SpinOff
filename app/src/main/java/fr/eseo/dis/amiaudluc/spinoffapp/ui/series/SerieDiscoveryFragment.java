package fr.eseo.dis.amiaudluc.spinoffapp.ui.series;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.spinoffapp.api.tmdb.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.DiscoveryBaseFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.action.DeleteSerieActionListener;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.AdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.MediaAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.serie.SerieViewModel;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.DiscoveryViewModel;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.discovery.SerieDiscoveryViewModel;

/**
 * Created by lucasamiaud on 28/12/2018.
 */
public class SerieDiscoveryFragment extends DiscoveryBaseFragment {
    private DiscoveryViewModel discoveryViewModel;
    private SerieViewModel serieViewModel;
    private SerieDiscoveryViewModel serieDiscoveryViewModel;
    protected MediaAdapter seriesAdapter;

    public static SerieDiscoveryFragment newInstance() {
        return new SerieDiscoveryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.discoveryViewModel = new ViewModelProvider(requireActivity()).get(DiscoveryViewModel.class);
        this.serieViewModel = new ViewModelProvider(requireActivity()).get(SerieViewModel.class);
        this.serieDiscoveryViewModel = new ViewModelProvider(requireActivity()).get(SerieDiscoveryViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.seriesAdapter = new MediaAdapter(requireContext(), this, new ArrayList<>(), false);
        binding.cardList.setAdapter(this.seriesAdapter);

        this.serieDiscoveryViewModel.bindFilters(this.discoveryViewModel.getFilter());
        this.discoveryViewModel.getFilter().observe(getViewLifecycleOwner(), type -> resetScroll());

        this.observeSeries();
    }

    @Override
    protected void initializeSwipeContainer() {
        super.initializeSwipeContainer();
        binding.swipeContainer.setOnRefreshListener(() -> this.serieDiscoveryViewModel.resetSearch());
    }

    @Override
    public void onRecyclerLoadMore(int page) {
        this.serieDiscoveryViewModel.loadPage(page + 1);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.context_menu_add) {
            this.serieViewModel.insert(super.selectedContextId);
            Snackbar.make(binding.getRoot(), R.string.serie_added, Snackbar.LENGTH_LONG)
                    .setAction(
                            R.string.undo_action,
                            new DeleteSerieActionListener(this.serieViewModel, super.selectedContextId)
                    )
                    .show();
            return true;
        } else if (item.getItemId() == R.id.context_menu_delete) {
            this.serieViewModel.deleteById(super.selectedContextId);
            Snackbar.make(binding.getRoot(), R.string.serie_deleted, Snackbar.LENGTH_LONG)
                    .show();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    protected void observeSeries() {
        this.serieDiscoveryViewModel.getResults().observe(getViewLifecycleOwner(), series -> {
            binding.swipeContainer.setRefreshing(false);
            binding.progressBar.setVisibility(View.GONE);
            binding.cardList.setVisibility(View.VISIBLE);
            binding.noMediaDisplay.getRoot().setVisibility(View.GONE);
            if (series != null) {
                loadSeries(series.stream()
                        .map(Serie::toAdapterFormat)
                        .collect(Collectors.toList()));
            } else {
                binding.cardList.setVisibility(View.GONE);
                binding.noMediaDisplay.getRoot().setVisibility(View.VISIBLE);
                Snackbar.make(binding.getRoot(), R.string.no_results, Snackbar.LENGTH_LONG)
                        .setAction("Refresh", v -> onRecyclerLoadMore(0)).show();
            }
        });
    }

    private void loadSeries(List<AdapterData> serieDatabaseList){
        this.seriesAdapter.setMedias(serieDatabaseList);
    }

    @Override
    public void onItemClick(Integer id, FragmentType type) {
        Intent intent = new Intent(requireContext(), SerieActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (super.selectedContextId != null) {
            this.serieViewModel.getDatabaseSeries().observe(getViewLifecycleOwner(), series -> {
                boolean isPresent = series.stream().anyMatch(i -> i.getId().equals(super.selectedContextId));
                if (isPresent) {
                    menu.removeItem(R.id.context_menu_add);
                } else {
                    menu.removeItem(R.id.context_menu_delete);
                }
            });
        }
    }
}
