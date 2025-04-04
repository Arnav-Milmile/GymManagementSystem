package com.example.gymmanagementsystem;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
            // 🔹 Date ka format set karo
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            // 🔹 Joining date ko convert karo Date object me
            Calendar endDate = Calendar.getInstance();
            endDate.set(year, month, day);

            // 🔹 Subscription ke hisaab se days add karo
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

            // 🔹 Direct date me days add karo
            endDate.add(Calendar.DAY_OF_MONTH, daysToAdd);

            // 🔹 Final end date nikal ke textview me set karo
            String finalEndDate = sdf.format(endDate.getTime());
            txtEndDate.setText(finalEndDate);


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "Date Parsing Error: " + e.getMessage());
        }
    }


    private void saveMember() {
        String name = editName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String subscriptionType = spinnerSubscription.getSelectedItem().toString();
        String endDate = txtEndDate.getText().toString();

        if (name.isEmpty() || phone.isEmpty() || joiningDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Please fill all details!", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("GymMembers", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 🔹 Member ko ek key (name) ke under store karo
        String memberData = phone + "," + joiningDate + "," + subscriptionType + "," + endDate;
        editor.putString(name, memberData);
        editor.apply();

        Toast.makeText(this, "Member Saved Successfully!", Toast.LENGTH_SHORT).show();

        // 🔄 List update karne ke liye ViewMembersActivity open karo
        Intent intent = new Intent(regform.this, ViewMembersActivity.class);
        startActivity(intent);
        finish();
    }

}

