package com.example.lifestyle.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "weather_table", primaryKeys = {"latitude", "longitude"})
public class WeatherTable {
    @NonNull
    @ColumnInfo(name = "latitude")
    private Double latitude;

    @NonNull
    @ColumnInfo(name = "longitude")
    private Double longitude;


    @NonNull
    @ColumnInfo(name = "weatherdata")
    private String weatherJson;

    public WeatherTable(@NonNull Double latitude, @NonNull Double longitude, @NonNull String weatherJson){
        this.latitude = latitude;
        this.longitude = longitude;
        this.weatherJson = weatherJson;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setWeatherJson(String weatherdata){
        this.weatherJson = weatherdata;
    }

    public String getWeatherJson(){
        return weatherJson;
    }
}
