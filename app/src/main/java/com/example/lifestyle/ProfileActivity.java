package com.example.lifestyle;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class ProfileActivity extends ProfileBaseActivity implements View.OnClickListener {

    // VIEW or EDIT
    private static String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setUp();

        mIvProfile.setOnClickListener(this);
        mButtonSubmit.setOnClickListener(this);

        disableInputs();
        displayData();
        mButtonSubmit.setText("Edit");
        mode = "VIEW";
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.UserImage: {
                if (mode.equals("EDIT"))
                    startCamera();
                break;
            }

            case R.id.SubmitButtonUsers: {
                handleButtonClick();
            }
        }
    }

    private void handleButtonClick() {
        if (mode.equals("EDIT")) {
            try {
                saveInputs();

                Intent myIntent = new Intent(ProfileActivity.this, HomeActivity.class);
                ProfileActivity.this.startActivity(myIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Change from VIEW mode to EDIT mode
            mode = "EDIT";
            mButtonSubmit.setText("Save");
            enableInputs();
        }
    }
}

