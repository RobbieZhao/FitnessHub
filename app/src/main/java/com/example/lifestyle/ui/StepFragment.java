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

import com.example.lifestyle.R;

public class StepFragment extends Fragment {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mStepDetector;
    public TextView mTvData;
    private final double mThreshold = 20;

    private long currentTime;
    private long lastUpdateTime;

    private boolean isStepDetectorEnabled;

    private int mSteps;

    //Previous accelerations
    private double last_x, last_y, last_z;

    //current accelerations
    private double now_x, now_y,now_z;
    private boolean mNotFirstTime;


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

        mSteps = 0;

        isStepDetectorEnabled = false;
        lastUpdateTime = System.currentTimeMillis();
    }

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
                    lastUpdateTime = currentTime;
                    if (isStepDetectorEnabled) {
                        disableStepDetector();
                        Log.d("StepDetector", "disable");
                    }
                    else {
                        enableStepDetector();
                        Log.d("StepDetector", "enable");
                    }
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
