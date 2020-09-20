package com.example.lifestyle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class WeatherUtils {
    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?";
    private static String APPIDQUERY = "&appid=";
    private static final String app_id="51d2ee9441b8ede6bc0951007ce24ccd";

    public static final String temperatureUnit = "°C";
    public static final String pressureUnit = "hPa";
    public static final String windSpeedUnit = "meter/sec";
    public static final String visibilityUnit = "meters";

    public static URL buildURLFromString(double latitude, double longitude){
        URL myURL = null;
        try{
            String coordinate = "lat=" + latitude + "&" + "lon=" + longitude;
            myURL = new URL(BASE_URL + coordinate + APPIDQUERY + app_id);
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return myURL;
    }

    public static String getDataFromURL(URL url) throws IOException{
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();

            //The scanner trick: search for the next "beginning" of the input stream
            //No need to user BufferedReader
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return scanner.next();
            }
            else{
                return null;
            }

        }finally {
            urlConnection.disconnect();
        }
    }

    public static WeatherData getWeatherData(String data) throws JSONException {
        WeatherData weatherData = new WeatherData();

        //Start parsing JSON data
        JSONObject jsonObject = new JSONObject(data); //Must throw JSONException

        WeatherData.CurrentCondition currentCondition = weatherData.getCurrentCondition();
        WeatherData.Temperature temperature = weatherData.getTemperature();
        WeatherData.Wind wind = weatherData.getWind();

        //Get the actual weather information
        JSONArray jsonWeatherArray = jsonObject.getJSONArray("weather");
        JSONObject jsonWeather = jsonWeatherArray.getJSONObject(0);
        JSONObject jsonMain = jsonObject.getJSONObject("main");
        JSONObject jsonWind = jsonObject.getJSONObject("wind");

        currentCondition.setCondition(jsonWeather.getString("main"));
        currentCondition.setHumidity(jsonMain.getInt("humidity"));
        currentCondition.setPressure(jsonMain.getInt("pressure"));
        weatherData.setCurrentCondition(currentCondition);

        temperature.setMaxTemp(jsonMain.getDouble("temp_max"));
        temperature.setMinTemp(jsonMain.getDouble("temp_min"));
        temperature.setTemp(jsonMain.getDouble("temp"));
        temperature.setFeelTemp(jsonMain.getDouble("feels_like"));
        weatherData.setTemperature(temperature);

        wind.setSpeed(jsonWind.getDouble("speed"));
        weatherData.setWind(wind);

        return weatherData;
    }

    public static String kelvinToCelsius(double kelvin_value) {
        return "" + Math.round(kelvin_value - 273.15) + " " + temperatureUnit;
    }

    public static String pressurePlusUnit(double pressure) {
        return "" + pressure + " " + pressureUnit;
    }

    public static String windSpeedPlusUnit(double windSpeed) {
        return "" + windSpeed + " " + windSpeedUnit;
    }

    public static String visibilityPlusUnit(int visibility) {
        return "" + visibility + " " + visibilityUnit;
    }
}
