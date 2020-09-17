package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView mIvProfile;
    private EditText mEtUsername, mEtAge, mEtCountry, mEtCity, mEtFoot, mEtInch, mEtWeight;
    private RadioGroup mRgSex;
    private Button mButtonSubmit;

    private HashMap<String, String> ProfileData = new HashMap<>();
    // userMode is either NEW_USER, VIEW, EDIT
    private String userMode;

    private String directory;
    private String data_file = "userdata.txt";
    private String image_file = "profile.jpg";

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mIvProfile = findViewById(R.id.UserImage);
        mEtUsername = findViewById(R.id.EditTextName);
        mEtAge = findViewById(R.id.EditTextAge);
        mRgSex = findViewById(R.id.RgSex);
        mEtCountry = findViewById(R.id.EditTextCountry);
        mEtCity = findViewById(R.id.EditTextTextCity);
        mEtFoot = findViewById(R.id.EditTextHeightFoot);
        mEtInch = findViewById(R.id.EditTextHeightInch);
        mEtWeight = findViewById(R.id.EditTextWeight);

        mButtonSubmit = findViewById(R.id.SubmitButtonUsers);

        mIvProfile.setOnClickListener(this);
        mButtonSubmit.setOnClickListener(this);

        directory = getFilesDir().getAbsolutePath();

        String parentActivity = getIntent().getStringExtra("ParentActivity");
        if (parentActivity.equals("LoginActivity")) {
            userMode = "NEW_USER";
        } else {
            userMode = "VIEW";
            mButtonSubmit.setText("Edit");
            ProfileData = Helper.readData(directory, data_file);
            displayData();
            disableInputs();
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.UserImage: {
                handleImageClick();
                break;
            }

            case R.id.SubmitButtonUsers: {
                handleButtonClick();
            }
        }
    }

    private void handleImageClick() {
        if (userMode.equals("NEW_USER") || userMode.equals("EDIT")) {
            startCamera();
        }
    }

    private void startCamera() {
        //The ImageView should open a camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //Get the bitmap
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");

            mIvProfile.setImageBitmap(photo);

            //Open a file and write to it
            if(Helper.isExternalStorageWritable()){
                if (Helper.saveImage(photo, directory, image_file)) {
                    Toast.makeText(this, "Image saved!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this,"External storage not writable.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleButtonClick() {
        if (userMode.equals("NEW_USER") || userMode.equals("EDIT")) {
            try {
                saveInputs();

                Intent myIntent = new Intent(ProfileActivity.this, HomeActivity.class);
                ProfileActivity.this.startActivity(myIntent);
            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            }
        } else if (userMode.equals("VIEW")){
            userMode = "EDIT";
            mButtonSubmit.setText("Save");
            enableInputs();
        }
    }

    private void saveInputs() throws Exception {
        collectUsername();
        collectAge();
        collectSex();
        collectLocation();
        collectHeight();
        collectWeight();

        if (!Helper.storeData(ProfileData, directory, data_file))
            throw new Exception("File not saved");
    }

    private void enableInputs() {
        ArrayList<EditText> editTexts = new ArrayList(
                Arrays.asList(mEtUsername, mEtAge, mEtCountry, mEtCity, mEtFoot, mEtInch, mEtWeight));
        Helper.enableEditText(editTexts);

        Helper.enableRadioGroup(mRgSex);
    }

    private void displayData() {
        //Set the ImageView
        Bitmap thumbnailImage = BitmapFactory.decodeFile(directory + "/" + image_file);
        if(thumbnailImage!=null){
            mIvProfile.setImageBitmap(thumbnailImage);
        }

        // Set EditTexts
        mEtUsername.setText(getValueFromMap(ProfileData, "username"));
        mEtAge.setText(getValueFromMap(ProfileData, "age"));
        mEtCountry.setText(getValueFromMap(ProfileData, "country"));
        mEtCity.setText(getValueFromMap(ProfileData, "city"));
        mEtFoot.setText(getValueFromMap(ProfileData, "foot"));
        mEtInch.setText(getValueFromMap(ProfileData, "inch"));
        mEtWeight.setText(getValueFromMap(ProfileData, "weight"));

        // Set RadioGroup
        String sex = getValueFromMap(ProfileData, "sex");
        if (sex.equals("male")) {
            mRgSex.check(R.id.Male);
        } else {
            mRgSex.check(R.id.Female);
        }
    }

    private void disableInputs() {
        ArrayList<EditText> editTexts = new ArrayList(
                Arrays.asList(mEtUsername, mEtAge, mEtCountry, mEtCity, mEtFoot, mEtInch, mEtWeight));
        Helper.disableEditText(editTexts);

        Helper.disableRadioGroup(mRgSex);
    }

    private String getValueFromMap(HashMap<String, String> hashMap, String key) {
        if (hashMap.containsKey(key)) {
            return hashMap.get(key);
        }

        return "";
    }

    private void collectUsername() throws Exception {
        String username = mEtUsername.getText().toString().trim();
        if (username.length() == 0) {
            Toast.makeText(this,"Username is required!",Toast.LENGTH_SHORT).show();
            throw new Exception("Empty username!");
        }
        ProfileData.put("username", username);
    }

    private void collectAge() {
        int age = Helper.getIntegerInput(mEtAge);

        if (age > 0)
            ProfileData.put("age", Integer.toString(age));
    }

    private void collectSex() {
        int selectedID = mRgSex.getCheckedRadioButtonId();

        // If those two conditions are false, that means
        // the RadioGroup is not selected.
        if (selectedID == R.id.Male) {
            ProfileData.put("sex", "male");
        } else if (selectedID == R.id.Female) {
            ProfileData.put("sex", "female");
        }
    }

    private void collectLocation() {
        String country = mEtCountry.getText().toString().trim();
        if (!country.isEmpty())
            ProfileData.put("country", country);

        String city = mEtCity.getText().toString().trim();
        if (!city.isEmpty())
            ProfileData.put("city", city);
    }

    private void collectHeight() throws Exception {
        int foot = Helper.getIntegerInput(mEtFoot);
        int inch = Helper.getIntegerInput(mEtInch);

        if (inch >= 13) {
            Toast.makeText(this,"Inch must be 0-11!",Toast.LENGTH_SHORT).show();
            throw new Exception("Invalid inches");
        } else if (foot == 0 && inch == 0) {
            Toast.makeText(this,"Height can't be 0!",Toast.LENGTH_SHORT).show();
            throw new Exception("Invalid height");
        } else if (foot == -1 && inch >= 0) {
            Toast.makeText(this,"Please enter foot!",Toast.LENGTH_SHORT).show();
            throw new Exception("Empty foot");
        } else if (foot >= 0 && inch == -1) {
            Toast.makeText(this,"Please enter inch!",Toast.LENGTH_SHORT).show();
            throw new Exception("Empty inch");
        }

        ProfileData.put("foot", Integer.toString(foot));
        ProfileData.put("inch", Integer.toString(inch));
    }

    private void collectWeight() throws Exception {
        float weight = Helper.getFloatInput(mEtWeight);

        if (weight == 0) {
            Toast.makeText(this,"Weight can't be 0!",Toast.LENGTH_SHORT).show();
            throw new Exception("Invalid weight");
        }

        if (weight > 0)
            ProfileData.put("weight", Float.toString(weight));
    }
}

