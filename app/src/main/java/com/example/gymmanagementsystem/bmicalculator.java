package com.example.gymmanagementsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class bmicalculator extends AppCompatActivity {
    private EditText editWeight, editHeight;
    private Button calcButton;
    private TextView bmiText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmicalculator);

        // Initializing views
        editWeight = findViewById(R.id.editWeight);
        editHeight = findViewById(R.id.editheight);
        calcButton = findViewById(R.id.calc);
        bmiText = findViewById(R.id.bmitext);

        calcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBMI();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void calculateBMI() {
        String weightStr = editWeight.getText().toString();
        String heightStr = editHeight.getText().toString();

        // Validate input
        if (weightStr.isEmpty() || heightStr.isEmpty()) {
            bmiText.setText("Please enter both weight and height!");
            return;
        }

        try {
            double weight = Double.parseDouble(weightStr);
            double height = Double.parseDouble(heightStr);

            if (height <= 0) {
                bmiText.setText("Height must be greater than 0!");
                return;
            }

            // BMI formula
            double bmi = weight / (height * height);
            String category = getBMICategory(bmi);
            bmiText.setText(String.format("BMI: %.2f\n%s", bmi, category));

        } catch (NumberFormatException e) {
            bmiText.setText("Invalid input! Please enter numbers only.");
        }
    }

    private String getBMICategory(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 24.9) {
            return "Normal Weight";
        } else if (bmi < 29.9) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }
}
