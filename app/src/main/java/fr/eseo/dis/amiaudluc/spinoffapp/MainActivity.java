package fr.eseo.dis.amiaudluc.spinoffapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import fr.eseo.dis.amiaudluc.spinoffapp.calendar.CalendarActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.calendar.CalendarFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.common.CacheManager;
import fr.eseo.dis.amiaudluc.spinoffapp.content.Content;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.library.LibraryActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Serie;
import fr.eseo.dis.amiaudluc.spinoffapp.movies.OnAirMoviesFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.movies.PopularMoviesFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.movies.TopRatedMoviesFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.notifications.NotificationReceiver;
import fr.eseo.dis.amiaudluc.spinoffapp.search.SearchActivity;
import fr.eseo.dis.amiaudluc.spinoffapp.series.OnAirSeriesFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.series.PopularSeriesFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.series.TopRatedSeriesFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String ARGUMENT = "FRAGMENT";
    private String currentFragment;
    private HashMap<String, Fragment> fragments = new HashMap<>();
    private FloatingActionsMenu fam;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = AppDatabase.getAppDatabase(this);

        fragments.put(getString(R.string.fragment_popular_movies), new PopularMoviesFragment());
        fragments.put(getString(R.string.fragment_popular_series), new PopularSeriesFragment());
        fragments.put(getString(R.string.fragment_top_rated_movies), new TopRatedMoviesFragment());
        fragments.put(getString(R.string.fragment_top_rated_series), new TopRatedSeriesFragment());
        fragments.put(getString(R.string.fragment_on_air_movies), new OnAirMoviesFragment());
        fragments.put(getString(R.string.fragment_on_air_series), new OnAirSeriesFragment());
        fragments.put(getString(R.string.fragment_my_calendar), new CalendarFragment());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_movies);

        ActionBar actionBar = getSupportActionBar();

        initializeActionBarTitle(actionBar,savedInstanceState);

        fam = (FloatingActionsMenu) findViewById(R.id.fab);
        findViewById(R.id.button_pop).setVisibility(View.GONE);
        fam.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                setVisibility();
            }

            /**
             * To hide the button that links to the current page
             */
            @Override
            public void onMenuCollapsed() {
                setVisibility();
            }
        });
        initializeButtons();

        /**
         * DEBUG MODE
         */
        CacheManager.getInstance().removeAll(this);
        //AppDatabase.getAppDatabase(this).nukeDB();
    }

    private void setVisibility(){
        FloatingActionButton fabTop = (FloatingActionButton) findViewById(R.id.button_top_rated);
        FloatingActionButton fabPop = (FloatingActionButton) findViewById(R.id.button_pop);
        FloatingActionButton fabOnA = (FloatingActionButton) findViewById(R.id.button_on_air);
        switch (currentFragment.substring(0,3)){
            case "Top":
                fabTop.setVisibility(View.GONE);
                fabPop.setVisibility(View.VISIBLE);
                fabOnA.setVisibility(View.VISIBLE);
                break;
            case "Pop":
                fabTop.setVisibility(View.VISIBLE);
                fabPop.setVisibility(View.GONE);
                fabOnA.setVisibility(View.VISIBLE);
                break;
            case "On ":
                fabTop.setVisibility(View.VISIBLE);
                fabPop.setVisibility(View.VISIBLE);
                fabOnA.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * Initializes the action bar title
     * @param actionBar
     * @param savedInstanceState
     */
    private void initializeActionBarTitle(ActionBar actionBar, Bundle savedInstanceState){

        if (savedInstanceState != null) {
            currentFragment = savedInstanceState.getString(ARGUMENT, getString(R.string.fragment_popular_movies));
            getSupportFragmentManager().beginTransaction().replace(R.id.content,
                    fragments.get(currentFragment), currentFragment).commit();
            if (actionBar != null) {
                setActionBarTitle(currentFragment);
            }
        } else {
            currentFragment = getString(R.string.fragment_popular_movies);
            getSupportFragmentManager().beginTransaction().replace(R.id.content,
                    fragments.get(currentFragment), currentFragment).commit();

            if (actionBar != null) {
                setActionBarTitle(currentFragment);
            }
        }
        Content.currentFragment = currentFragment;
    }

    /**
     * sets action bar title
     * Can be accessed from the frags
     * @param title
     */
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    /**
     * Initializes the floating menu buttons
     */
    private void initializeButtons(){

        FloatingActionButton fabTop = (FloatingActionButton) findViewById(R.id.button_top_rated);
        fabTop.setIcon(R.drawable.ic_thumb_up_yellow);
        fabTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //The switch is here to redirect on the right page
                //If you coming from Movies you go to top rated movies
                //If you coming from Series you go to top rated series
                switch(currentFragment.substring(currentFragment.length() - 6,currentFragment.length())){
                    case "Movies":
                        currentFragment = getString(R.string.fragment_top_rated_movies);
                        break;
                    case "Series":
                        currentFragment = getString(R.string.fragment_top_rated_series);
                        break;
                    default:
                        Snackbar.make(view, "Action à rajouter", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                }
                switchFragment(fragments.get(currentFragment));
                fam.collapse();
                fam.collapse();
            }
        });

        FloatingActionButton fabPop = (FloatingActionButton) findViewById(R.id.button_pop);
        fabPop.setIcon(R.drawable.ic_star_yellow);
        fabPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(currentFragment.substring(currentFragment.length() - 6,currentFragment.length())){
                    case "Movies":
                        currentFragment = getString(R.string.fragment_popular_movies);
                        break;
                    case "Series":
                        currentFragment = getString(R.string.fragment_popular_series);
                        break;
                    default:
                        Snackbar.make(view, "Action à rajouter", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                }
                //resetEndlessScroll((ItemInterface)fragments.get(previousFragment));
                switchFragment(fragments.get(currentFragment));
                fam.collapse();
                fam.collapse();
            }
        });

        FloatingActionButton fabNew = (FloatingActionButton) findViewById(R.id.button_on_air);
        fabNew.setIcon(R.drawable.ic_alert_circle);
        fabNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(currentFragment.substring(currentFragment.length() - 6,currentFragment.length())){
                    case "Movies":
                        currentFragment = getString(R.string.fragment_on_air_movies);
                        break;
                    case "Series":
                        currentFragment = getString(R.string.fragment_on_air_series);
                        break;
                    default:
                        Snackbar.make(view, "Action à rajouter", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                }
                //resetEndlessScroll((ItemInterface)fragments.get(previousFragment));
                switchFragment(fragments.get(currentFragment));
                fam.collapse();
            }
        });
    }

    public void switchFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment, currentFragment).commit();
        setActionBarTitle(currentFragment);
        Content.currentFragment = currentFragment;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        FloatingActionsMenu fam = findViewById(R.id.fab);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fam.isExpanded()){
            fam.collapse();
        } else{
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
        }else if(id == R.id.action_search){
            Intent intent = new Intent(this,SearchActivity.class);
            this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /**
         * This to stay on Movie theme or on Serie theme whatever we wanna go
         */
        switch (id) {
            case R.id.nav_movies:
                if(currentFragment.substring(0,2).equals("To")){
                    currentFragment = getString(R.string.fragment_top_rated_movies);
                }else if(currentFragment.substring(0,2).equals("Po")){
                    currentFragment = getString(R.string.fragment_popular_movies);
                }else if(currentFragment.substring(0,2).equals("On")) {
                    currentFragment = getString(R.string.fragment_on_air_movies);
                }else{
                    currentFragment = getString(R.string.fragment_top_rated_series);
                }
                break;
            case R.id.nav_series:
                if(currentFragment.substring(0,2).equals("To")){
                    currentFragment = getString(R.string.fragment_top_rated_series);
                }else if(currentFragment.substring(0,2).equals("Po")){
                    currentFragment = getString(R.string.fragment_popular_series);
                }else if(currentFragment.substring(0,2).equals("On")) {
                    currentFragment = getString(R.string.fragment_on_air_series);
                }else{
                    currentFragment = getString(R.string.fragment_top_rated_series);
                }
                break;
            case R.id.nav_library:
                Intent intent = new Intent(this,LibraryActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_calendar:
                Intent intent2 = new Intent(this,CalendarActivity.class);
                startActivity(intent2);
                break;
        }

        switchFragment(fragments.get(currentFragment));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //When killing the app to remove all the caches files
    @Override
    protected void onDestroy() {
        CacheManager.getInstance().removeAll(this);
        super.onDestroy();
    }
}
