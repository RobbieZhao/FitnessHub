package com.example.lifestyle.utils;

import com.example.lifestyle.data.ProfileData;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONProfileUtils {

    public static ProfileData getprofiledata(String data) throws JSONException {

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

    public static String storeProfileJSON(ProfileData profileData) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", profileData.getUsername());
        jsonObject.put("age", profileData.getAge()+"");
        jsonObject.put("city", profileData.getCity());
        jsonObject.put("country", profileData.getCountry());
        jsonObject.put("weight", profileData.getWeight()+"");
        jsonObject.put("foot", profileData.getFoot()+"");
        jsonObject.put("inch",profileData.getInch()+"");

        return jsonObject.toString();

    }

}