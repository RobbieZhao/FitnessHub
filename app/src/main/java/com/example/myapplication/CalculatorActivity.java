package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.HashMap;

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtFoot, mEtInch, mEtWeight;
    private Button mButtonCalculator;
    private TextView mTvBMR;
    private HashMap<String, String> ProfileData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        mEtFoot = findViewById(R.id.editTextHeightFoot);
        mEtInch = findViewById(R.id.editTextHeightInch);
        mEtWeight = findViewById(R.id.EditTextWeight);

        mButtonCalculator = findViewById(R.id.GetMyPlanButton);

        mTvBMR = findViewById(R.id.BMROutput);

        mButtonCalculator.setOnClickListener(this);

        displayData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.GetMyPlanButton: {
                generatePlan();
            }
        }
    }

    private void displayData() {
        ProfileData = Helper.readData(getFilesDir().getAbsolutePath(), Helper.data_file);

        if (ProfileData.containsKey("foot"))
            mEtFoot.setText(ProfileData.get("foot"));
        if (ProfileData.containsKey("inch"))
            mEtInch.setText(ProfileData.get("inch"));
        if (ProfileData.containsKey("weight"))
            mEtWeight.setText(ProfileData.get("weight"));
    }

    private void generatePlan() {
        try {
            showBMR();
            showCaloriesIntake();
            showWarnings();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBMR() throws Exception {
        int foot = Helper.getIntegerInput(mEtFoot);
        int inch = Helper.getIntegerInput(mEtInch);

        String[] messages = Helper.checkHeightInput(foot, inch, true);
        if (!messages[0].isEmpty() && !messages[1].isEmpty()) {
            Toast.makeText(this, messages[0], Toast.LENGTH_SHORT).show();
            throw new Exception(messages[1]);
        }

        int totalInches = Helper.calculateTotalInches(foot, inch);

        if (!ProfileData.containsKey("age")) {
            Toast.makeText(this, "Fill out age in the profile!", Toast.LENGTH_SHORT).show();
            throw new Exception("Unknown age");
        }

        // Age is already sanitized in the Profile page
        int age = Integer.parseInt(ProfileData.get("age"));

        if (!ProfileData.containsKey("sex")) {
            Toast.makeText(this, "Fill out sex in the profile!", Toast.LENGTH_SHORT).show();
            throw new Exception("Unknown sex");
        }

        double weight = Helper.getDoubleInput(mEtWeight);
        if (weight == -1) {
            Toast.makeText(this, "Please enter weight!", Toast.LENGTH_SHORT).show();
            throw new Exception("Empty weight");
        } else if (weight == 0) {
            Toast.makeText(this, "Weight can't be 0!", Toast.LENGTH_SHORT).show();
            throw new Exception("Invalid weight");
        }

        double BMR;
        String sex = ProfileData.get("sex");
        if (sex.equals("male")) {
            BMR = Helper.calculateBMR(weight, totalInches, age, true);
        } else {
            BMR = Helper.calculateBMR(weight, totalInches, age, false);
        }

        mTvBMR.setText("Your BMR: " + String.format("%.2f", BMR));
    }

    private void showCaloriesIntake() {

    }

    private void showWarnings() {

    }
}