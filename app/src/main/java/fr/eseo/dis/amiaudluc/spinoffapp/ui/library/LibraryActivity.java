package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import fr.eseo.dis.amiaudluc.spinoffapp.R;

public class LibraryActivity extends AppCompatActivity {

    private Fragment fragment;
    private String currentFragment;

    private BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_lib_movie:
                    currentFragment = "My Movies";
                    fragment = new MyMoviesFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment, currentFragment).commit();
                    getSupportActionBar().setTitle(currentFragment);
                    return true;
                case R.id.navigation_dashboard:
                    currentFragment = "Dashboard";
                    fragment = new DashboardFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment, currentFragment).commit();
                    getSupportActionBar().setTitle(currentFragment);
                    return true;
                case R.id.navigation_lib_serie:
                    currentFragment = "My Series";
                    fragment = new MySeriesFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment, currentFragment).commit();
                    getSupportActionBar().setTitle(currentFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.fab).setVisibility(View.GONE);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_dashboard);

        currentFragment = "Dashboard";
        fragment = new DashboardFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment, currentFragment).commit();
        getSupportActionBar().setTitle(currentFragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.menu.options_menu:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
