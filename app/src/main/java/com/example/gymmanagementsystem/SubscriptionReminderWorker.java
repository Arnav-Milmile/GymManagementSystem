package com.example.gymmanagementsystem;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SubscriptionReminderWorker extends Worker {

    private static final String TAG = "SubReminderWorker";

    public SubscriptionReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "ReminderWorker triggered");

        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] resultHolder = {true};

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("members");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot memberSnapshot : snapshot.getChildren()) {
                    String mobile = memberSnapshot.child("mobile").getValue(String.class);
                    String endDateStr = memberSnapshot.child("subscriptionEndDate").getValue(String.class);

                    if (mobile == null || endDateStr == null) continue;

                    try {
                        // Updated date format
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        Date endDate = sdf.parse(endDateStr);
                        Date today = new Date();

                        long diffMillis = endDate.getTime() - today.getTime();
                        long daysLeft = TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);

                        if (daysLeft == 1) {
                            sendSMSReminder(mobile);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing date: " + e.getMessage());
                        resultHolder[0] = false;
                    }
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase error: " + error.getMessage());
                resultHolder[0] = false;
                latch.countDown();
            }
        });

        try {
            latch.await(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, "Latch interrupted: " + e.getMessage());
            return Result.failure();
        }

        return resultHolder[0] ? Result.success() : Result.failure();
    }

    private void sendSMSReminder(String mobile) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String message = "Your gym subscription is ending tomorrow. Please renew to continue enjoying our services.";
            smsManager.sendTextMessage(mobile, null, message, null, null);
            Log.d(TAG, "SMS sent to " + mobile);
        } catch (Exception e) {
            Log.e(TAG, "Failed to send SMS: " + e.getMessage());
        }
    }
}
