package com.example.lifestyle.utils;

import com.example.lifestyle.data.ProfileData;
import com.example.lifestyle.data.StepData;

import org.json.JSONException;
import org.json.JSONObject;

public class StepUtils {

    public static StepData getStepData(String data) throws JSONException {
        StepData stepData = new StepData();

        JSONObject jsonObject = new JSONObject(data);

        stepData.setStart(jsonObject.getLong("start"));
        stepData.setEnd(jsonObject.getLong("end"));
        stepData.setStep(jsonObject.getInt("step"));

        return stepData;
    }

    public static String storeStepJson(StepData stepData) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("start", stepData.getStart());
        jsonObject.put("end", stepData.getEnd());
        jsonObject.put("step", stepData.getStep());

        return jsonObject.toString();
    }
}
