package com.safaorhan.reunion.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.safaorhan.reunion.FirestoreHelper;
import com.safaorhan.reunion.R;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    SplashActivity.this.startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();

            }
        }, SPLASH_DISPLAY_LENGTH);
        //configureFirestore();
    }

    private void configureFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            navigateToLoginDelayed(1000);
        } else {
            navigateToConversationsDelayed(1000);
        }
    }

    private void navigateToLoginDelayed(int delayMillis) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }, delayMillis);
    }

    private void navigateToConversationsDelayed(int delayMillis) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, ConversationsActivity.class);
                startActivity(intent);
            }
        }, delayMillis);
    }
}
