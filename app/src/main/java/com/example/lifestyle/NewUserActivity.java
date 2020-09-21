package com.example.lifestyle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;

public class NewUserActivity extends ProfileBaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setUp();

        ProfileData = new HashMap<>();

        mIvProfile.setOnClickListener(this);
        mButtonSubmit.setOnClickListener(this);

        directory = getFilesDir().getAbsolutePath();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.UserImage: {
                startCamera();
                break;
            }

            case R.id.SubmitButtonUsers: {
                goToHomeActivity();
            }
        }
    }

    private void goToHomeActivity() {
        try {
            saveInputs();

            Intent myIntent = new Intent(NewUserActivity.this, HomeActivity.class);
            NewUserActivity.this.startActivity(myIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}