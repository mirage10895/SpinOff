package fr.eseo.dis.amiaudluc.spinoffapp.ui;

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
import fr.eseo.dis.amiaudluc.spinoffapp.api.beans.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.action.DeleteSerieActionListener;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieAdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SeriesAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SerieViewModel;

/**
 * Created by lucasamiaud on 28/12/2018.
 */

public abstract class BaseSerieFragment extends BaseFragment {
    protected SeriesAdapter seriesAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.serieViewModel = new ViewModelProvider(requireActivity()).get(SerieViewModel.class);
        this.seriesAdapter = new SeriesAdapter(requireContext(), this, new ArrayList<>(), false);
        binding.cardList.setAdapter(this.seriesAdapter);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.context_menu_add) {
            super.serieViewModel.insert(super.selectedContextId);
            Snackbar.make(binding.getRoot(), R.string.serie_added, Snackbar.LENGTH_LONG)
                    .setAction(
                            R.string.undo_action,
                            new DeleteSerieActionListener(this.serieViewModel, super.selectedContextId)
                    )
                    .show();
            return true;
        } else if (item.getItemId() == R.id.context_menu_delete) {
            super.serieViewModel.deleteById(super.selectedContextId);
            Snackbar.make(binding.getRoot(), R.string.serie_deleted, Snackbar.LENGTH_LONG)
                    .show();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    protected void observeSeries() {
        this.serieViewModel.getSeries().observe(getViewLifecycleOwner(), series -> {
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
                        .setAction("Refresh", view -> onRecyclerLoadMore(0)).show();
            }
        });
    }

    private void loadSeries(List<SerieAdapterData> serieDatabaseList){
        this.seriesAdapter.setSeries(serieDatabaseList);
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
            super.serieViewModel.getDatabaseSeries().observe(getViewLifecycleOwner(), series -> {
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
