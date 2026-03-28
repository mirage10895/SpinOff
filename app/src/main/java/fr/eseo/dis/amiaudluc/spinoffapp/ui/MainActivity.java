package fr.eseo.dis.amiaudluc.spinoffapp.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ActivityMainBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.services.UpdateSeriesWorker;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.OnAirMoviesFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.PopularMoviesFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.TopRatedMoviesFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.search.SearchContainerFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.OnAirSeriesFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.PopularSeriesFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.TopRatedSeriesFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_movies) {
                switchFragment(
                        HomeFragment.newInstance(
                                new PopularMoviesFragment(),
                                new TopRatedMoviesFragment(),
                                new OnAirMoviesFragment()
                        )
                );
                return true;
            }
            if (id == R.id.nav_series) {
                switchFragment(
                        HomeFragment.newInstance(
                                new PopularSeriesFragment(),
                                new TopRatedSeriesFragment(),
                                new OnAirSeriesFragment()
                        )
                );
                return true;
            }
            if (id == R.id.nav_discover) {
                switchFragment(
                        SearchContainerFragment.newInstance()
                );
                return true;
            }
            if (id == R.id.nav_library) {
                // Placeholder: you might want to create a separate LibraryFragment later
                switchFragment(
                        SearchContainerFragment.newInstance()
                );
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.nav_movies);
        }

        scheduleBackgroundTasks();
    }

    private void scheduleBackgroundTasks() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest updateSeriesRequest =
                new PeriodicWorkRequest.Builder(UpdateSeriesWorker.class, 1, TimeUnit.DAYS)
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "UpdateSeriesWork",
                ExistingPeriodicWorkPolicy.KEEP,
                updateSeriesRequest
        );
    }

    public void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            binding.bottomNavigation.setSelectedItemId(R.id.nav_discover);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
