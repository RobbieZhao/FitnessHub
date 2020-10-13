package com.example.lifestyle.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.lifestyle.ProfileViewModel;
import com.example.lifestyle.R;
import com.example.lifestyle.data.ProfileData;
import com.example.lifestyle.utils.JSONProfileUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    protected ImageView mIvProfile;
    protected EditText mEtUsername, mEtAge, mEtCountry, mEtCity, mEtFoot, mEtInch, mEtWeight;
    protected RadioGroup mRgSex;
    protected Button mButtonSubmit;

    private String MODE;
    private Activity mActivity;
    protected String directory;

    private ProfileViewModel mProfileViewModel;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mIvProfile = getView().findViewById(R.id.UserImage);
        mEtUsername = getView().findViewById(R.id.EditTextName);
        mEtAge = getView().findViewById(R.id.EditTextAge);
        mRgSex = getView().findViewById(R.id.RgSex);
        mEtCountry = getView().findViewById(R.id.EditTextCountry);
        mEtCity = getView().findViewById(R.id.EditTextTextCity);
        mEtFoot = getView().findViewById(R.id.EditTextHeightFoot);
        mEtInch = getView().findViewById(R.id.EditTextHeightInch);
        mEtWeight = getView().findViewById(R.id.EditTextWeight);
        mButtonSubmit = getView().findViewById(R.id.SubmitButtonUsers);

        mActivity = getActivity();

        //Create the view model
        mProfileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        //Set the observer
        (mProfileViewModel.getData()).observe(getViewLifecycleOwner(), nameObserver);

        MODE = getArguments().getString("MODE");
        if (MODE.equals("NEW_USER")) {

        } else {
            mProfileViewModel.fetchUserData();
            mButtonSubmit.setText("Edit");
        }

        mIvProfile.setOnClickListener(this);
        mButtonSubmit.setOnClickListener(this);
    }

    //create an observer that watches the LiveData<profilerData> object
    final Observer<ProfileData> nameObserver = new Observer<ProfileData>() {
        @Override
        public void onChanged(@Nullable final ProfileData profileData) {
            if(profileData != null){
                mEtUsername.setText(profileData.getUsername());
                if (profileData.getAge() >= 0)
                    mEtAge.setText(String.valueOf(profileData.getAge()));
                mEtCountry.setText(profileData.getCountry());
                mEtCity.setText(profileData.getCity());
                if (profileData.getFoot() >= 0)
                    mEtFoot.setText(String.valueOf(profileData.getFoot()));
                if (profileData.getInch() >= 0)
                    mEtInch.setText(String.valueOf(profileData.getInch()));
                if (profileData.getWeight() >= 0)
                    mEtWeight.setText(String.valueOf(profileData.getWeight()));

                String sex = profileData.getSex();
                if (sex.equals("male")) {
                    mRgSex.check(R.id.Male);
                } else if (sex.equals("female")) {
                    mRgSex.check(R.id.Female);
                }
                disableInputs();
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.UserImage: {
                // In VIEW mode, the camera is disabled
                if (MODE.equals("NEW_USER") || MODE.equals("EDIT"))
                    startCamera();
                break;
            }

            case R.id.SubmitButtonUsers: {
                handleButtonClick();
            }
        }
    }


    private void handleButtonClick() {
        if (MODE.equals("NEW_USER")) {
            try {

                saveInputs();
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (MODE.equals("EDIT")) {
            try {
                saveInputs();
                mProfileViewModel.getData();
                disableInputs();
                mButtonSubmit.setText("Edit");
                MODE = "VIEW";
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Change from VIEW mode to EDIT mode
            MODE = "EDIT";
            mButtonSubmit.setText("Save");
            enableInputs();
        }
    }

    protected void saveInputs() throws Exception {
        String username = getUsername();
        int age = getAge();
        String sex = getSex();
        String country = getCountry();
        String city = getCity();
        int foot = getFoot();
        int inch = getInch();
        double weight = getWeight();

        ProfileData profileData = new ProfileData();
        profileData.setUsername(username);
        profileData.setAge(age);
        profileData.setSex(sex);
        profileData.setCountry(country);
        profileData.setCity(city);
        profileData.setFoot(foot);
        profileData.setInch(inch);
        profileData.setWeight(weight);

        mProfileViewModel.storeUserData(JSONProfileUtils.storeProfileJSON(profileData));
    }
    private String getUsername() throws Exception {
        String username = mEtUsername.getText().toString().trim();
        
        if (username.length() == 0) {
            Toast.makeText(mActivity,"Username is required!",Toast.LENGTH_SHORT).show();
            throw new Exception("Empty username!");
        }

        return username;
    }

    private int getAge() {
        return Utils.getIntegerInput(mEtAge);
    }

    private String getSex() {
        int selectedID = mRgSex.getCheckedRadioButtonId();

        // If those two conditions are false, that means
        // the RadioGroup is not selected.
        if (selectedID == R.id.Male) {
            return "male";
        } else if (selectedID == R.id.Female) {
            return "female";
        } else {
            return "null";
        }
    }

    private String getCountry() {
        return mEtCountry.getText().toString().trim();
    }

    private String getCity() {
        return mEtCity.getText().toString().trim();
    }

    private int getFoot() {
        return Utils.getIntegerInput(mEtFoot);
    }

    private int getInch() {
        return Utils.getIntegerInput(mEtInch);
    }

    private double getWeight() throws Exception {
        double weight = Utils.getDoubleInput(mEtWeight);

        if (weight == 0) {
            Toast.makeText(mActivity,"Weight can't be 0!",Toast.LENGTH_SHORT).show();
            throw new Exception("Invalid weight");
        }

        return weight;
    }

    protected void enableInputs() {
        ArrayList<EditText> editTexts = new ArrayList(
                Arrays.asList(mEtUsername, mEtAge, mEtCountry, mEtCity, mEtFoot, mEtInch, mEtWeight));
        Utils.enableEditText(editTexts);

        Utils.enableRadioGroup(mRgSex);
    }

    protected void disableInputs() {
        ArrayList<EditText> editTexts = new ArrayList(
                Arrays.asList(mEtUsername, mEtAge, mEtCountry, mEtCity, mEtFoot, mEtInch, mEtWeight));
        Utils.disableEditText(editTexts);

        Utils.disableRadioGroup(mRgSex);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == mActivity.RESULT_OK) {

            //Get the bitmap
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");

            mIvProfile.setImageBitmap(photo);

            //Open a file and write to it
            if(Utils.isExternalStorageWritable()){
                if (Utils.saveImage(photo, directory, Utils.image_file)) {
                    Toast.makeText(mActivity, "Image saved!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mActivity,"External storage not writable.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void startCamera() {
        //The ImageView should open a camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(mActivity.getPackageManager())!=null){
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}