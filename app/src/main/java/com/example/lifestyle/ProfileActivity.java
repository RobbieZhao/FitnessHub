package com.example.lifestyle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView mIvProfile;
    private EditText mEtUsername, mEtAge, mEtCountry, mEtCity, mEtFoot, mEtInch, mEtWeight;
    private RadioGroup mRgSex;
    private Button mButtonSubmit;

    private HashMap<String, String> ProfileData;
    // userMode is either NEW_USER, VIEW, EDIT
    private String userMode;

    private String directory;

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
            ProfileData = new HashMap<>();
        } else {
            userMode = "VIEW";
            mButtonSubmit.setText("Edit");
            ProfileData = Utils.readData(directory, Utils.data_file);
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
            if(Utils.isExternalStorageWritable()){
                if (Utils.saveImage(photo, directory, Utils.image_file)) {
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
                e.printStackTrace();
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

        if (!Utils.storeData(ProfileData, directory, Utils.data_file))
            throw new Exception("File not saved");
    }

    private void enableInputs() {
        ArrayList<EditText> editTexts = new ArrayList(
                Arrays.asList(mEtUsername, mEtAge, mEtCountry, mEtCity, mEtFoot, mEtInch, mEtWeight));
        Utils.enableEditText(editTexts);

        Utils.enableRadioGroup(mRgSex);
    }

    private void displayData() {
        //Set the ImageView
        Bitmap thumbnailImage = BitmapFactory.decodeFile(directory + "/" + Utils.image_file);
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
        } else if (sex.equals("female")) {
            mRgSex.check(R.id.Female);
        }
    }

    private void disableInputs() {
        ArrayList<EditText> editTexts = new ArrayList(
                Arrays.asList(mEtUsername, mEtAge, mEtCountry, mEtCity, mEtFoot, mEtInch, mEtWeight));
        Utils.disableEditText(editTexts);

        Utils.disableRadioGroup(mRgSex);
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
        int age = Utils.getIntegerInput(mEtAge);

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
        int foot = Utils.getIntegerInput(mEtFoot);
        int inch = Utils.getIntegerInput(mEtInch);

        String[] messages = Utils.checkHeightInput(foot, inch, false);
        if (!messages[0].isEmpty() && !messages[1].isEmpty()) {
            Toast.makeText(this, messages[0], Toast.LENGTH_SHORT).show();
            throw new Exception(messages[1]);
        }

        if (foot != -1 && inch != -1) {
            ProfileData.put("foot", Integer.toString(foot));
            ProfileData.put("inch", Integer.toString(inch));
        }
    }

    private void collectWeight() throws Exception {
        double weight = Utils.getDoubleInput(mEtWeight);

        if (weight == 0) {
            Toast.makeText(this,"Weight can't be 0!",Toast.LENGTH_SHORT).show();
            throw new Exception("Invalid weight");
        }

        if (weight > 0)
            ProfileData.put("weight", Double.toString(weight));
    }
}

