package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;


public class UsersActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView mIvProfile;
    private Button mButtonBMI, mButtonSubmit;
    private TextView mTvBMI;
    private EditText mUsername, mEtHeight, mEtWeight;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mIvProfile = findViewById(R.id.imageView);
        mButtonSubmit = findViewById(R.id.submit);
        mUsername = findViewById(R.id.editTextUsername);

        mEtHeight = findViewById(R.id.editTextHeight);
        mEtWeight = findViewById(R.id.editTextWeight);
        mButtonBMI = findViewById(R.id.bmiButton);
        mTvBMI = findViewById(R.id.bmiOutput);

        mIvProfile.setOnClickListener(this);
        mButtonBMI.setOnClickListener(this);
        mButtonSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.imageView: {
                startCamera();
            }

            case R.id.bmiButton: {
                showBMI();
            }

            case R.id.submit: {
                submit();
            }
        }
    }

    private void submit() {
        if (mUsername.getText().toString().trim().length() == 0) {
            Toast.makeText(this,"Username is required!",Toast.LENGTH_SHORT).show();
        }
    }

    // Get the data needed to calculate BMI
    private float getBMIParas(String param) {
        String toastMessage = "Invalid " + param + "!";

        EditText editText;
        if (param == "height") {
            editText = mEtHeight;
        } else {
            editText = mEtWeight;
        }

        float data;
        try {
            data = Float.parseFloat(editText.getText().toString());

            if (data <= 0) {
                Toast.makeText(this,toastMessage, Toast.LENGTH_SHORT).show();
                return -1;
            }

            return data;
        } catch (Exception e) {
            Toast.makeText(this,toastMessage,Toast.LENGTH_SHORT).show();
            return -1;
        }
    }

    private void showBMI() {

        float height = getBMIParas("height");
        float weight = getBMIParas("weight");

        if (height > 0 && weight > 0) {
            float BMI = calculateBMI(height, weight);

            DecimalFormat df = new DecimalFormat("#.#");

            mTvBMI.setText("Your BMI: " + df.format(BMI));
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
        }
    }

    private float calculateBMI(float height, float weight) {
        return (weight / height / height) * 10000;
    }
}

