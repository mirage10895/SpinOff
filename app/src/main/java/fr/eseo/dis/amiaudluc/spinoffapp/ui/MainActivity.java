package fr.eseo.dis.amiaudluc.spinoffapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ActivityMainBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.services.UpdateSeriesWorker;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.calendar.CalendarFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.library.LibraryActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.OnAirMoviesFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.PopularMoviesFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.TopRatedMoviesFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.search.SearchActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.OnAirSeriesFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.PopularSeriesFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.TopRatedSeriesFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String STATE_CURRENT_TAG = "current_fragment_tag";
    private ActivityMainBinding binding;
    private final Map<String, Class<? extends Fragment>> fragmentClasses = new HashMap<>();
    private FamManager famMenu;
    private String currentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        setupFragmentClasses();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.appBarMain.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);

        famMenu = new FamManager();
        binding.fab.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                famMenu.updateVisibility();
            }

            @Override
            public void onMenuCollapsed() {
                famMenu.updateVisibility();
            }
        });

        if (savedInstanceState == null) {
            currentTag = getString(R.string.fragment_popular_movies);
            binding.navView.setCheckedItem(R.id.nav_movies);
            switchFragment(currentTag);
        } else {
            currentTag = savedInstanceState.getString(STATE_CURRENT_TAG);
            setActionBarTitle(currentTag);
        }

        scheduleBackgroundTasks();
    }

    private void setupFragmentClasses() {
        fragmentClasses.put(getString(R.string.fragment_popular_movies), PopularMoviesFragment.class);
        fragmentClasses.put(getString(R.string.fragment_popular_series), PopularSeriesFragment.class);
        fragmentClasses.put(getString(R.string.fragment_top_rated_movies), TopRatedMoviesFragment.class);
        fragmentClasses.put(getString(R.string.fragment_top_rated_series), TopRatedSeriesFragment.class);
        fragmentClasses.put(getString(R.string.fragment_on_air_movies), OnAirMoviesFragment.class);
        fragmentClasses.put(getString(R.string.fragment_on_air_series), OnAirSeriesFragment.class);
        fragmentClasses.put(getString(R.string.fragment_my_calendar), CalendarFragment.class);
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

    public void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    public void switchFragment(String tag) {
        this.currentTag = tag;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Class<? extends Fragment> fragmentClass = fragmentClasses.get(tag);
        
        if (fragmentClass != null) {
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.content, fragmentClass, null, tag)
                    .commit();
            setActionBarTitle(tag);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_CURRENT_TAG, currentTag);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            } else if (binding.fab.isExpanded()) {
                binding.fab.collapse();
            } else {
                getOnBackPressedDispatcher().onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_movies) {
            this.famMenu.setFamTheme("Movies");
            this.famMenu.updateFragment();
        } else if (id == R.id.nav_series) {
            this.famMenu.setFamTheme("Series");
            this.famMenu.updateFragment();
        } else if (id == R.id.nav_library) {
            startActivity(new Intent(this, LibraryActivity.class));
        } else if (id == R.id.nav_calendar) {
            switchFragment(getString(R.string.fragment_my_calendar));
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private class FamManager {
        private String famTheme = "Movies";
        private FamType famType = FamType.POPULAR;

        FamManager() {
            binding.buttonTopRated.setIcon(R.drawable.ic_thumb_up_yellow);
            binding.buttonTopRated.setOnClickListener(view -> {
                this.famType = FamType.TOP_RATED;
                this.updateFragment();
                binding.fab.collapse();
            });

            binding.buttonPop.setIcon(R.drawable.ic_star_yellow);
            binding.buttonPop.setOnClickListener(view -> {
                this.famType = FamType.POPULAR;
                this.updateFragment();
                binding.fab.collapse();
            });

            binding.buttonOnAir.setIcon(R.drawable.ic_alert_circle);
            binding.buttonOnAir.setOnClickListener(view -> {
                this.famType = FamType.ON_AIR;
                this.updateFragment();
                binding.fab.collapse();
            });
        }

        void setFamTheme(String famTheme) {
            this.famTheme = famTheme;
        }

        void updateVisibility() {
            binding.buttonOnAir.setVisibility(this.famType == FamType.ON_AIR ? View.GONE : View.VISIBLE);
            binding.buttonTopRated.setVisibility(this.famType == FamType.TOP_RATED ? View.GONE : View.VISIBLE);
            binding.buttonPop.setVisibility(this.famType == FamType.POPULAR ? View.GONE : View.VISIBLE);
        }

        void updateFragment() {
            String fragmentTag;
            if ("Movies".equals(this.famTheme)) {
                fragmentTag = getString(this.famType.movieFragment);
            } else {
                fragmentTag = getString(this.famType.serieFragment);
            }
            switchFragment(fragmentTag);
            updateVisibility();
        }
    }

    private enum FamType {
        POPULAR("Popular", R.string.fragment_popular_movies, R.string.fragment_popular_series),
        TOP_RATED("Top Rated", R.string.fragment_top_rated_movies, R.string.fragment_top_rated_series),
        ON_AIR("On Air", R.string.fragment_on_air_movies, R.string.fragment_on_air_series);

        final String name;
        final int movieFragment;
        final int serieFragment;

        FamType(String name, int movieFragment, int serieFragment) {
            this.name = name;
            this.movieFragment = movieFragment;
            this.serieFragment = serieFragment;
        }
    }
}
