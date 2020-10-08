package com.example.lifestyle.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.lifestyle.R;

public class WeatherActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);

        // Fetch data from home
        double latitude = getIntent().getDoubleExtra("latitude", -360);
        double longitude = getIntent().getDoubleExtra("longitude", -360);

        // Replace the whole screen with weather fragment
        WeatherFragment fragment = new WeatherFragment();

        Bundle bundle = new Bundle();
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);
        fragment.setArguments(bundle);

        FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.whole_screen, fragment,"weather_fragment");
        fTrans.commit();

        getSupportActionBar().setTitle("Weather");
    }
}