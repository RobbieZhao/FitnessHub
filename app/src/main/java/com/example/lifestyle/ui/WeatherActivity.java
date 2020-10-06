package com.example.lifestyle.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.example.lifestyle.R;
import com.example.lifestyle.WeatherViewModel;
import com.example.lifestyle.data.WeatherData;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView mTvWeather, mTvCurrentTemp, mTvMinTemp, mTvMaxTemp, mTvFeelsTemp,
            mTvPressure, mTvHumidity, mTvWindSpeed;

    private TextView mEtLocation;

    private Button mBtSubmit;

    private double latitude;
    private double longitude;

    private WeatherViewModel mWeatherViewModel;

    private WeatherData mWeatherData;

    private static final int SEARCH_LOADER = 11;
    public static final String LAT_STRING = "LATITUDE";
    public static final String LON_STRING = "LONGITUDE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);

        mTvWeather = findViewById(R.id.Weather);
        mTvCurrentTemp = findViewById(R.id.CurrentTemperature);
        mTvMinTemp = findViewById(R.id.MinTemperature);
        mTvMaxTemp = findViewById(R.id.MaxTemperature);
        mTvFeelsTemp = findViewById(R.id.FeelsLikeTemp);
        mTvPressure = findViewById(R.id.Pressure);
        mTvHumidity = findViewById(R.id.Humidity);
        mTvWindSpeed = findViewById(R.id.WindSpeed);
        mEtLocation = findViewById(R.id.Location);
        mBtSubmit = findViewById(R.id.SubmitButtonUsers);
        mBtSubmit.setOnClickListener(this);

        mWeatherViewModel.getData().observe(this, nameObserver);

        // Fetch data from home
        double latitude = getIntent().getDoubleExtra("latitude", -360);
        double longitude = getIntent().getDoubleExtra("longitude", -360);
    }
        // Replace the whole screen with weather fragment
        //WeatherFragment fragment = new WeatherFragment();

//        Bundle bundle = new Bundle();
//        bundle.putDouble("latitude", latitude);
//        bundle.putDouble("longitude", longitude);
//        fragment.setArguments(bundle);
//
//        FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
//        fTrans.replace(R.id.whole_screen, fragment,"weather_fragment");
//        fTrans.commit();
        final Observer<WeatherData> nameObserver = new Observer<WeatherData>() {
            @Override
            public void onChanged(@Nullable final WeatherData weatherData) {
                // Update the UI if this data variable changes
                if (weatherData != null) {
                    mTvCurrentTemp.setText(WeatherUtils.kelvinToCelsius(mWeatherData.getTemperature().getTemp()));
                    mTvHumidity.setText("" + weatherData.getCurrentCondition().getHumidity() + "%");
                    mTvPressure.setText("" + weatherData.getCurrentCondition().getPressure() + " hPa");
                }
            }
        };

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.SubmitButtonUsers:{
                    //Get the string from the edit text and sanitize the input
                    String inputFromEt = mEtLocation.getText().toString().replace(' ','&');
                    loadWeatherData(inputFromEt);
                }
                break;
            }
        }

    void loadWeatherData(String location){
        //pass the location in to the view model
        mWeatherViewModel.setLocation(location);
    }


//        getSupportActionBar().setTitle("Weather");
//    }
}