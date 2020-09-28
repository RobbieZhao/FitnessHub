package com.example.lifestyle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import org.json.JSONException;

import java.net.URL;

public class WeatherFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {
    private TextView mTvWeather, mTvCurrentTemp, mTvMinTemp, mTvMaxTemp, mTvFeelsTemp,
            mTvPressure, mTvHumidity, mTvWindSpeed;

    private double latitude;
    private double longitude;

    private WeatherData mWeatherData;

    private static final int SEARCH_LOADER = 11;
    public static final String LAT_STRING = "LATITUDE";
    public static final String LON_STRING = "LONGITUDE";

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
        loadWeatherData(latitude, longitude);

        (getActivity()).getSupportLoaderManager().initLoader(SEARCH_LOADER, null, this);

    }

    private void loadWeatherData(double latitude, double longitude) {
        Bundle searchQueryBundle = new Bundle();

        searchQueryBundle.putDouble(LAT_STRING, latitude);
        searchQueryBundle.putDouble(LON_STRING, longitude);

        LoaderManager loaderManager = (getActivity()).getSupportLoaderManager();
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
        return new AsyncTaskLoader<String>(getActivity()) {
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
