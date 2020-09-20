package com.example.lifestyle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mButtonSubmit;
    private EditText mEtUsername, mEtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mButtonSubmit = findViewById(R.id.SubmitButtonMain);
        mEtUsername = findViewById(R.id.EditTextUsername);
        mEtPassword = findViewById(R.id.EditTextPassword);

        mButtonSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {

            case R.id.SubmitButtonMain: {

                String username = mEtUsername.getText().toString().trim();
                String password = mEtPassword.getText().toString().trim();

                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                intent.putExtra("ParentActivity", "LoginActivity");
                LoginActivity.this.startActivity(intent);
            }
        }
    }
}