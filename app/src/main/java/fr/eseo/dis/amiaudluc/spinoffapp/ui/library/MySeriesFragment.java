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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.FragmentMyMediasBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.database.dao.model.SerieDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.AdapterData;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.FragmentType;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.ItemInterface;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.common.MediaAdapter;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.SerieActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.viewmodel.SerieViewModel;

/**
 * Created by lucasamiaud on 08/03/2018.
 */
public class MySeriesFragment extends Fragment implements ItemInterface {

    private FragmentMyMediasBinding binding;
    private SerieViewModel serieViewModel;
    private MediaAdapter toSeeSeriesAdapter;
    private MediaAdapter seenSeriesAdapter;
    private Integer selectedSerieId;

    public MySeriesFragment() {
        // Empty constructor required
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.serieViewModel = new ViewModelProvider(requireActivity()).get(SerieViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentMyMediasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = view.getContext();

        binding.title.setText(R.string.library_series);

        // Setup "To See" RecyclerView
        binding.mediaToSee.setHasFixedSize(true);
        binding.mediaToSee.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        this.toSeeSeriesAdapter = new MediaAdapter(context, this, new ArrayList<>(), true);
        binding.mediaToSee.setAdapter(toSeeSeriesAdapter);

        // Setup "Seen" RecyclerView
        binding.mediaSeen.setHasFixedSize(true);
        binding.mediaSeen.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        this.seenSeriesAdapter = new MediaAdapter(context, this, new ArrayList<>(), true);
        binding.mediaSeen.setAdapter(seenSeriesAdapter);

        this.serieViewModel.getDatabaseSeries().observe(getViewLifecycleOwner(), serieDatabases -> {
            List<SerieDatabase> seen = serieDatabases.stream()
                    .filter(SerieDatabase::isWatched)
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

            binding.mediaNumber.setText(String.valueOf(serieDatabases.size()));
            binding.mediaSeenNumber.setText(String.valueOf(seen.size()));
            
            int seenRuntime = seen.stream().mapToInt(SerieDatabase::getRuntime).sum();
            binding.mediaRuntime.setText(DateUtils.displayDuration(seenRuntime));

            binding.mediaSeenLayer.setVisibility(seen.isEmpty() ? View.GONE : View.VISIBLE);
            binding.mediaToSeeLayer.setVisibility(toSee.isEmpty() ? View.GONE : View.VISIBLE);

            this.setSeries(
                    seen.stream()
                            .map(m -> new AdapterData(
                                    m.getId(),
                                    m.getName(),
                                    m.getPosterPath(),
                                    FragmentType.SERIE
                            ))
                            .collect(Collectors.toList()),
                    toSee
            );
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setSeries(
            List<AdapterData> seenSeries,
            List<AdapterData> toSeeSeries
    ) {
        toSeeSeriesAdapter.setMedias(toSeeSeries);
        seenSeriesAdapter.setMedias(seenSeries);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = requireActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_library, menu);

        if (v.getTag() instanceof Integer) {
            this.selectedSerieId = (Integer) v.getTag();
        }

        List<SerieDatabase> series = this.serieViewModel.getDatabaseSeries().getValue();
        if (series != null) {
            boolean hasBeenWatched = series.stream()
                    .anyMatch(i -> i.isWatched() && i.getId().equals(this.selectedSerieId));
            if (hasBeenWatched) {
                menu.removeItem(R.id.context_menu_mark_as);
            } else {
                menu.removeItem(R.id.context_menu_mark_as_not);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.context_menu_delete) {
            this.serieViewModel.deleteById(this.selectedSerieId);
            return true;
        }
        if (item.getItemId() == R.id.context_menu_mark_as || item.getItemId() == R.id.context_menu_mark_as_not) {
            this.serieViewModel.toggleSerieIsWatched(this.selectedSerieId);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onItemClick(Integer id, FragmentType fragmentType) {
        Intent intent = new Intent(getContext(), SerieActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onRegisterContextMenu(View view, Integer id) {
        view.setTag(id);
        registerForContextMenu(view);
    }
}
