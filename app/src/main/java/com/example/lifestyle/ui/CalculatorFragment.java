package com.example.lifestyle.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lifestyle.ProfileViewModel;
import com.example.lifestyle.R;

import java.util.HashMap;

public class CalculatorFragment extends Fragment implements View.OnClickListener {

    private EditText mEtFoot, mEtInch, mEtWeight, mEtGainWeight, mEtLoseWeight;
    private Button mButtonCalculator;
    private TextView mTvBMR, mTvBMI, mTvCalories, mTvWarning;
    private RadioGroup mRgStatus, mRgGoal;
    private HashMap<String, String> ProfileData;
    private String directory;
    private Activity activity;

    private ProfileViewModel mprofileviewmodel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.print("we are here");
        return inflater.inflate(R.layout.activity_calculator,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mEtFoot = getView().findViewById(R.id.editTextHeightFoot);
        mEtInch = getView().findViewById(R.id.editTextHeightInch);
        mEtWeight = getView().findViewById(R.id.EditTextWeight);
        mEtGainWeight = getView().findViewById(R.id.EtGainWeight);
        mEtLoseWeight = getView().findViewById(R.id.EtLoseWeight);

        mButtonCalculator = getView().findViewById(R.id.GetMyPlanButton);

        mTvBMR = getView().findViewById(R.id.BMROutput);
        mTvBMI = getView().findViewById(R.id.BMIOutput);
        mTvCalories = getView().findViewById(R.id.CaloriesOutput);
        mTvWarning = getView().findViewById(R.id.EatingHabitOutput);

        mRgStatus = getView().findViewById(R.id.RgStatus);
        mRgGoal = getView().findViewById(R.id.RgGoal);

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

        activity = getActivity();
        directory = activity.getFilesDir().getAbsolutePath();

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
        ProfileData = Utils.readData(directory, Utils.data_file);

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
            boolean isMale = isMale();
            int age = getAge();
            int[] height = getHeight();
            double weight = getWeight();
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

            Utils.storeData(ProfileData, directory, Utils.data_file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int[] getHeight() throws Exception {
        int foot = Utils.getIntegerInput(mEtFoot);
        int inch = Utils.getIntegerInput(mEtInch);

        String[] messages = Utils.checkHeightInput(foot, inch, true);
        if (!messages[0].isEmpty() && !messages[1].isEmpty()) {
            Toast.makeText(activity, messages[0], Toast.LENGTH_SHORT).show();
            throw new Exception(messages[1]);
        }

        return new int[] {foot, inch};
    }

    private int getAge() throws Exception {
        if (!ProfileData.containsKey("age")) {
            Toast.makeText(activity, "Fill out age in the profile!", Toast.LENGTH_SHORT).show();
            throw new Exception("Unknown age");
        }

        // Age is already sanitized in the Profile page
        return Integer.parseInt(ProfileData.get("age"));
    }

    private double getWeight() throws Exception {
        double weight = Utils.getDoubleInput(mEtWeight);

        if (weight == -1) {
            Toast.makeText(activity, "Please enter weight!", Toast.LENGTH_SHORT).show();
            throw new Exception("Empty weight");
        } else if (weight == 0) {
            Toast.makeText(activity, "Weight can't be 0!", Toast.LENGTH_SHORT).show();
            throw new Exception("Invalid weight");
        }

        return weight;
    }

    private boolean isMale() throws Exception {
        if (!ProfileData.containsKey("sex")) {
            Toast.makeText(activity, "Fill out sex in the profile!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(activity, "Please select status!", Toast.LENGTH_SHORT).show();
            throw new Exception("Empty status");
        }
    }

    private double getGoal() throws Exception {
        int selectedID = mRgGoal.getCheckedRadioButtonId();

        double goal;
        if (selectedID == R.id.loseWeight) {
            goal = Utils.getDoubleInput(mEtLoseWeight);
            if (goal == -1) {
                Toast.makeText(activity, "Please enter goal!", Toast.LENGTH_SHORT).show();
                throw new Exception("Empty lose weight goal");
            }
            goal = -goal;
        } else if (selectedID == R.id.maintainWeight) {
            goal = 0;
        } else if (selectedID == R.id.gainWeight) {
            goal = Utils.getDoubleInput(mEtGainWeight);
            if (goal == -1) {
                Toast.makeText(activity, "Please enter goal!", Toast.LENGTH_SHORT).show();
                throw new Exception("Empty gain weight goal");
            }
        } else {
            Toast.makeText(activity, "Please select goal!", Toast.LENGTH_SHORT).show();
            throw new Exception("Empty gain weight goal");
        }

        return goal;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mTvBMI.setText(savedInstanceState.getString("BMI"));
            mTvBMR.setText(savedInstanceState.getString("BMR"));
            mTvCalories.setText(savedInstanceState.getString("Calorie"));
            mTvWarning.setText(savedInstanceState.getString("Warning"));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Put them in the outgoing Bundle
        outState.putString("BMI", mTvBMI.getText().toString());
        outState.putString("BMR", mTvBMR.getText().toString());
        outState.putString("Calorie", mTvCalories.getText().toString());
        outState.putString("Warning", mTvWarning.getText().toString());
    }
}