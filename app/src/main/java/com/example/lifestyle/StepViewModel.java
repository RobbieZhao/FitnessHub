package com.example.lifestyle;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lifestyle.data.StepData;

import org.json.JSONException;

import java.util.ArrayList;


public class StepViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<StepData>> jsonData;
    private StepRepository mStepRepository;

    public StepViewModel(Application application){
        super(application);
        mStepRepository = new StepRepository(application);
        jsonData = mStepRepository.getData();
    }

    public LiveData<ArrayList<StepData>> getData(){return jsonData;}


    public void storeStepData(StepData stepData) {
        mStepRepository.storeStepData(stepData);
    }

    public void fetchStepData() {
        mStepRepository.fetchStepData();
    }
}
