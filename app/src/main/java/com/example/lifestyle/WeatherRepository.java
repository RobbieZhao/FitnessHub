package com.example.lifestyle;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.example.lifestyle.data.WeatherData;
import com.example.lifestyle.db.WeatherDao;
import com.example.lifestyle.db.WeatherRoomDatabase;
import com.example.lifestyle.db.WeatherTable;
import com.example.lifestyle.utils.WeatherUtils;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;

public class WeatherRepository {
    private final MutableLiveData<WeatherData> jsonData = new MutableLiveData<WeatherData>();
    private double mLatitude, mLongitude;
    private String mJsonString;
    private WeatherDao mWeatherDao;

    WeatherRepository(Application application){
        WeatherRoomDatabase db = WeatherRoomDatabase.getDatabase(application);
        mWeatherDao = db.weatherDao();
//        loadData();
    }

    public void setLocation(double latitude, double longitude){
        mLatitude = latitude;
        mLongitude = longitude;
        loadData();
    }

    private void insert(){
        WeatherTable weatherTable = new WeatherTable(mLatitude, mLongitude, mJsonString);
        new insertAsyncTask(mWeatherDao).execute(weatherTable);
    }

    public MutableLiveData<WeatherData> getData() {
        return jsonData;
    }

    private void loadData()
    {
        new fetchWeatherAsyncTask(this).execute(mLatitude, mLongitude);
    }

    private static class fetchWeatherAsyncTask extends AsyncTask<Double,Void,String> {
        private WeakReference<WeatherRepository> mRepoWReference;

        fetchWeatherAsyncTask(WeatherRepository repo)
        {
            mRepoWReference = new WeakReference<WeatherRepository>(repo);
        }
        @Override
        protected String doInBackground(Double... coordinates) {
            double latitude = coordinates[0];
            double longitude = coordinates[1];

            URL weatherDataURL = WeatherUtils.buildURLFromString(latitude, longitude);

            String retrievedJsonData = null;
            try {
                retrievedJsonData = WeatherUtils.getDataFromURL(weatherDataURL);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return retrievedJsonData;
        }

        @Override
        protected void onPostExecute(String returnedJson) {
            WeatherRepository localWRvar = mRepoWReference.get();
            if(returnedJson!=null) {
                localWRvar.mJsonString = returnedJson;
                localWRvar.insert();
                try {
                    localWRvar.jsonData.setValue(WeatherUtils.getWeatherData(returnedJson));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private static class insertAsyncTask extends AsyncTask<WeatherTable,Void,Void>{
        private WeatherDao mAsyncTaskDao;

        insertAsyncTask(WeatherDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(WeatherTable... weatherTables) {
            mAsyncTaskDao.insert(weatherTables[0]);
            return null;
        }
    }
}


