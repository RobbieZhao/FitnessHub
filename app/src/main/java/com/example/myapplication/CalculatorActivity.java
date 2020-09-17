package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtFoot, mEtInch, mEtWeight;
    private Button mButtonCalculator;
    private TextView mTvBMR;

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

    }
}