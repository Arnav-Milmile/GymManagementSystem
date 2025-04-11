package com.example.gymmanagementsystem;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Button btnManageMembers, btnFees, btnBMI, btnLogout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Enqueue one-time subscription reminder
        WorkManager.getInstance(this).enqueue(
                new OneTimeWorkRequest.Builder(SubscriptionReminderWorker.class).build()
        );

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Check login status
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Button initialization
        btnManageMembers = findViewById(R.id.manmem);

        btnBMI = findViewById(R.id.bmi);
        btnLogout = findViewById(R.id.btnLogout);

        // Click listeners
        btnManageMembers.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ManageMembers.class)));

        btnFees.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ViewFeesActivity.class)));

        btnBMI.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, bmicalculator.class)));

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        // Insets padding for full-screen experience
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Request SMS permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 100);
        }

        // Set up daily background worker
        PeriodicWorkRequest dailyReminder =
                new PeriodicWorkRequest.Builder(SubscriptionReminderWorker.class, 1, TimeUnit.DAYS)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "SubscriptionReminderWork",
                ExistingPeriodicWorkPolicy.KEEP,
                dailyReminder
        );
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            String msg = (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    ? "SMS permission granted"
                    : "SMS permission denied";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
