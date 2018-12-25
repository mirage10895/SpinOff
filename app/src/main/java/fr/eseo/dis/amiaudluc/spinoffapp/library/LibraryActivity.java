package fr.eseo.dis.amiaudluc.spinoffapp.library;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import fr.eseo.dis.amiaudluc.spinoffapp.R;
import fr.eseo.dis.amiaudluc.spinoffapp.Utils.ConstUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.Utils.DateUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.Utils.LogUtils;
import fr.eseo.dis.amiaudluc.spinoffapp.database.DAO.DBInitializer.AppDatabase;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Episode;
import fr.eseo.dis.amiaudluc.spinoffapp.model.Season;
import fr.eseo.dis.amiaudluc.spinoffapp.notifications.NotificationReceiver;

public class LibraryActivity extends AppCompatActivity {

    private AppDatabase db;
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
        db = AppDatabase.getAppDatabase(this);

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

        GetDataFromDB mDataTask = new GetDataFromDB();
        mDataTask.execute();
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

    ///////////////////
    // ALARM MANAGER //
    ///////////////////

    private AlarmManager alarmManager;

    public void setAlarm(Calendar calendar, Episode episode) {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            Intent myIntent = new Intent(this, NotificationReceiver.class);
            myIntent.putExtra(ConstUtils.BUNDLE_KEY_EPISODE, episode.getId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, episode.getId(), myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),12,0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private class GetDataFromDB extends AsyncTask<Void, Void, List<Season>> {

        @Override
        protected List<Season> doInBackground(Void... params) {
            return db.seasonDAO().getAll();
        }

        @Override
        protected void onPostExecute(List<Season> seasons) {
            for (Season season : seasons) {
                if (season.getFutureEpisode().getAirDate() != null) {
                    Calendar cal = Calendar.getInstance(Locale.US);
                    cal.setTime(season.getFutureEpisode().getAirDate());
                    setAlarm(cal, season.getFutureEpisode());
                }
            }
        }
    }

}
