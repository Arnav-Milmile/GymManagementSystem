package com.example.gymmanagementsystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.auth.FirebaseAuth;

public class regform extends AppCompatActivity {

    private EditText editName, editPhone;
    private TextView txtSelectedDate, txtEndDate;
    private Spinner spinnerSubscription;
    private Button btnSelectDate, btnSave;
    private String joiningDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regform);

        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        txtSelectedDate = findViewById(R.id.txtSelectedDate);
        txtEndDate = findViewById(R.id.txtEndDate);
        spinnerSubscription = findViewById(R.id.spinnerSubscription);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSave = findViewById(R.id.btnSave);

        btnSelectDate.setOnClickListener(v -> openDatePicker());
        btnSave.setOnClickListener(v -> saveMember());
    }

    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    joiningDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                    txtSelectedDate.setText("Joining Date: " + joiningDate);
                    calculateEndDate(year, month, dayOfMonth);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void calculateEndDate(int year, int month, int day) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Calendar endDate = Calendar.getInstance();
            endDate.set(year, month, day);

            String subscriptionType = spinnerSubscription.getSelectedItem().toString().trim();
            int daysToAdd = 30; // Default monthly

            switch (subscriptionType) {
                case "Monthly(Strength)":
                case "Monthly(Cardio+Strength)":
                    daysToAdd = 30;
                    break;
                case "3 Month(Cardio)":
                case "3 Month(Cardio+Strength)":
                    daysToAdd = 90;
                    break;
            }

            endDate.add(Calendar.DAY_OF_MONTH, daysToAdd);
            String finalEndDate = sdf.format(endDate.getTime());
            txtEndDate.setText(finalEndDate);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "Date Parsing Error: " + e.getMessage());
        }
    }

    // NEW: Save member data to Firestore instead of SharedPreferences
    private void saveMember() {
        String name = editName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String subscriptionType = spinnerSubscription.getSelectedItem().toString();
        String endDate = txtEndDate.getText().toString();

        if (name.isEmpty() || phone.isEmpty() || joiningDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Please fill all details!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map with member details
        Map<String, Object> memberData = new HashMap<>();
        memberData.put("name", name);
        memberData.put("phone", phone);
        memberData.put("joiningDate", joiningDate);
        memberData.put("subscriptionType", subscriptionType);
        memberData.put("endDate", endDate);
        // NEW: Save the current owner's UID with the member record
        memberData.put("ownerId", FirebaseAuth.getInstance().getCurrentUser().getUid());

        FirebaseFirestore.getInstance().collection("members")
                .add(memberData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(regform.this, "Member Saved Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(regform.this, ViewMembersActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(regform.this, "Error saving member: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
