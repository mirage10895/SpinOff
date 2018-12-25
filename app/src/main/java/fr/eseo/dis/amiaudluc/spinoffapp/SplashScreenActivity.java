package fr.eseo.dis.amiaudluc.spinoffapp;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class SplashScreenActivity extends AppCompatActivity {

    private Context ctx;
    private boolean loaded = false;
    private final static int SPLASH_TIME_OUT = 1900;
    private final static int MIN_TRICK = 5;
    private int trick = 0;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ctx = this;

        if (Build.VERSION.SDK_INT >= 24) {
            List<String> listPermissionsNeeded = new ArrayList<>();
            if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
            }
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
            } else {
                StartAnimations();
            }
        } else {
            StartAnimations();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permission, grantResult);
        if (grantResult[0] == PackageManager.PERMISSION_GRANTED) {
            StartAnimations();
        } else {
            StartAnimations();
        }
    }

    private void StartAnimations() {
        ImageView vLogo = (ImageView) findViewById(R.id.imgLogo); // Gantier's listener

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.idLoad);

        // Special trick is back !
        vLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trick++;
            }
        });


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 1, 500);
                animation.setDuration(1500); //in milliseconds
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                animation.start();
            }
        }, 400);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();

            }
        }, SPLASH_TIME_OUT);
    }

}
