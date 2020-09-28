package com.example.lifestyle;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;


public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);

        ProfileFragment fragment = new ProfileFragment();

        Bundle bundle = new Bundle();
        bundle.putString("MODE", "VIEW");
        fragment.setArguments(bundle);

        FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.whole_screen, fragment, "profile_fragment");
        fTrans.commit();

        getSupportActionBar().setTitle("Profile");
    }
}

