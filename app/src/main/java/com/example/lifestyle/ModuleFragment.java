package com.example.lifestyle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class ModuleFragment extends Fragment implements View.OnClickListener {
    private Button mButtonCalculator, mButtonWeather, mButtonMap, mButtonProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.module_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mButtonCalculator = getView().findViewById(R.id.calculator_module);
        mButtonWeather = getView().findViewById(R.id.weather_module);
        mButtonMap = getView().findViewById(R.id.map_module);
        mButtonProfile = getView().findViewById(R.id.profile_module);

        mButtonCalculator.setOnClickListener(this);
        mButtonWeather.setOnClickListener(this);
        mButtonMap.setOnClickListener(this);
        mButtonProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.calculator_module: {
                ((HomeActivity) getActivity()).openCalculator();
                break;
            }

            case R.id.weather_module: {
                ((HomeActivity) getActivity()).openWeather();
                break;
            }

            case R.id.map_module: {
                ((HomeActivity) getActivity()).openMap();
                break;
            }

            case R.id.profile_module: {
                ((HomeActivity) getActivity()).openProfile();
            }
        }
    }

    private void openCalculator() {
    }

    private void openWeather() {

    }

    private void openMap() {

    }

    private void openProfile() {

    }
}

