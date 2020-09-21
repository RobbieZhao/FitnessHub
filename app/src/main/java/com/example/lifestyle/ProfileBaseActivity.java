package com.example.lifestyle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class ProfileBaseActivity extends AppCompatActivity{

    protected ImageView mIvProfile;
    protected EditText mEtUsername, mEtAge, mEtCountry, mEtCity, mEtFoot, mEtInch, mEtWeight;
    protected RadioGroup mRgSex;
    protected Button mButtonSubmit;

    protected HashMap<String, String> ProfileData;

    protected String directory;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setUp() {
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

        directory = getFilesDir().getAbsolutePath();
    }

    protected void startCamera() {
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

    protected void saveInputs() throws Exception {
        collectUsername();
        collectAge();
        collectSex();
        collectLocation();
        collectHeight();
        collectWeight();

        if (!Utils.storeData(ProfileData, directory, Utils.data_file))
            throw new Exception("File not saved");
    }

    protected void enableInputs() {
        ArrayList<EditText> editTexts = new ArrayList(
                Arrays.asList(mEtUsername, mEtAge, mEtCountry, mEtCity, mEtFoot, mEtInch, mEtWeight));
        Utils.enableEditText(editTexts);

        Utils.enableRadioGroup(mRgSex);
    }

    protected void displayData() {
        ProfileData = Utils.readData(directory, Utils.data_file);

        //Set the ImageView
        Bitmap thumbnailImage = BitmapFactory.decodeFile(directory + "/" + Utils.image_file);
        if(thumbnailImage!=null){
            mIvProfile.setImageBitmap(thumbnailImage);
        }

        // Set EditTexts
        readFromMap(mEtUsername, "username");
        readFromMap(mEtAge, "age");
        readFromMap(mEtCountry, "country");
        readFromMap(mEtCity, "city");
        readFromMap(mEtFoot, "foot");
        readFromMap(mEtInch, "inch");
        readFromMap(mEtWeight, "weight");

        // Set RadioGroup
        if (ProfileData.containsKey("sex")) {
            String sex = ProfileData.get("sex");
            if (sex.equals("male")) {
                mRgSex.check(R.id.Male);
            } else if (sex.equals("female")) {
                mRgSex.check(R.id.Female);
            }
        }
    }

    protected void disableInputs() {
        ArrayList<EditText> editTexts = new ArrayList(
                Arrays.asList(mEtUsername, mEtAge, mEtCountry, mEtCity, mEtFoot, mEtInch, mEtWeight));
        Utils.disableEditText(editTexts);

        Utils.disableRadioGroup(mRgSex);
    }

    private void readFromMap(EditText editText, String key) {
        if (ProfileData.containsKey(key))
            editText.setText(ProfileData.get(key));
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

