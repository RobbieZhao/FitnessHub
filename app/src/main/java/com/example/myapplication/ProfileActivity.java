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
            readData();
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
            if(isExternalStorageWritable()){
                saveImage(photo);
            } else{
                Toast.makeText(this,"External storage not writable.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void saveImage(Bitmap finalBitmap) {
        File file = new File(directory, image_file);

        if (file.exists()) file.delete ();

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Toast.makeText(this,"file saved!",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
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

        storeData();
    }

    private void storeData() throws IOException {
        File file = new File(directory, data_file);

        if (file.exists()) file.delete ();

        FileOutputStream fileOut = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(ProfileData);
        out.close();
        fileOut.close();
    }

    private void enableInputs() {
        ArrayList<EditText> editTexts = new ArrayList(
                Arrays.asList(mEtUsername, mEtAge, mEtCountry, mEtCity, mEtFoot, mEtInch, mEtWeight));
        Helper.enableEditText(editTexts);

        Helper.enableRadioGroup(mRgSex);
    }

    private void readData() {
        try {
            File file = new File(directory, data_file);

            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            ProfileData = (HashMap<String, String>) in.readObject();
            in.close();
            fileIn.close();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
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

    private void collectAge() throws Exception {
        collectInteger(mEtAge, "age");
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
        collectString(mEtCountry, "country");
        collectString(mEtCity, "city");
    }

    private void collectHeight() throws Exception {
        collectInteger(mEtFoot, "foot");
        collectInteger(mEtInch, "inch");
    }

    private void collectWeight() throws Exception {
        collectFloat(mEtWeight, "weight");
    }

    private void collectInteger(EditText editText, String name) throws Exception {
        String str = editText.getText().toString().trim();

        if (!str.isEmpty()) {
            String errorMessage = "Invalid " + name + "!";

            int age;
            try {
                age = Integer.parseInt(str);

                if (age < 0) {
                    throw new Exception(errorMessage);
                }
            } catch (Exception e) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                throw new Exception(errorMessage);
            }

            ProfileData.put(name, Integer.toString(age));
        }
    }

    private void collectFloat(EditText editText, String name) throws Exception {
        String str = editText.getText().toString().trim();

        if (!str.isEmpty()) {
            String errorMessage = "Invalid " + name + "!";

            float age;
            try {
                age = Float.parseFloat(str);

                if (age < 0) {
                    throw new Exception(errorMessage);
                }
            } catch (Exception e) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                throw new Exception(errorMessage);
            }

            ProfileData.put(name, Float.toString(age));
        }
    }

    private void collectString(EditText editText, String name) {
        String str = editText.getText().toString().trim();

        if (!str.isEmpty()) {
            ProfileData.put(name, str);
        }
    }
}

