package com.example.lifestyle.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
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
    private Sensor mStepCounter;
    public TextView mTvData;
    private final double mThreshold = 0.2;

    //Previous accelerations
    private double last_x, last_y, last_z;

    //current accelerations
    private double now_x, now_y,now_z;
    private boolean mNotFirstTime;

    private long firstUpdateTime;
    private boolean mNotSecondTime;
    private final double UPTATE_INTERVAL_TIME = 1000;
    private double third_x, third_y, third_z;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_sensor,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //Get sensor manager
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        //Get the default StepCounter sensor
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        //Get the text view
        mTvData = getView().findViewById(R.id.tv_sensordata);
    }

    private SensorEventListener mListener = new SensorEventListener() {


        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            //Get the acceleration rates along the y and z axes
            now_x = sensorEvent.values[0];
            now_y = sensorEvent.values[1];
            now_z = sensorEvent.values[2];

            if (mNotFirstTime) {
                double dx = Math.abs(last_x - now_x);
                double dy = Math.abs(last_y - now_y);
                double dz = Math.abs(last_z - now_z);

                //Check if the values of acceleration have changed on any pair of axes
                if ((dx > mThreshold && dy > mThreshold) ||
                        (dx > mThreshold && dz > mThreshold) ||
                        (dy > mThreshold && dz > mThreshold)) {

                    //start step counter and display the steps
                    mTvData.setText("" + String.valueOf(sensorEvent.values[0]));

                    firstUpdateTime = System.currentTimeMillis();

                    mNotSecondTime = true;

                    third_x = last_x;
                    third_y = last_y;
                    third_z = last_z;

                    if (mNotSecondTime) {

                        long currentUpdateTime = System.currentTimeMillis();
                        long timeInterval = currentUpdateTime - firstUpdateTime;
                        firstUpdateTime = currentUpdateTime;

                        if (timeInterval < UPTATE_INTERVAL_TIME) {
                            dx = Math.abs(third_x - now_x);
                            dy = Math.abs(third_y - now_y);
                            dz = Math.abs(third_z - now_z);

                            if ((dx > mThreshold && dy > mThreshold) ||
                                    (dx > mThreshold && dz > mThreshold) ||
                                    (dy > mThreshold && dz > mThreshold)) {

                                //step counter reset to 0
                                mTvData.setText("0");
                            }
                        }
                    }
                }
            }
            last_x = now_x;
            last_y = now_y;
            last_z = now_z;
            mNotFirstTime = true;
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if(mStepCounter!=null){
            mSensorManager.registerListener(mListener,mStepCounter,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mStepCounter!=null){
            mSensorManager.unregisterListener(mListener);
        }
    }
}
