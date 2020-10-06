package com.example.lifestyle.utils;

import com.example.lifestyle.data.ProfileData;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONProfileUtils {
    public static ProfileData getprofiledata(String data) throws JSONException{
        ProfileData profiledata = new ProfileData();
        //Start parsing JSON data
        JSONObject jsonObject = new JSONObject(data); //Must throw JSONException
        profiledata.setAge(jsonObject.getInt("age"));
        profiledata.setCity(jsonObject.getString("city"));
        profiledata.setCountry(jsonObject.getString("country"));
        profiledata.setFoot(jsonObject.getInt("foot"));
        profiledata.setInch(jsonObject.getInt("inch"));
        profiledata.setWeight(jsonObject.getInt("weight"));
        profiledata.setUsername(jsonObject.getString("username"));
        return profiledata;
    }
}