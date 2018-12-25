package fr.eseo.dis.amiaudluc.spinoffapp.calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import fr.eseo.dis.amiaudluc.spinoffapp.R;

public class CalendarActivity extends AppCompatActivity {

    private String currentFragment;
    private CalendarFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.fab).setVisibility(View.GONE);

        currentFragment = getString(R.string.fragment_my_calendar);
        fragment = new CalendarFragment();
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
