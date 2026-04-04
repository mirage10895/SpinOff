package fr.eseo.dis.amiaudluc.spinoffapp.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.AnticipateInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import fr.eseo.dis.amiaudluc.R;
import fr.eseo.dis.amiaudluc.databinding.ActivityMainBinding;
import fr.eseo.dis.amiaudluc.spinoffapp.services.UpdateSeriesWorker;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.library.LibraryFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.OnAirMoviesFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.PopularMoviesFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.movies.TopRatedMoviesFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.search.SearchContainerFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.OnAirSeriesFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.PopularSeriesFragment;
import fr.eseo.dis.amiaudluc.spinoffapp.ui.series.TopRatedSeriesFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final int PERMISSIONS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Handle the splash screen transition.
        setupSplashScreen();

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkPermissions();
        setupBottomNavigation();

        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.nav_movies);
        }

        scheduleBackgroundTasks();
    }

    private void setupSplashScreen() {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setOnExitAnimationListener(splashScreenView -> {
            final View splashView = splashScreenView.getView();

            // Create a slide-up animation
            ObjectAnimator slideUp = ObjectAnimator.ofFloat(
                    splashView,
                    View.TRANSLATION_Y,
                    0f,
                    -splashView.getHeight()
            );
            slideUp.setInterpolator(new AnticipateInterpolator());
            slideUp.setDuration(500L);

            // Call splashScreenView.remove() when the animation is done
            slideUp.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    splashScreenView.remove();
                }
            });

            slideUp.start();
        });
    }

    private void setupBottomNavigation() {
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
                switchFragment(
                        LibraryFragment.newInstance()
                );
                return true;
            }
            return false;
        });
    }

    private void checkPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Permissions are optional for the app to function, or handled elsewhere if needed.
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
                .replace(binding.navHostFragment.getId(), fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
