package fr.eseo.dis.amiaudluc.spinoffapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.HashMap;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.common.CacheManager;
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

    private static final String ARGUMENT = "FRAGMENT";
    private HashMap<String, Fragment> fragments = new HashMap<>();
    private FloatingActionsMenu fam;
    private FamManager famMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragments.put(getString(R.string.fragment_popular_movies), new PopularMoviesFragment());
        fragments.put(getString(R.string.fragment_popular_series), new PopularSeriesFragment());
        fragments.put(getString(R.string.fragment_top_rated_movies), new TopRatedMoviesFragment());
        fragments.put(getString(R.string.fragment_top_rated_series), new TopRatedSeriesFragment());
        fragments.put(getString(R.string.fragment_on_air_movies), new OnAirMoviesFragment());
        fragments.put(getString(R.string.fragment_on_air_series), new OnAirSeriesFragment());
        fragments.put(getString(R.string.fragment_my_calendar), new CalendarFragment());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_movies);

        ActionBar actionBar = getSupportActionBar();

        initializeActionBarTitle(actionBar, savedInstanceState);

        famMenu = new FamManager();
        famMenu.updateVisibility();

        fam = findViewById(R.id.fab);
        fam.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                famMenu.updateVisibility();
            }

            /**
             * To hide the button that links to the current page
             */
            @Override
            public void onMenuCollapsed() {
                famMenu.updateVisibility();
            }
        });

        /*
         * DEBUG MODE
         */
        //CacheManager.getInstance().removeAll(this);
        //AppDatabase.getAppDatabase(this).nukeDB();
    }

    /**
     * Initializes the action bar title
     *
     * @param actionBar
     * @param savedInstanceState
     */
    private void initializeActionBarTitle(ActionBar actionBar, Bundle savedInstanceState) {
        String currentFragment;
        if (savedInstanceState != null) {
            currentFragment = savedInstanceState.getString(ARGUMENT, getString(R.string.fragment_popular_movies));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content,
                    fragments.get(currentFragment), currentFragment).commit();
            if (actionBar != null) {
                setActionBarTitle(currentFragment);
            }
        } else {
            currentFragment = getString(R.string.fragment_popular_movies);
            getSupportFragmentManager()
                    .beginTransaction().replace(R.id.content,
                    fragments.get(currentFragment), currentFragment).commit();

            if (actionBar != null) {
                setActionBarTitle(currentFragment);
            }
        }
    }

    /**
     * sets action bar title
     * Can be accessed from the frags
     *
     * @param title
     */
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void switchFragment(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragments.get(tag), tag).commit();
        setActionBarTitle(tag);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        FloatingActionsMenu fam = findViewById(R.id.fab);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fam.isExpanded()) {
            fam.collapse();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.filter_king_menu) {
            findViewById(R.id.filterKing).setVisibility(View.VISIBLE);
            return true;
        } else if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*
         * This to stay on Movie theme or on Serie theme wherever we wanna go
         */
        switch (id) {
            case R.id.nav_movies:
                this.famMenu.setFamTheme("Movies");
                this.famMenu.updateFragment();
                break;
            case R.id.nav_series:
                this.famMenu.setFamTheme("Series");
                this.famMenu.updateFragment();
                break;
            case R.id.nav_library:
                Intent intent = new Intent(this, LibraryActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_calendar:
                switchFragment(getString(R.string.fragment_my_calendar));
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class FamManager {
        private final FloatingActionButton fabTop;
        private final FloatingActionButton fabPop;
        private final FloatingActionButton fabOnA;
        private String famTheme;
        private FamType famType;


        FamManager() {
            fabTop = findViewById(R.id.button_top_rated);
            fabPop = findViewById(R.id.button_pop);
            fabOnA = findViewById(R.id.button_on_air);
            famTheme = "Movies";
            famType = FamType.POPULAR;

            fabTop.setIcon(R.drawable.ic_thumb_up_yellow);
            fabTop.setOnClickListener(view -> {
                this.setFamType(FamType.TOP_RATED);
                this.updateFragment();
                fam.collapse();
            });

            fabPop.setIcon(R.drawable.ic_star_yellow);
            fabPop.setOnClickListener(view -> {
                this.setFamType(FamType.POPULAR);
                this.updateFragment();
                fam.collapse();
            });

            fabOnA.setIcon(R.drawable.ic_alert_circle);
            fabOnA.setOnClickListener(view -> {
                this.setFamType(FamType.ON_AIR);
                this.updateFragment();
                fam.collapse();
            });
        }

        void setFamTheme(String famTheme) {
            this.famTheme = famTheme;
        }

        void setFamType(FamType famType) {
            this.famType = famType;
        }

        void updateVisibility() {
            this.fabOnA.setVisibility(this.famType == FamType.ON_AIR ? View.GONE : View.VISIBLE);
            this.fabTop.setVisibility(this.famType == FamType.TOP_RATED ? View.GONE : View.VISIBLE);
            this.fabPop.setVisibility(this.famType == FamType.POPULAR ? View.GONE : View.VISIBLE);
        }

        void updateFragment() {
            String fragment;
            if ("Movies".equals(this.famTheme)) {
                fragment = getString(this.famType.getMovieFragment());
            } else {
                fragment = getString(this.famType.getSerieFragment());
            }
            switchFragment(fragment);
            updateVisibility();
        }
    }

    private enum FamType {
        POPULAR("Popular", R.string.fragment_popular_movies, R.string.fragment_popular_series),
        TOP_RATED("Top Rated", R.string.fragment_top_rated_movies, R.string.fragment_top_rated_series),
        ON_AIR("On Air", R.string.fragment_on_air_movies, R.string.fragment_on_air_series);

        String name;
        int movieFragment;
        int serieFragment;

        FamType(String name, int movieFragment, int serieFragment) {
            this.name = name;
            this.movieFragment = movieFragment;
            this.serieFragment = serieFragment;
        }

        public int getMovieFragment() {
            return this.movieFragment;
        }

        public int getSerieFragment() {
            return serieFragment;
        }
    }

    //When killing the app to remove all the caches files
    @Override
    protected void onDestroy() {
        CacheManager.getInstance().removeAll(this);
        super.onDestroy();
    }
}
