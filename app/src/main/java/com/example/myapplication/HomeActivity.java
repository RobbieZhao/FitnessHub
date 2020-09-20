package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mButtonCalculator, mButtonWeather, mButtonMap, mButtonProfile;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double latitude, longitude;

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

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        updateLocation();
    }

    private void updateLocation() {
        if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(HomeActivity.this,
                                    Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1
                            );

                            latitude = addresses.get(0).getLatitude();
                            longitude = addresses.get(0).getLongitude();
                        } catch (Exception e) {

                        }
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
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
        Intent calculatorIntent = new Intent(HomeActivity.this, CalculatorActivity.class);
        HomeActivity.this.startActivity(calculatorIntent);
    }

    private void openWeather() {
        updateLocation();

        Intent weatherIntent = new Intent(HomeActivity.this, WeatherActivity.class);
        weatherIntent.putExtra("latitude", latitude);
        weatherIntent.putExtra("longitude", longitude);
        HomeActivity.this.startActivity(weatherIntent);
    }

    private void openMap() {
        updateLocation();

        Uri searchUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=hike");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, searchUri);
        if(mapIntent.resolveActivity(getPackageManager()) != null){
            startActivity(mapIntent);
        }
    }

    private void openProfile() {
        Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
        profileIntent.putExtra("ParentActivity", "HomeActivity");
        HomeActivity.this.startActivity(profileIntent);
    }
}