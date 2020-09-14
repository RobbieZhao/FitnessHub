package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mButtonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mButtonSubmit = findViewById(R.id.SubmitButtonMain);

        mButtonSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {

            case R.id.SubmitButtonMain: {
                Intent myIntent = new Intent(MainActivity.this, UsersActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        }
    }
}