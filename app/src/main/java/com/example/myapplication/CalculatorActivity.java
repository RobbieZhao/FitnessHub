package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtFoot, mEtInch, mEtWeight;
    private Button mButtonCalculator;
    private TextView mTvBMI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        mEtFoot = findViewById(R.id.editTextHeightFoot);
        mEtInch = findViewById(R.id.editTextHeightInch);
        mEtWeight = findViewById(R.id.EditTextWeight);

        mButtonCalculator = findViewById(R.id.GetMyPlanButton);

        mTvBMI = findViewById(R.id.BMIOutput);

        mButtonCalculator.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.GetMyPlanButton: {
                generatePlan();
            }
        }
    }

    private void generatePlan() {
        showBMI();
    }

    private void showBMI() {

        int foot = getInteger(mEtFoot);
        int inch = getInteger(mEtInch);
        int totalInches = getTotalInches(foot, inch);
        float weight = getFloat(mEtWeight);

        float BMI = calculateBMI(weight, totalInches);

        mTvBMI.setText("Your BMI: " + BMI);
    }

    private float calculateBMI(float weight, int inch) {
        return 703 * weight / inch / inch;
    }


    private int getInteger(EditText editText) {
        String str = editText.getText().toString().trim();

        return Integer.parseInt(str);
    }

    private float getFloat(EditText editText) {

        String str = editText.getText().toString().trim();

        return Float.parseFloat(str);
    }
    private int getTotalInches(int foot, int inch) {
        return foot * 12 + inch;
    }
}