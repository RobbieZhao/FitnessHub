package com.example.lifestyle.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.lifestyle.R;
import com.example.lifestyle.WeatherViewModel;
import com.example.lifestyle.data.WeatherData;
import com.example.lifestyle.utils.WeatherUtils;


public class WeatherFragment extends Fragment  {
    private TextView mTvWeather, mTvCurrentTemp, mTvMinTemp, mTvMaxTemp, mTvFeelsTemp,
            mTvPressure, mTvHumidity, mTvWindSpeed;

    private double latitude;
    private double longitude;
    
    private WeatherViewModel mWeatherViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_weather,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mTvWeather = getView().findViewById(R.id.Weather);
        mTvCurrentTemp = getView().findViewById(R.id.CurrentTemperature);
        mTvMinTemp = getView().findViewById(R.id.MinTemperature);
        mTvMaxTemp = getView().findViewById(R.id.MaxTemperature);
        mTvFeelsTemp = getView().findViewById(R.id.FeelsLikeTemp);
        mTvPressure = getView().findViewById(R.id.Pressure);
        mTvHumidity = getView().findViewById(R.id.Humidity);
        mTvWindSpeed = getView().findViewById(R.id.WindSpeed);

        latitude = getArguments().getDouble("latitude");
        longitude = getArguments().getDouble("longitude");

        //Create the view model
        mWeatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);

        //Set the observer
        mWeatherViewModel.getData().observe(getViewLifecycleOwner(), nameObserver);

        loadWeatherData();
    }

    //create an observer that watches the LiveData<WeatherData> object
    final Observer<WeatherData> nameObserver  = new Observer<WeatherData>() {
        @Override
        public void onChanged(@Nullable final WeatherData weatherData) {
            // Update the UI if this data variable changes
            if(weatherData!=null) {

                mTvWeather.append(" " + weatherData.getCurrentCondition().getCondition());

                mTvCurrentTemp.append(WeatherUtils.kelvinToCelsius(weatherData.getTemperature().getTemp()));
                mTvMinTemp.append(WeatherUtils.kelvinToCelsius(weatherData.getTemperature().getMinTemp()));
                mTvMaxTemp.append(WeatherUtils.kelvinToCelsius(weatherData.getTemperature().getMaxTemp()));
                mTvFeelsTemp.append(WeatherUtils.kelvinToCelsius(weatherData.getTemperature().getFeelTemp()));

                mTvPressure.append(WeatherUtils.pressurePlusUnit(weatherData.getCurrentCondition().getPressure()));
                mTvHumidity.append(weatherData.getCurrentCondition().getHumidity() + "%");
                mTvWindSpeed.append(WeatherUtils.windSpeedPlusUnit(Math.round(weatherData.getWind().getSpeed())));
            }
        }
    };

    void loadWeatherData() {
        mWeatherViewModel.setLocation(latitude, longitude);
    }
}


