package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mButtonCalculator, mButtonWeather, mButtonMap, mButtonProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mButtonCalculator = findViewById(R.id.CalculatorButton);
        mButtonWeather = findViewById(R.id.WeatherButton);
        mButtonMap = findViewById(R.id.MapButton);
        mButtonProfile = findViewById(R.id.ProfileButton);

        mButtonCalculator.setOnClickListener(this);
        mButtonWeather.setOnClickListener(this);
        mButtonMap.setOnClickListener(this);
        mButtonProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.CalculatorButton: {
                openCalculator();
                break;
            }

            case R.id.WeatherButton: {
                openWeather();
                break;
            }

            case R.id.MapButton: {
                openMap();
                break;
            }

            case R.id.ProfileButton: {
                openProfile();
            }
        }
    }

    private void openCalculator() {
        Intent myIntent = new Intent(HomeActivity.this, CalculatorActivity.class);
        HomeActivity.this.startActivity(myIntent);
    }

    private void openWeather() {

    }

    private void openMap() {
        Uri searchUri = Uri.parse("geo:40.767778,-111.845205?q=hike");

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, searchUri);
        if(mapIntent.resolveActivity(getPackageManager())!=null){
            startActivity(mapIntent);
        }
    }

    private void openProfile() {
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        intent.putExtra("ParentActivity", "HomeActivity");
        HomeActivity.this.startActivity(intent);
    }
}