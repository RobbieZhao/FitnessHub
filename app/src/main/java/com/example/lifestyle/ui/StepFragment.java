package com.example.lifestyle.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lifestyle.ProfileViewModel;
import com.example.lifestyle.R;
import com.example.lifestyle.StepViewModel;
import com.example.lifestyle.data.ProfileData;
import com.example.lifestyle.data.StepData;

import java.util.ArrayList;

public class StepFragment extends Fragment {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mStepDetector;

    private TextView mTvData;
    private TextView mTvStatus;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private StepViewModel mStepViewModel;
    private final double mThreshold = 5;

    private long currentTime;
    private long lastUpdateTime;

    private boolean isStepDetectorEnabled;

    private int mSteps;

    private double last_x, last_y, last_z;
    private double now_x, now_y,now_z;

    private ArrayList<StepData> mStepHistoryData;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_step,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //Get sensor manager
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        //Get the default StepCounter sensor
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        //Get the text view
        mTvData = getView().findViewById(R.id.textview_stepCounter);
        mTvStatus = getView().findViewById(R.id.textView_start);
        mSteps = 0;

        isStepDetectorEnabled = false;
        lastUpdateTime = System.currentTimeMillis();

        //Get the recycler view
        mRecyclerView = getView().findViewById(R.id.rv_StepHistory);

        //Set the layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Create the view model
        mStepViewModel= ViewModelProviders.of(this).get(StepViewModel.class);

        //Set the observer
        (mStepViewModel.getData()).observe(getViewLifecycleOwner(), nameObserver);
        mStepViewModel.fetchStepData();
    }

    //create an observer that watches the LiveData<profilerData> object
    final Observer<ArrayList<StepData>> nameObserver = new Observer<ArrayList<StepData>>() {

        @Override
        public void onChanged(ArrayList<StepData> stepData) {
            //Set the adapter
            mStepHistoryData = stepData;
            mAdapter = new StepHistoryAdapter(stepData);
            mRecyclerView.setAdapter(mAdapter);
        }
    };

    private SensorEventListener mStepDetectorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            mSteps += 1;
            mTvData.setText(String.valueOf(mSteps));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    private SensorEventListener mAccelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            //Get the acceleration rates along the y and z axes
            now_x = event.values[0];
            now_y = event.values[1];
            now_z = event.values[2];

            currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime >= 1000) {
                double dx = Math.abs(last_x - now_x);
                double dy = Math.abs(last_y - now_y);
                double dz = Math.abs(last_z - now_z);

                //Check if the values of acceleration have changed on any pair of axes
                if ((dx > mThreshold && dy > mThreshold)
                        || (dx > mThreshold && dz > mThreshold)
                        || (dy > mThreshold && dz > mThreshold)) {
                    if (isStepDetectorEnabled) {
                        Log.d("StepDetector", "disable");

                        mTvData.setText("0");
                        mTvStatus.setText("Shake to Start!");

                        StepData stepData = new StepData();
                        stepData.setStart(lastUpdateTime);
                        stepData.setEnd(System.currentTimeMillis());
                        stepData.setStep(mSteps);

                        mStepHistoryData.add(stepData);
                        mAdapter.notifyDataSetChanged();

                        mStepViewModel.storeStepData(stepData);

                        disableStepDetector();
                    }
                    else {
                        enableStepDetector();
                        Log.d("StepDetector", "enable");

                        mTvStatus.setText("Keep it up babe!");
                    }
                    lastUpdateTime = currentTime;
                }
            }

            last_x = now_x;
            last_y = now_y;
            last_z = now_z;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (mAccelerometer != null) {
            mSensorManager.registerListener(mAccelerometerListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mStepDetector != null) {
            mSensorManager.registerListener(mStepDetectorListener, mStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void enableStepDetector() {
        if (mStepDetector != null) {
            mSensorManager.registerListener(mStepDetectorListener, mStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
        }
        isStepDetectorEnabled = true;
    }

    public void disableStepDetector() {
        if (mStepDetector != null) {
            mSensorManager.unregisterListener(mStepDetectorListener);
        }
        isStepDetectorEnabled = false;
        mSteps = 0;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAccelerometer != null) {
            mSensorManager.unregisterListener(mAccelerometerListener);
        }
        if (mStepDetector != null) {
            mSensorManager.unregisterListener(mStepDetectorListener);
        }
    }
}
