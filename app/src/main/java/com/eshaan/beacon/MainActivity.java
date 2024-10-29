package com.eshaan.beacon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.d("MainActivity", "User is not signed in");
            redirectTo(LoginActivity.class);
        }

        TextView usernameTextView = findViewById(R.id.UsernameText);

        assert currentUser != null;
        usernameTextView.setText(currentUser.getDisplayName());

        initializeButtons();
    }

    private void redirectTo(Class<?> activity){
        Intent intent = new Intent(MainActivity.this, activity);
        startActivity(intent);
    }

    private void initializeButtons() {
        ImageButton settingsButton = findViewById(R.id.Settings_Button);
        settingsButton.setOnClickListener(v -> redirectTo(SettingsActivity.class));

        ImageButton signOutButton = findViewById(R.id.SignOut_Button);
        signOutButton.setOnClickListener(v -> {
            mAuth.signOut();
            redirectTo(LoginActivity.class);
        });

        Button videosButton = findViewById(R.id.Videos_Button);
        videosButton.setOnClickListener(v -> redirectTo(VideosActivity.class));
    }
}