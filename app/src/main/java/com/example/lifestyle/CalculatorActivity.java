package com.example.lifestyle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtFoot, mEtInch, mEtWeight, mEtGainWeight, mEtLoseWeight;
    private Button mButtonCalculator;
    private TextView mTvBMR, mTvBMI, mTvCalories, mTvWarning;
    private RadioGroup mRgStatus, mRgGoal;
    private HashMap<String, String> ProfileData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        mEtFoot = findViewById(R.id.editTextHeightFoot);
        mEtInch = findViewById(R.id.editTextHeightInch);
        mEtWeight = findViewById(R.id.EditTextWeight);
        mEtGainWeight = findViewById(R.id.EtGainWeight);
        mEtLoseWeight = findViewById(R.id.EtLoseWeight);

        mButtonCalculator = findViewById(R.id.GetMyPlanButton);

        mTvBMR = findViewById(R.id.BMROutput);
        mTvBMI = findViewById(R.id.BMIOutput);
        mTvCalories = findViewById(R.id.CaloriesOutput);
        mTvWarning = findViewById(R.id.EatingHabitOutput);

        mRgStatus = findViewById(R.id.RgStatus);
        mRgGoal = findViewById(R.id.RgGoal);

        mButtonCalculator.setOnClickListener(this);
        mRgGoal.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.gainWeight) {
                    Utils.enableEditText(mEtGainWeight);
                    Utils.disableEditText(mEtLoseWeight);
                } else if (checkedId == R.id.loseWeight) {
                    Utils.enableEditText(mEtLoseWeight);
                    Utils.disableEditText(mEtGainWeight);
                } else {
                    Utils.disableEditText(mEtLoseWeight);
                    Utils.disableEditText(mEtGainWeight);
                }
            }
        });
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
        ProfileData = Utils.readData(getFilesDir().getAbsolutePath(), Utils.data_file);

        if (ProfileData.containsKey("foot"))
            mEtFoot.setText(ProfileData.get("foot"));
        if (ProfileData.containsKey("inch"))
            mEtInch.setText(ProfileData.get("inch"));
        if (ProfileData.containsKey("weight"))
            mEtWeight.setText(ProfileData.get("weight"));
        if (ProfileData.containsKey("status")) {
            if (ProfileData.get("status").equals("active"))
                mRgStatus.check(R.id.Active);
            else mRgStatus.check(R.id.Sedentary);
        }
        if (ProfileData.containsKey("goal")) {
            double goal = Double.parseDouble(ProfileData.get("goal"));
            if (goal < 0) {
                mRgGoal.check(R.id.loseWeight);
                mEtLoseWeight.setText(Double.toString(-goal));
            } else if (goal > 0) {
                mRgGoal.check(R.id.gainWeight);
                mEtGainWeight.setText(Double.toString(goal));
            } else {
                mRgGoal.check(R.id.maintainWeight);
            }
        } else {
            Utils.disableEditText(mEtGainWeight);
            Utils.disableEditText(mEtLoseWeight);
        }
    }

    private void generatePlan() {
        try {
            double weight = getWeight();
            int[] height = getHeight();
            int age = getAge();
            boolean isMale = isMale();
            boolean status = isActive();
            double goal = getGoal();

            int totalInches = Utils.calculateTotalInches(height[0], height[1]);

            double BMR = Utils.calculateBMR(weight, totalInches, age, isMale);
            double BMI = Utils.calculateBMI(weight, totalInches);
            double dailyCalorie = Utils.calculateCaloriesIntake(BMR, status, goal);

            mTvBMR.setText("BMR: " + String.format("%.2f", BMR));
            mTvBMI.setText("BMI: " + String.format("%.2f", BMI));
            mTvCalories.setText("You should eat " + String.format("%.2f", dailyCalorie) + " calories/day");
            String warning = "";
            if (Math.abs(goal) > 2)
                warning += "Take it easy on your goal! no need to rush\n";
            if ((dailyCalorie < 1200 && isMale) || (dailyCalorie < 1000 && !isMale))
                warning += "You are not eating enough each day!";
            mTvWarning.setText(warning);

            ProfileData.put("foot", Integer.toString(height[0]));
            ProfileData.put("inch", Integer.toString(height[1]));
            ProfileData.put("weight", Double.toString(weight));
            if (status)
                ProfileData.put("status", "active");
            else
                ProfileData.put("status", "sedentary");
            ProfileData.put("goal", Double.toString(goal));

            Utils.storeData(ProfileData, getFilesDir().getAbsolutePath(), Utils.data_file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int[] getHeight() throws Exception {
        int foot = Utils.getIntegerInput(mEtFoot);
        int inch = Utils.getIntegerInput(mEtInch);

        String[] messages = Utils.checkHeightInput(foot, inch, true);
        if (!messages[0].isEmpty() && !messages[1].isEmpty()) {
            Toast.makeText(this, messages[0], Toast.LENGTH_SHORT).show();
            throw new Exception(messages[1]);
        }

        return new int[] {foot, inch};
    }

    private int getAge() throws Exception {
        if (!ProfileData.containsKey("age")) {
            Toast.makeText(this, "Fill out age in the profile!", Toast.LENGTH_SHORT).show();
            throw new Exception("Unknown age");
        }

        // Age is already sanitized in the Profile page
        return Integer.parseInt(ProfileData.get("age"));
    }

    private double getWeight() throws Exception {
        double weight = Utils.getDoubleInput(mEtWeight);

        if (weight == -1) {
            Toast.makeText(this, "Please enter weight!", Toast.LENGTH_SHORT).show();
            throw new Exception("Empty weight");
        } else if (weight == 0) {
            Toast.makeText(this, "Weight can't be 0!", Toast.LENGTH_SHORT).show();
            throw new Exception("Invalid weight");
        }

        return weight;
    }

    private boolean isMale() throws Exception {
        if (!ProfileData.containsKey("sex")) {
            Toast.makeText(this, "Fill out sex in the profile!", Toast.LENGTH_SHORT).show();
            throw new Exception("Unknown sex");
        }

        String sex = ProfileData.get("sex");

        return sex.equals("male");
    }

    private boolean isActive() throws Exception {
        int selectedID = mRgStatus.getCheckedRadioButtonId();

        if (selectedID == R.id.Active) {
            return true;
        } else if (selectedID == R.id.Sedentary) {
            return false;
        } else {
            Toast.makeText(this, "Please select status!", Toast.LENGTH_SHORT).show();
            throw new Exception("Empty status");
        }
    }

    private double getGoal() throws Exception {
        int selectedID = mRgGoal.getCheckedRadioButtonId();

        double goal;
        if (selectedID == R.id.loseWeight) {
            goal = Utils.getDoubleInput(mEtLoseWeight);
            if (goal == -1) {
                Toast.makeText(this, "Please enter goal!", Toast.LENGTH_SHORT).show();
                throw new Exception("Empty lose weight goal");
            }
            goal = -goal;
        } else if (selectedID == R.id.maintainWeight) {
            goal = 0;
        } else if (selectedID == R.id.gainWeight) {
            goal = Utils.getDoubleInput(mEtGainWeight);
            if (goal == -1) {
                Toast.makeText(this, "Please enter goal!", Toast.LENGTH_SHORT).show();
                throw new Exception("Empty gain weight goal");
            }
        } else {
            Toast.makeText(this, "Please select goal!", Toast.LENGTH_SHORT).show();
            throw new Exception("Empty gain weight goal");
        }

        return goal;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Put them in the outgoing Bundle
        outState.putString("BMI", mTvBMI.getText().toString());
        outState.putString("BMR", mTvBMR.getText().toString());
        outState.putString("Calorie", mTvCalories.getText().toString());
        outState.putString("Warning", mTvWarning.getText().toString());

        //Save the view hierarchy
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //Restore stuff
        mTvBMI.setText(savedInstanceState.getString("BMI"));
        mTvBMR.setText(savedInstanceState.getString("BMR"));
        mTvCalories.setText(savedInstanceState.getString("Calorie"));
        mTvWarning.setText(savedInstanceState.getString("Warning"));

        //Restore the view hierarchy automatically
        super.onRestoreInstanceState(savedInstanceState);
    }
}