package com.aiva.aivacrm.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.aiva.aivacrm.R;

import network.UserSessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION_MS = 1500; // 1.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use the fade animation layout
        setContentView(R.layout.activity_splash);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
        alphaAnimation.setDuration(800);
        alphaAnimation.setRepeatCount(100);
        alphaAnimation.setInterpolator(new DecelerateInterpolator());
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        findViewById(R.id.logo).startAnimation(alphaAnimation);

        // Optionally, you can also start the progress indicator here if not using the default ProgressBar
        // For this example, the ProgressBar in the layout is sufficient

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Decide where to navigate based on user session
                if (UserSessionManager.isUserLoggedIn(SplashActivity.this)) {
                    // User is logged in, navigate to DailyTasks
                    Intent mainIntent = new Intent(SplashActivity.this, DailyTasks.class);
                    startActivity(mainIntent);
                } else {
                    // User is not logged in, navigate to LoginActivity
                    Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
                finish(); // Close SplashActivity
            }
        }, SPLASH_DURATION_MS);
    }

    /**
     * Starts a fade animation on the provided ImageView.
     *
     * @param imageView The ImageView to animate.
     */
    private void startFadeAnimation(ImageView imageView) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
        alphaAnimation.setDuration(800); // Duration of the animation in milliseconds
        alphaAnimation.setInterpolator(new DecelerateInterpolator());
        alphaAnimation.setFillAfter(true); // Retain the final state after animation
        imageView.startAnimation(alphaAnimation);
    }
}
