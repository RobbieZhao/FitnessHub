package com.example.lifestyle.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.example.lifestyle.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    protected ImageView mIvProfile;
    protected EditText mEtUsername, mEtAge, mEtCountry, mEtCity, mEtFoot, mEtInch, mEtWeight;
    protected RadioGroup mRgSex;
    protected Button mButtonSubmit;

    protected HashMap<String, String> ProfileData;

    private String MODE;
    private Activity activity;
    protected String directory;

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

        activity = getActivity();
        directory = activity.getFilesDir().getAbsolutePath();

        MODE = getArguments().getString("MODE");
        if (MODE.equals("NEW_USER")) {
            ProfileData = new HashMap<>();
        } else {
            ProfileData = Utils.readData(directory, Utils.data_file);
            displayData();
            disableInputs();
            mButtonSubmit.setText("Edit");
        }

        mIvProfile.setOnClickListener(this);
        mButtonSubmit.setOnClickListener(this);
    }

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

                disableInputs();
                mButtonSubmit.setText("Edit");
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

    protected void startCamera() {
        //The ImageView should open a camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(activity.getPackageManager())!=null){
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == activity.RESULT_OK) {

            //Get the bitmap
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");

            mIvProfile.setImageBitmap(photo);

            //Open a file and write to it
            if(Utils.isExternalStorageWritable()){
                if (Utils.saveImage(photo, directory, Utils.image_file)) {
                    Toast.makeText(activity, "Image saved!", Toast.LENGTH_SHORT).show();
                    ProfileData.put("image", "true");
                }
            } else {
                Toast.makeText(activity,"External storage not writable.",Toast.LENGTH_SHORT).show();
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
        //Set the ImageView
        if (ProfileData.containsKey("image")) {
            Bitmap thumbnailImage = BitmapFactory.decodeFile(directory + "/" + Utils.image_file);
            if (thumbnailImage != null) {
                mIvProfile.setImageBitmap(thumbnailImage);
            }
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
            Toast.makeText(activity,"Username is required!",Toast.LENGTH_SHORT).show();
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
            Toast.makeText(activity, messages[0], Toast.LENGTH_SHORT).show();
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
            Toast.makeText(activity,"Weight can't be 0!",Toast.LENGTH_SHORT).show();
            throw new Exception("Invalid weight");
        }

        if (weight > 0)
            ProfileData.put("weight", Double.toString(weight));
    }
}
