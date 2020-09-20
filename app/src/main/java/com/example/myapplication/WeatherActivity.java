package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;

import java.net.URL;

public class WeatherActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{

    private TextView mTvWeather, mTvCurrentTemp, mTvMinTemp, mTvMaxTemp, mTvFeelsTemp,
            mTvPressure, mTvHumidity, mTvWindSpeed;

    private double latitude;
    private double longitude;

    private WeatherData mWeatherData;

    private static final int SEARCH_LOADER = 11;
    public static final String LAT_STRING = "LATITUDE";
    public static final String LON_STRING = "LONGITUDE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        mTvWeather = findViewById(R.id.Weather);
        mTvCurrentTemp = findViewById(R.id.CurrentTemperature);
        mTvMinTemp = findViewById(R.id.MinTemperature);
        mTvMaxTemp = findViewById(R.id.MaxTemperature);
        mTvFeelsTemp = findViewById(R.id.FeelsLikeTemp);
        mTvPressure = findViewById(R.id.Pressure);
        mTvHumidity = findViewById(R.id.Humidity);
        mTvWindSpeed = findViewById(R.id.WindSpeed);

        getSupportLoaderManager().initLoader(SEARCH_LOADER, null, this);

        latitude = getIntent().getDoubleExtra("latitude", -360);
        longitude = getIntent().getDoubleExtra("longitude", -360);

        if (latitude != -360 && longitude != -360)
            loadWeatherData(latitude, longitude);
    }

    private void loadWeatherData(double latitude, double longitude) {
        Bundle searchQueryBundle = new Bundle();

        searchQueryBundle.putDouble(LAT_STRING, latitude);
        searchQueryBundle.putDouble(LON_STRING, longitude);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> searchLoader = loaderManager.getLoader(SEARCH_LOADER);
        if (searchLoader == null) {
            loaderManager.initLoader(SEARCH_LOADER, searchQueryBundle, this);
        } else {
            loaderManager.restartLoader(SEARCH_LOADER, searchQueryBundle, this);
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable final Bundle bundle) {
        return new AsyncTaskLoader<String>(this) {
            private String mLoaderData;

            @Override
            protected void onStartLoading() {
                if (bundle == null) {
                    return;
                }
                if (mLoaderData != null) {
                    //Cache data for onPause instead of loading all over again
                    //Other config changes are handled automatically
                    deliverResult(mLoaderData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                double latitude = bundle.getDouble(LAT_STRING);
                double longitude = bundle.getDouble(LON_STRING);
                URL weatherDataURL = WeatherUtils.buildURLFromString(latitude, longitude);
                String jsonWeatherData;
                try {
                    jsonWeatherData = WeatherUtils.getDataFromURL(weatherDataURL);
                    return jsonWeatherData;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(String data) {
                mLoaderData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String jsonWeatherData) {
        if (jsonWeatherData != null) {
            try {
                mWeatherData = WeatherUtils.getWeatherData(jsonWeatherData);

                mTvWeather.append(" " + mWeatherData.getCurrentCondition().getCondition());
                mTvCurrentTemp.append(WeatherUtils.kelvinToCelsius(mWeatherData.getTemperature().getTemp()));
                mTvMinTemp.append(WeatherUtils.kelvinToCelsius(mWeatherData.getTemperature().getMinTemp()));
                mTvMaxTemp.append(WeatherUtils.kelvinToCelsius(mWeatherData.getTemperature().getMaxTemp()));
                mTvFeelsTemp.append(WeatherUtils.kelvinToCelsius(mWeatherData.getTemperature().getFeelTemp()));

                mTvPressure.append(WeatherUtils.pressurePlusUnit(mWeatherData.getCurrentCondition().getPressure()));
                mTvHumidity.append(mWeatherData.getCurrentCondition().getHumidity() + "%");
                mTvWindSpeed.append(WeatherUtils.windSpeedPlusUnit(Math.round(mWeatherData.getWind().getSpeed())));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}