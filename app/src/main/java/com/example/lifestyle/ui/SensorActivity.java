package com.example.lifestyle.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.example.lifestyle.R;

//shake once to open step counter, shake twice in 1s to stop the step counter

public class SensorActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private Sensor mStepCounter;
    public TextView mTvData;
    private final double mThreshold = 2.0;

    //Previous accelerations
    private double last_x, last_y, last_z;

    //current accelerations
    private double now_x, now_y,now_z;
    private boolean mNotFirstTime;

    private long firstUpdateTime;
    private boolean mNotSecondTime;
    private final double UPTATE_INTERVAL_TIME = 1000;
    private double third_x, third_y, third_z;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        //Get sensor manager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Get the default StepCounter sensor
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        //Get the text view
        mTvData = (TextView) findViewById(R.id.tv_sensordata);

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
    protected void onResume() {
        super.onResume();
        if(mStepCounter!=null){
            mSensorManager.registerListener(mListener,mStepCounter,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mStepCounter!=null){
            mSensorManager.unregisterListener(mListener);
        }
    }

}