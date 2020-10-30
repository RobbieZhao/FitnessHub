package com.example.lifestyle.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.example.lifestyle.R;
import com.example.lifestyle.WeatherViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mButtonCalculator, mButtonWeather, mButtonMap, mButtonProfile, mButtonStep;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double latitude, longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setTitle("Home");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        updateLocation();

        try {
            // Add these lines to add the AWSCognitoAuthPlugin and AWSS3StoragePlugin plugins
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());

            try {
                uploadDatabases();
            } catch (Exception e) {
                Log.i("FileUpload", "Could not upload files");
            }

            Log.i("MyAmplifyApp", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error);
        }

        if (isTablet()) {
            ModuleFragment moduleFragment = new ModuleFragment();
            FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.tablet_left, moduleFragment,"module");
            fTrans.commit();
        } else {
            mButtonCalculator = findViewById(R.id.CalculatorButton);
            mButtonWeather = findViewById(R.id.WeatherButton);
            mButtonMap = findViewById(R.id.MapButton);
            mButtonProfile = findViewById(R.id.ProfileButton);
            mButtonStep = findViewById(R.id.StepButton);

            mButtonCalculator.setOnClickListener(this);
            mButtonWeather.setOnClickListener(this);
            mButtonMap.setOnClickListener(this);
            mButtonProfile.setOnClickListener(this);
            mButtonStep.setOnClickListener(this);
        }
    }

    private boolean isTablet() {
        return getResources().getBoolean(R.bool.isTablet);
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
                break;
            }

            case R.id.StepButton: {
                openStep();
            }
        }
    }

    public void openCalculator() {
        if (isTablet()) {
            FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.tablet_right, new CalculatorFragment(),"calculator");
            fTrans.commit();
        } else {
            Intent calculatorIntent = new Intent(HomeActivity.this, CalculatorActivity.class);
            HomeActivity.this.startActivity(calculatorIntent);
        }
    }

    public void openWeather() {
        updateLocation();

        if (isTablet()) {
            WeatherFragment fragment = new WeatherFragment();

            Bundle bundle = new Bundle();
            bundle.putDouble("latitude", latitude);
            bundle.putDouble("longitude", longitude);
            fragment.setArguments(bundle);

            FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.tablet_right, fragment,"weather");
            fTrans.commit();
        } else {
            Intent weatherIntent = new Intent(HomeActivity.this, WeatherActivity.class);
            weatherIntent.putExtra("latitude", latitude);
            weatherIntent.putExtra("longitude", longitude);
            HomeActivity.this.startActivity(weatherIntent);
        }
    }

    public void openMap() {
        updateLocation();

        Uri searchUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=hike");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, searchUri);
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    public void openProfile() {
        if (isTablet()) {
            ProfileFragment fragment = new ProfileFragment();

            Bundle bundle = new Bundle();
            bundle.putString("MODE", "VIEW");
            fragment.setArguments(bundle);

            FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.tablet_right, fragment,"profile_fragment");
            fTrans.commit();
        } else {
            Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
            HomeActivity.this.startActivity(profileIntent);
        }
    }

    public void openStep() {
        if (isTablet()) {
            FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.tablet_right, new StepFragment(),"step");
            fTrans.commit();
        } else {
            Intent stepIntent = new Intent(HomeActivity.this, StepActivity.class);
            HomeActivity.this.startActivity(stepIntent);
        }
    }

    private void uploadDatabases() {
        uploadFile("profile.db");
        uploadFile("step.db");
        uploadFile("weather.db");
    }

    private void uploadFile(String filename) {

        File file = getApplicationContext().getDatabasePath(filename);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.append("Example file contents");
            writer.close();
        } catch (Exception exception) {
            Log.e("MyAmplifyApp", "Upload failed", exception);
        }

        Amplify.Storage.uploadFile(
                filename,
                file,
                result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey()),
                storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
        );
    }
}