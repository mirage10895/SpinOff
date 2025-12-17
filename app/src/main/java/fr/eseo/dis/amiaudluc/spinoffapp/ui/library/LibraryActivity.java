package fr.eseo.dis.amiaudluc.spinoffapp.ui.library;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import fr.eseo.dis.amiaudluc.R;

public class LibraryActivity extends AppCompatActivity {

    private Fragment fragment;
    private String currentFragment;

    private BottomNavigationView navigation;

    private final NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_lib_movie) {
                currentFragment = "My Movies";
                fragment = new MyMoviesFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content, fragment, currentFragment)
                        .commit();
                getSupportActionBar().setTitle(currentFragment);
                return true;
            } else if (itemId == R.id.navigation_dashboard) {
                currentFragment = "Dashboard";
                fragment = new DashboardFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, fragment, currentFragment)
                        .commit();
                getSupportActionBar().setTitle(currentFragment);
                return true;
            } else if (itemId == R.id.navigation_lib_serie) {
                currentFragment = "My Series";
                fragment = new MySeriesFragment();
                getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content, fragment, currentFragment)
                        .commit();
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.fab).setVisibility(View.GONE);

        navigation = findViewById(R.id.navigation);
        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_dashboard);

        currentFragment = "Dashboard";
        fragment = new DashboardFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment, currentFragment).commit();
        getSupportActionBar().setTitle(currentFragment);

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        } else if (itemId == R.menu.options_menu) {
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
}
